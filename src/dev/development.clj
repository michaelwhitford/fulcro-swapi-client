(ns development
  (:require
    [clojure.pprint :refer [pprint]]
    [clojure.repl :refer [doc source]]
    [clojure.tools.namespace.repl :as tools-ns :refer [disable-reload! refresh clear set-refresh-dirs]]
    [swapi.components.database :refer [xtdb-nodes]]
    [swapi.components.ring-middleware]
    [swapi.components.server]
    [com.fulcrologic.rad.ids :refer [new-uuid]]
    [com.fulcrologic.rad.type-support.date-time :as dt]
    ;[datomic.api :as d]
    [xtdb.api :as xt]
    [mount.core :as mount]
    [taoensso.timbre :as log]))

;; Prevent tools-ns from finding source in other places, such as resources
(set-refresh-dirs "src/main" "src/dev")

(comment
  (let [db (xt/db (:main xtdb-nodes))]
    (xt/q db '{:find [(pull ?account [*])]
               :where [[?account :account/id ?uid]]})))

(defn seed! []
  (dt/set-timezone! "America/Phoenix")
  (let [connection (:main xtdb-nodes)]
    (when connection
      (log/info "SEEDING data.")
      )))

(defn start []
  (mount/start-with-args {:config "config/dev.edn"})
  (seed!)
  :ok)

(defn stop
  "Stop the server."
  []
  (mount/stop))

(defn fast-restart
  "Stop, refresh, and restart the server."
  []
  (stop)
  (start))

(defn restart
  "Stop, refresh, and restart the server."
  []
  (stop)
  (tools-ns/refresh :after 'development/start))

(comment
  (start)
  (stop)
  (restart))
