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

  ;; after listing the objects, reduce and restructure the information to conform to the spec
  (let [paths (into []
                    (comp (filter #(not (str/ends-with? (:key %) "/")))
                          (map #(select-keys % [:key :etag :size]))
                          (map #(clojure.set/rename-keys % {:key :path :etag :md5 :last-modified :timestamp}))
                          (map #(reduce-kv (fn [m k v]
                                             (if (contains? #{:path :md5} k)
                                               (assoc m k v)
                                               (assoc-in m [:meta k] v)))
                                           {} %)))
                    (list-all-objects request))]
    (if (str/blank? (:prefix request))
      (set paths)
      (set (map #(assoc-in % [:path] (subs (:path %) (inc (count (:prefix request))))) paths)))))

(defn delete
  [bucket key]
  (s3/delete-object bucket key))

(defn write
  [bucket key stream meta-data]
  (log/debug "writing to key" key "meta data supplied" meta-data)
  (let [s3-meta-data (-> meta-data
                          (select-keys [:size])
                          (clojure.set/rename-keys {:size :content-length}))]
    (log/debug "writing to key" key "with meta data" s3-meta-data)
    (s3/put-object :bucket-name bucket :key key :input-stream stream :meta-data s3-meta-data)))

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

(defrecord S3Location [bucket prefix]
  l/Location
  (analyse [this]
    (analyse-s3-bucket {:bucket-name bucket :prefix prefix}))
  (delete [this file]
    (delete bucket (prefix&path->key prefix (:path file))))
  (read [this file]
    (log/debug "attempting to read file" (:path file) "prefix is" prefix)
    (read bucket (prefix&path->key prefix (:path file))))
  (write [this file stream]
    (write bucket (prefix&path->key prefix (:path file)) stream (:meta file))))

(defn new-s3-location
  [bucket prefix]
  (map->S3Location {:bucket bucket :prefix prefix}))
