(ns avisi.rsync.local-dir
  (:require [avisi.rsync.location :as l]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [pandect.algo.md5 :as md5])
  (:import [java.io File]
           [java.util.regex Pattern]))

(declare relative-path)
(declare path->file-details)
(declare path->absolute-path)

(defn analyse-local-directory
  "Analyse a local directory returnings a set
   of file details describing the relative path
   and md5 checksum of all the files (recursively)
   under the directory."
  [dir-path]
  (let [root-dir (clojure.java.io/file dir-path)
        abs-dir-path (.getAbsolutePath root-dir)]
    (->> (file-seq root-dir)
         (filter #(not (.isDirectory %)))
         (map (partial path->file-details abs-dir-path))
         (set))))

(defn path->absolute-path [path]
  (.getAbsolutePath (clojure.java.io/file path)))

(defn combine-path [root-path rel-path]
  (let [root (clojure.java.io/file root-path)
        combined (clojure.java.io/file root rel-path)
        abs-path (.getAbsolutePath combined)]
    abs-path))

(defn file-url->file-name
  [url]
  (second (str/split url #"://")))

(defn read
  [file-path]
  (log/debug "reading from" file-path)
  (io/input-stream (io/file file-path)))

(defn write
  [file-path input-stream]
  (log/debug "writing to" file-path)
  (io/make-parents file-path)
  (with-open [output-stream (io/output-stream (io/file file-path))]
    (io/copy input-stream output-stream)))

(defn delete
  [file-path]
  (io/delete-file file-path))

;; Private Helper Functions

(defn- root-path-regex [root]
  (let [updated-root (.replace root File/separator "/")]
    (str "^" (str updated-root "/"))))

(defn- relative-path [root target]
  (-> target
      (.replace File/separator "/")
      (.replaceAll (root-path-regex root) "")))

(defn- path->file-details [root-path file]
  (let [absolute-path (.getAbsolutePath file)
        rel-path (relative-path root-path absolute-path)
        md5 (md5/md5-file absolute-path)
        last-modified (.lastModified file)
        size (.length file)]
    {:path rel-path :md5 md5 :meta {:size size}}))

(defrecord DirectoryLocation [directory]
  l/Location
  (analyse [this]
    (analyse-local-directory directory))
  (write [this {:keys [path]} stream]
    (write (str directory "/" path) stream))
  (read [this {:keys [path]}]
    (read (str directory "/" path)))
  (delete [this {:keys [path]}]
    (delete (str directory "/" path))))

(defn new-directory-location
  [directory]
  (map->DirectoryLocation {:directory directory}))
