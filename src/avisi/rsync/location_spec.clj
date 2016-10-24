(ns avisi.rsync.location-spec
  (:require [clojure.spec :as s]
            [avisi.rsync.location :as l]))

(def md5-regex #"^[a-fA-F0-9]{32}$")

(defn valid-path?
  [path]
  (not (= (first path) \/)))

(def path-gen (s/gen #{"a" "qwerty" "a/b" "a/b/c" "readme.txt" "usr/local/bin" "backup/production/2016-09-23"}))

(s/def ::path (s/with-gen (s/and string? valid-path?) (constantly path-gen)))

(s/def ::md5 (s/and string? #(re-matches md5-regex %)))

(s/def ::analysis-entry (s/keys :req-un [::path ::md5]))

(s/def ::analysis (s/coll-of ::analysis-entry :kind set?))
