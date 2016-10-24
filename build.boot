(def project 'avisi/rsync)
(def version "0.1.0-SNAPSHOT")

(set-env! :resource-paths #{"resources" "src"}
          :source-paths   #{"test"}
          :dependencies   '[[org.clojure/clojure "1.9.0-alpha11"]
                            [pandect "0.6.0"]
                            [com.amazonaws/aws-java-sdk-s3 "1.10.61"]
                            [amazonica "0.3.76" :exclusions [org.clojure/clojure]]
                            [org.clojure/test.check "0.9.0" :scope "test"]
                            [org.clojure/tools.logging "0.3.1"]
                            [adzerk/boot-test "RELEASE" :scope "test"]])

(task-options!
 pom {:project     project
      :version     version
      :description "FIXME: write description"
      :url         "http://example/FIXME"
      :scm         {:url "https://github.com/yourname/aws-s3-sync"}
      :license     {"Eclipse Public License"
                    "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build
  "Build and install the project locally."
  []
  (comp (pom) (jar) (install)))

(require '[adzerk.boot-test :refer [test]])
