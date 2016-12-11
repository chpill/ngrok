(def project 'com.chpill.ngrok)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"src"}
          :source-paths   #{"src"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [http-kit "2.2.0"]
                            [org.clojure/core.async "0.2.395"]
                            [cheshire "5.6.3"]

                            [boot/core "RELEASE" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/com.chpill.ngrok"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]]
         '[com.chpill.ngrok])
