(ns avisi.rsync.core-test
  (:require [clojure.test :refer :all]
            [avisi.rsync.core :as t]))

(deftest contains-path?
  (testing "file existince in file set"
    (testing "exists"
      (is (= true (t/contains-path? "folder1/__init__.py"
                                  [{:path "folder1/__init__.py", :md5 "d41d8cd98f00b204e9800998ecf8427e"} {:path "folder1/channel.py", :md5 "331f28d96d11c40e3ba905a9707fc737"} {:path "folder1/device.py", :md5 "e911946ce224ffc06a1f7af2135c032d"} {:path "folder1/last_state.py", :md5 "f542731a7719cb5c2675aca9f130ccdc"} {:path "folder1/node.py", :md5 "7e3f2deb1b0d83a4088148a7dd761491"} {:path "folder1/thing.py", :md5 "baa63d690251ff74865840ee1870e9c0"}]))) 
      (is (= true (t/contains-path? "a" [{:path "a" :md5 "check"}])))
      (is (= true (t/contains-path? "some/long/path" [{:path "some/long/path" :md5 "check"}])))
      (is (= true (t/contains-path? "a" [{:path "aa" :md5 "check"}{:path "a" :md5 "check"}]))))
    (testing "does not exist"
      (is (= false (t/contains-path? "folder1/init.py"
                                    [{:path "folder1/__init__.py", :md5 "d41d8cd98f00b204e9800998ecf8427e"} {:path "folder1/channel.py", :md5 "331f28d96d11c40e3ba905a9707fc737"} {:path "folder1/device.py", :md5 "e911946ce224ffc06a1f7af2135c032d"} {:path "folder1/last_state.py", :md5 "f542731a7719cb5c2675aca9f130ccdc"} {:path "folder1/node.py", :md5 "7e3f2deb1b0d83a4088148a7dd761491"} {:path "folder1/thing.py", :md5 "baa63d690251ff74865840ee1870e9c0"}]))) 
      (is (= false (t/contains-path? "b" [{:path "a" :md5 "check"}]))))
    (testing "nil"
      (is (= false (t/contains-path? nil [{:path "a" :md5 "check"}]))))))



