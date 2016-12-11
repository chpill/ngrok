(ns com.chpill.ngrok.boot
  (:require [boot.core :as boot]))

(boot/deftask ngrok
  [p port PORT int "port to which the tunnel will send traffic (default: 80)"
   b bin-path BIN_PATH str "custom path to the ngrok bin"
   m protocol PROTOCOL str "protocol (default: http)"
   r region REGION str "region (default: eu)"]

  (ngrok/start-long-running *opts*)

  (fn [next-task] (fn [fileset] (next-task fileset))))
