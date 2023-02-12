(ns swapi.components.database
  (:require
    ;[com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [roterski.fulcro.rad.database-adapters.xtdb :as xtdb]
    [xtdb.rocksdb]
    [taoensso.timbre :as log]
    [mount.core :refer [defstate]]
    [swapi.model-rad.attributes :refer [all-attributes]]
    [swapi.components.config :refer [config]]))

;(defstate ^{:on-reload :noop} datomic-connections
;  :start
;  (datomic/start-databases all-attributes config))

(defstate ^{:on-reload :noop} xtdb-nodes
  :start
  (do (log/info (with-meta config {:pretty true}))
      (xtdb/start-databases (xtdb/symbolize-xtdb-modules config)))
  :stop
  (doseq [[_ node] xtdb-nodes]
    (.close node)))
