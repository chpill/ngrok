# ngrok Clojure wrapper

This is a small clojure library and its accompanying boot task to use [ngrok](ngrok.io).

You must provide a `ngrok` binary available on your `PATH`, or otherwise
available on you filesystem (see the `:bin-path` option for more informations).

## WARNING

**This is alpha software!** The API will break (and hopefully improve) in future
versions. Upgrade with caution.

## Basic usage

Require it in one of your namespaces `(require '[com.chpill.ngrok :as ngrok])`.

Open a http tunnel `(def stop-ngrok-process (ngrok/start {:port 80 :protocol
"http" :region "eu"}))`

Note that the `start` returns a function that stops the ngrok process.

Option list:

* `:port` (default `80`)
* `:protocol` (default `"http"`)
* `:region` (default `"eu"`)
* `:bin-path` an optional path to you ngrok binary if you don't have it on your
  `PATH` (or do not want to use the one on your path)

A convenience function is provided if you want the process to be stopped
automatically on JVM shutdown `start-long-running`.

A boot task is provided if you want to use `start-long-running` along with your other boot tasks:

```
(require '[com.chpill.ngrok.boot :refer [boot-ngrok]])

(deftask dev []
  (comp ...
        (boot-ngrok)
        ...))

```

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
