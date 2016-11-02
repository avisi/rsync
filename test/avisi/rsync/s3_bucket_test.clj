(ns avisi.rsync.s3-bucket-test
  (:require [avisi.rsync.s3-bucket :as sut]
            [avisi.rsync.s3-bucket-spec :as sut-spec]
            [clojure.spec.test :as stest]
            [clojure.test :refer :all]))

(deftest bucket-name
  (testing "bucket name"
    (is (= "bucket" (sut/s3-url->bucket-name "s3://bucket")))))

(deftest bucket-key
  (testing "bucket key"
    (is (true? (get-in (first (stest/check `sut/s3-url->key)) [:clojure.spec.test.check/ret :result])))))
