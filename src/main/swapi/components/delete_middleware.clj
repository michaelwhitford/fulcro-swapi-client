(ns swapi.components.delete-middleware
  (:require
    ;[com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [roterski.fulcro.rad.database-adapters.xtdb :as xtdb]))

(def middleware (xtdb/wrap-xtdb-delete))
