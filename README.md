# Clojure rsync [![Clojars Project](https://img.shields.io/clojars/v/avisi/rsync.svg)](https://clojars.org/avisi/rsync)

A Clojure library designed to sync files from one location to the other.

## Usage

````
(require '[avisi.rsync :as rsync])
(rsync/sync "s3://aws-codedeploy-us-east-1/latest" "/tmp")
````

## Supported locations

| protocol | example url |
| --- | --- |
| file | file:///tmp/put-it-here |
| s3 | s3://my-bucket/my-object |

## Spec support

If you want to validate your input parameters, turn on spec support:

````
(require '[clojure.spec.test :as stest])
(require '[avisi.rsync :as rsync])
(stest/instrument `rsync/sync)
````


## License

This is free and unencumbered software released into the public domain.

See LICENSE for more details.
