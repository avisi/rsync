(ns avisi.rsync.location)

(defprotocol Location
  "A location holding files that can be pulled and pushed"
  (analyse [this])
  (delete [this path])
  (read [this path])
  (write [this path stream]))

