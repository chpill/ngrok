(def project 'com.chpill/ngrok)
(def version "0.1.0")

(set-env! :resource-paths #{"src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [http-kit "2.2.0"]
                            [org.clojure/core.async "0.2.395"]
                            [cheshire "5.6.3"]

                            [boot/core "RELEASE" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "A simple wrapper for the amazing ngrok cli"
      :url         "https://github.com/chpill/ngrok"
      :scm         {:url "https://github.com/chpill/ngrok"}
      :license     {"BSD-3-Clause" "https://opensource.org/licenses/BSD-3-Clause"}})

(deftask build-install
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

;; Copied from bootlaces
;; https://github.com/adzerk-oss/bootlaces/blob/master/src/adzerk/bootlaces.clj#L39-L54
(deftask authorize
  "Collect CLOJARS_USER and CLOJARS_PASS from the user if they're not set."
  []
  (fn [next-handler]
    (fn [fileset]
      (let [[user pass] (get-creds), clojars-creds (atom {})]
        (if (and user pass)
          (swap! clojars-creds assoc :username user :password pass)
          (do (println "CLOJARS_USER and CLOJARS_PASS were not set; please enter your Clojars credentials.")
              (print "Username: ")
              (#(swap! clojars-creds assoc :username %) (read-line))
              (print "Password: ")
              (#(swap! clojars-creds assoc :password %)
               (apply str (.readPassword (System/console))))))
        (merge-env! :repositories [["deploy-clojars" (merge @clojars-creds {:url "https://clojars.org/repo"})]])
        (next-handler fileset)))))

(deftask publish
  "Build and install the project locally."
  []
  (comp (pom)
        (jar)
        (authorize)
        (push)))
