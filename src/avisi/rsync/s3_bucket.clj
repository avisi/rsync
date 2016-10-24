(ns avisi.rsync.s3-bucket
  (:require [avisi.rsync.location :as l]
            [amazonica.aws.s3 :as s3]
            [clojure.tools.logging :as log]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(declare s3url->bucket-name)
(declare s3url->key)

(defn list-all-objects
  "Pass a request map with :bucket-name and possibly a :prefix"
  [request]
  (let [response (s3/list-objects request)
        next-request (assoc request :marker (:next-marker response))]
    (concat (:object-summaries response) (if (:truncated? response)
                                           (lazy-seq (list-all-objects next-request))
                                           []))))

(defn analyse-s3-bucket
  "Pass a request map with :bucket-name and possibly a :prefix"
  [request]
  (let [paths (->> (list-all-objects request)
                   (filter #(not (str/ends-with? (:key %) "/"))) ;; filter folders
                   (map #(select-keys % [:key :etag]))
                   (map #(clojure.set/rename-keys % {:key :path :etag :md5})))]
    (if (str/blank? (:prefix request))
      (set paths)
      (set (map #(assoc-in % [:path] (subs (:path %) (inc (count (:prefix request))))) paths)))))

(defn delete
  [bucket key]
  (s3/delete-object bucket key))

(defn write
  [bucket key stream]
  (s3/put-object :bucket-name bucket :key key :input-stream stream))

(defn read
  [bucket key]
  (:input-stream (s3/get-object :bucket-name bucket :key key)))

(defn s3-url->bucket-name
 [url]
  (as-> (second (str/split url #"://")) v
    (first (str/split v #"/"))))

(defn s3-url->key
 [url]
  (as-> (second (str/split url #"://")) v
    (rest (str/split v #"/"))
    (str/join "/" v)))

(defn prefix&path->key
  [prefix path]
  (str/join "/" (remove empty? [prefix path])))

(keep )

(defrecord S3Location [bucket prefix]
    l/Location
    (analyse [this]
      (analyse-s3-bucket {:bucket-name bucket :prefix prefix}))
    (delete [this path]
      (delete bucket (prefix&path->key prefix path)))
    (read [this path]
      (log/info "attempting to read path" path "prefix is" prefix)
      (read bucket (prefix&path->key prefix path)))
    (write [this path stream]
      (write bucket (prefix&path->key prefix path) stream)))

(defn new-s3-location
  [bucket prefix]
  (map->S3Location {:bucket bucket :prefix prefix}))
