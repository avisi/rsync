(ns avisi.rsync.core
  (:require [avisi.rsync.location :as l]
            [avisi.rsync.local-dir :as dir]
            [avisi.rsync.s3-bucket :as s3]
            [amazonica.aws.s3 :as aws-s3]
            [clojure.tools.logging :as log]
            [clojure.data :as data]
            [clojure.string :as str]))

(defn contains-path?
 [path coll]
  (true? (some #(= path (:path %)) coll)))

(defmulti location (fn [url] (keyword (first (str/split url #"://")))))
(defmethod location :s3
  [url]
  (s3/new-s3-location (s3/s3-url->bucket-name url) (s3/s3-url->key url)))
(defmethod location :file
  [url]
  (dir/new-directory-location (dir/file-url->file-name url)))

(defn left-to-right
  [paths left-location right-location]
  (doseq [path paths]
    (log/debug "copying file" path)
    (with-open [input-stream (l/read left-location path)]
      (l/write right-location path input-stream))))

(defn delete
  [paths location]
  (doseq [path paths]
    (l/delete location path)))

(defn dry-run?
 [options]
 (true? (:dry-run options)))

(defn sync!
  "sync two folders, one or both possibly being remote"
  [from-url to-url options]
  (let [from-location (location from-url)
        to-location (location to-url)
        _ (log/debug "analysing from location")
        from-set (l/analyse from-location)
        _ (log/debug "analysing to location")
        to-set (l/analyse to-location)
        _ (log/debug "diffing results")
        diff (data/diff from-set to-set)
        to-be-deleted (filter #(not (contains-path? (:path %) (first diff))) (second diff))
        to-be-copied (filter #(not (contains-path? (:path %) (second diff))) (first diff))
        to-be-updated (filter #(contains-path? (:path %) (second diff)) (first diff))]
    (if (not (dry-run? options))
      (do
        (log/debug "copying new files")
        (left-to-right to-be-copied from-location to-location)
        (log/debug "updating existing files")
        (left-to-right to-be-updated from-location to-location)
        (log/debug "deleting redundant files")
        (delete to-be-deleted to-location)))
    {:deleted to-be-deleted
     :copied to-be-copied
     :updated to-be-updated}))
