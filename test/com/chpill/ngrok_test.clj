(ns com.chpill.ngrok-test
  (:require [com.chpill.ngrok :refer :all]
            [clojure.test :refer :all]))

(set! *warn-on-reflection* true)

(deftest integration-with-ngrok
  (let [stop-ngrok (start {:port 1337
                           :region "eu"
                           :bin-path "/home/chpill/p/buf/ngrok/ngrok"})
        property (System/getProperty "ngrok-tunnel-url")]
    (testing "sets the tunnel url in a system property"
      (is (some? property))
      (is (re-find #"eu.ngrok.io" property)))

    ;; stop the process at the end
    (stop-ngrok)

    (testing "resets system property on stop"
      (is (nil? (System/getProperty "ngrok-tunnel-url"))))))

