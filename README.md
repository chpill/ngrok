# ngrok Clojure wrapper

This is a small clojure library and its accompanying boot task to use [ngrok](ngrok.io).

It can open a ngrok tunnel, and it will expose the public url of this tunnel as
a JVM property. You can access it like this `(System/getProperty
"ngrok-tunnel-url")`, or even better, if you use the [aero (FIXME find url)]()
you can read it from your configuration file using the reader `#prop
ngrok-tunnel-url`.

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

## Why?

Ngrok is a wonderful utility to test integration with external services locally
on your development environment. In particular, This wrapper was conceived while
integrating trello webhooks with a clojure app, and greatly simplified the
development workflow.

## How?

This wrapper starts a ngrok process outside of the JVM, then communicate with
its HTTP API to open the requested tunnel.

## Skeletons

There are various points that needs to be adressed to get this software out of
alpha state:

* Get the concurrency right: For now, calling `start` will block the user
  thread, but it would be better provide more options to the user (like the
  http-kit client does maybe). Furthermore, There is a dependency on
  `core.async` we should get rid of for a wrapper this thin.
* 

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
