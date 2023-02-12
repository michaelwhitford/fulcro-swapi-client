(ns swapi.components.parser
  (:require
    [swapi.components.auto-resolvers :refer [automatic-resolvers]]
    [swapi.components.blob-store :as bs]
    [swapi.components.config :refer [config]]
    [swapi.components.database :refer [xtdb-nodes]]
    [swapi.components.delete-middleware :as delete]
    [swapi.components.save-middleware :as save]
    [swapi.model-rad.attributes :refer [all-attributes]]

    ;; Require namespaces that define resolvers
    [swapi.model.account :as m.account]

    [com.fulcrologic.rad.attributes :as attr]
    [com.fulcrologic.rad.blob :as blob]
    ;[com.fulcrologic.rad.database-adapters.datomic-cloud :as datomic]
    [roterski.fulcro.rad.database-adapters.xtdb :as xtdb]
    [com.fulcrologic.rad.form :as form]
    [com.fulcrologic.rad.pathom :as pathom]
    [mount.core :refer [defstate]]
    [com.wsscode.pathom.core :as p]
    [com.fulcrologic.rad.type-support.date-time :as dt]
    [com.wsscode.pathom.connect :as pc]))

(pc/defresolver index-explorer [{::pc/keys [indexes]} _]
  {::pc/input  #{:com.wsscode.pathom.viz.index-explorer/id}
   ::pc/output [:com.wsscode.pathom.viz.index-explorer/index]}
  {:com.wsscode.pathom.viz.index-explorer/index
   (p/transduce-maps
     (remove (comp #{::pc/resolve ::pc/mutate} key))
     indexes)})

(def all-resolvers
  "The list of all hand-written resolvers/mutations."
  [index-explorer m.account/resolvers])

(defstate parser
  :start
  (pathom/new-parser config
    [(attr/pathom-plugin all-attributes)                    ; Other plugins need the list of attributes. This adds it to env.
     ;; Install form middleware
     (form/pathom-plugin save/middleware delete/middleware)
     ;; Select database for schema
     (xtdb/pathom-plugin (fn [env] {:production (:main xtdb-nodes)}))
     ;; Enables binary object upload integration with RAD
     (blob/pathom-plugin bs/temporary-blob-store {:files bs/file-blob-store})
     {::p/wrap-parser
      (fn transform-parser-out-plugin-external [parser]
        (fn transform-parser-out-plugin-internal [env tx]
          ;; Ensure the time zone is set for all resolvers/mutations
          (dt/with-timezone "America/Phoenix"
            (if (and (map? env) (seq tx))
              (parser env tx)
              {}))))}]
    [automatic-resolvers
     all-resolvers
     form/resolvers
     (blob/resolvers all-attributes)]))
