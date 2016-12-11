(ns com.chpill.ngrok
  (:require [clojure.java.io :as java-io]
            [cheshire.core :as cheshire]
            [org.httpkit.client :as hk-cli]
            [clojure.core.async :as async]))

(def prop-name "ngrok-tunnel-url")
(def re-server-addr #"starting web service.*addr=(\d+\.\d+\.\d+\.\d+:\d+)")

(defn open-tunnel-with-retries [{:keys [port region proto retries tunnel-url] :as options}]
  (println "trying to open tunnel (retries left: " retries ")" )
  (if (= -1 retries)
    (throw (Exception. "[ngrok] Reached max retry count, failed to open tunnel"))
    (let [response @(hk-cli/request {:method :post
                                     ;; the request will be on localhost
                                     :timeout 1000
                                     :url tunnel-url
                                     :headers {"Content-type" "application/json"}
                                     :body (cheshire/encode {:addr port
                                                             :name (gensym)
                                                             :proto proto
                                                             :region region})})]
      (case (:status response)
        400 (throw (Exception. (cheshire/decode (:body response))))

        500 (do (Thread/sleep 100)
                (recur (assoc options :retries (dec retries))))
        502 (do (Thread/sleep 100)
                (recur (assoc options :retries (dec retries))))
        201 (do (println "201!!")
                (get (cheshire/decode (:body response))
                     "public_url"))))))

(defn tunnel-url [server-addr]
  (str "http://" server-addr "/api/tunnels"))

(defn start-process [& args]
  (.start (ProcessBuilder. (into-array String args))))

(defn start
  "Starts an outer ngrok process and use it to open a tunnel. Blocks the thread
  until the process is created and the tunnel opened.
  You may want to supply a reg

  By default, it will try to launch a `ngrok` bin on your `PATH`. You can supply
  a custom path for the bin to run with the optional `:bin-path`.

  The tunnel url is provided as the system property `ngrok-tunnel-url`.

  Returns a function thats stops the ngrok process."
  [{:keys [region port proto bin-path]
                    :or {region "eu"
                         port 80
                         proto "http"
                         bin-path "ngrok"}}]

  (let [proc (start-process bin-path
                            "start"
                            "--none"
                            "--log=stdout"
                            (str "--region=" region))
        tunnel-chan (async/chan)]
    (println "ngrok process started")
    (println "start reading its stdout")

    ;; Destroying the proc will cause the doseq to end, thus freeing the reader
    (future (with-open [reader (java-io/reader (.getInputStream proc))]
              (doseq [line (line-seq reader)]
                (println "[ngrok]: " line)
                (when-let [server-addr
                           (last
                            (re-find re-server-addr
                                     line))]
                  (do (println "found ngrok webserver launching statement")
                      (println "server url:" server-addr)
                      (println
                       "Wait 0.2s for the ngrok web service to be up then start trying")
                      (Thread/sleep 200)
                      (let [public-url
                            (open-tunnel-with-retries {:port port
                                                       :region region
                                                       :proto proto
                                                       :tunnel-url (tunnel-url server-addr)
                                                       :retries 20})]
                        (System/setProperty prop-name public-url)
                        (async/>!! tunnel-chan :ok)
                        (println "tunnel opened :" public-url)))))))

    ;; TODO:
    ;; There as to be a simple way to block like that without core.async...
    (async/<!! tunnel-chan)

    ;; Return a stop function
    (fn []
      (println "stopping the ngrok process")
      (System/clearProperty prop-name)
      (.destroy proc))))

(defn destroy [proc] (.destroy proc))

(defn start-long-running
  "Starts a ngrok process and shut it down automatically when the JVM stops"
  [options]
  (let [stop-ngrok (start options)]
    (println "[ngrok]: started!")

    (.addShutdownHook (Runtime/getRuntime)
                      (Thread. #(do (println "[ngrok]: stopping!")
                                    (stop-ngrok))))))
