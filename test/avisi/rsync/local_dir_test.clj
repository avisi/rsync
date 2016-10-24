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
          location (dir/new-directory-location (.toString file))]
      (l/write location "test" (io/input-stream (.getBytes "text")))
      (let [analysis (l/analyse location)]
        (is (s/valid? ::location-spec/analysis analysis)))
      (is (= "text" (with-open [input-stream (l/read location "test")] (slurp input-stream)))))))

(deftest read-write
  (testing "write / read back"
    (let [file (java.nio.file.Files/createTempDirectory "test" (into-array java.nio.file.attribute.FileAttribute []))
          location (dir/new-directory-location (.toString file))]
      (l/write location "test" (io/input-stream (.getBytes "text")))
      (is (= "text" (with-open [input-stream (l/read location "test")] (slurp input-stream)))))))
