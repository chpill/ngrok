(def project 'com.chpill/ngrok)
(def +version+ "0.1.0")

(set-env! :resource-paths #{"src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "RELEASE"]
                            [http-kit "2.2.0"]
                            [org.clojure/core.async "0.2.395"]
                            [cheshire "5.6.3"]

                            [boot/core "RELEASE" :scope "test"]
                            [adzerk/boot-test "RELEASE" :scope "test"]
                            [adzerk/bootlaces "0.1.13" :scope "test"]])

(task-options!
 pom {:project     project
      :version     +version+
      :description "A simple wrapper for the amazing ngrok cli"
      :url         "https://github.com/chpill/ngrok"
      :scm         {:url "https://github.com/chpill/ngrok"}
      :license     {"BSD-3-Clause" "https://opensource.org/licenses/BSD-3-Clause"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))


(require '[adzerk.bootlaces :refer :all])
(bootlaces! +version+)

push-release

