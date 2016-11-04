(ns avisi.rsync.location)

(defprotocol Location
  "A location holding files that can be pulled and pushed"
  (analyse [this])
  (delete [this file])
  (read [this file])
  (write [this file stream]))

