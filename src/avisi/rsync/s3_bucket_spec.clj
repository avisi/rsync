(ns avisi.rsync.s3-bucket-spec
  (:require [avisi.rsync.s3-bucket :as sus]
            [clojure.string :as string]
            [avisi.rsync.location-spec :as l-spec]
            [clojure.spec :as s]))

(defn valid-s3-url?
  [url]
  (string/starts-with? url "s3://"))

(def s3-url-gen (s/gen #{"s3://gerstree" "s3://some-bucket/with/a/long/path" "s3://another-bucket/somedir/somefile.png"}))

(s/def ::s3-url (s/with-gen (s/and string? valid-s3-url?) (constantly s3-url-gen)))

(s/fdef sus/s3-url->bucket-name
        :args (s/cat :s3-url ::s3-url)
        :ret string?)

(s/fdef avisi.rsync.s3-bucket/s3-url->key
        :args (s/cat :s3-url ::s3-url)
        :ret ::l-spec/path)
