(ns avisi.rsync.core-spec
  (:require [avisi.rsync.core :as sus]
            [clojure.spec :as s]))

(defn valid-url?
  [url]
  (not (nil? url)))

(def url-gen (s/gen #{"s3://my-bucket/my-object" "s3://my-bucket/another-object" "file:///tmp/put-it-here" "file:///tmp/or-maybe-here"}))

(s/def ::valid-url (s/with-gen valid-url? (constantly url-gen)))

(s/fdef sus/sync!
        :args (s/cat :from-url ::valid-url :to-url ::valid-url)
