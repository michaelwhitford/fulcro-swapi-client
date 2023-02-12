(ns swapi.components.auto-resolvers
  (:require
    [swapi.model-rad.attributes :refer [all-attributes]]
    [mount.core :refer [defstate]]
    [com.fulcrologic.rad.resolvers :as res]
    ;[com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [roterski.fulcro.rad.database-adapters.xtdb :as xtdb]))

(defstate automatic-resolvers
  :start
  (vec
    (concat
      (res/generate-resolvers all-attributes)
      (xtdb/generate-resolvers all-attributes :production))))
