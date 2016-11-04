(ns avisi.rsync.local-dir-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [avisi.rsync.location :as l]
            [avisi.rsync.local-dir :as dir]
            [avisi.rsync.location-spec :as location-spec]
            [clojure.spec :as s]))

(deftest analyse
  (testing "analyse"
    (let [file (java.nio.file.Files/createTempDirectory "test" (into-array java.nio.file.attribute.FileAttribute []))
          location (dir/new-directory-location (.toString file))
          test-file {:path "test"}
          test-content "text"]
      (l/write location test-file (io/input-stream (.getBytes test-content)))
      (let [analysis (l/analyse location)]
        (is (s/valid? ::location-spec/analysis analysis)))
      (is (= test-content (with-open [input-stream (l/read location test-file)] (slurp input-stream)))))))

(deftest read-write
  (testing "write / read back"
    (let [file (java.nio.file.Files/createTempDirectory "test" (into-array java.nio.file.attribute.FileAttribute []))
          location (dir/new-directory-location (.toString file))
          test-file {:path "test"}
          test-content "text"]
      (l/write location test-file (io/input-stream (.getBytes test-content)))
      (is (= test-content (with-open [input-stream (l/read location test-file)] (slurp input-stream)))))))
