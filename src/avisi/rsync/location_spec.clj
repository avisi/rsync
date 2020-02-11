(ns avisi.rsync.location-spec
  (:require [clojure.spec :as s]
            [clj-time.core :as t]
            [avisi.rsync.location :as l]))

(def md5-regex #"^[a-fA-F0-9]{32}$")

(defn valid-path?
  [path]
  (not (= (first path) \/)))

(def path-gen (s/gen #{"a" "qwerty" "a/b" "a/b/c" "readme.txt" "usr/local/bin" "backup/production/2016-09-23"}))

(s/def ::path (s/with-gen (s/and string? valid-path?) (constantly path-gen)))

(s/def ::md5 (s/and string? #(re-matches md5-regex %)))

(s/def ::size (s/and number? pos?))

(s/def ::timestamp (inst? org.joda.time.DateTime))

(s/def ::meta (s/keys :opt-un [::size ::timestamp]))

(s/def ::analysis-entry (s/keys :req-un [::path ::md5 ::meta]))

(s/def ::analysis (s/coll-of ::analysis-entry :kind set?))
