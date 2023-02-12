(ns swapi.model.account
  "Functions, resolvers, and mutations supporting `account`.

   DO NOT require a RAD model file in this ns. This ns is meant to be an ultimate
   leaf of the requires. Only include library code."
  (:require
    [com.wsscode.pathom.connect :as pc :refer [defresolver defmutation]]
    ;[com.fulcrologic.rad.database-adapters.datomic-options :as do]
    [roterski.fulcro.rad.database-adapters.xtdb-options :as xo]
    [com.fulcrologic.rad.ids :refer [new-uuid]]
    ;[datomic.client.api :as d]
    #?(:clj
       [xtdb.api :as xt])
    [taoensso.timbre :as log]))

(defn new-account
  "Create a new account. The Datomic tempid will be the email."
  [email & {:as addl}]
  (merge
    {:db/id           email
     :account/email   email
     :account/active? true
     :account/id      (new-uuid)}
    addl))

#?(:clj
   (defn get-all-accounts
     [env & args]
     (if-let [db (some-> (get-in env [xo/databases :production]) deref)]
       (->> (if (get-in env [:query-params :show-inactive?])
             '{:find [?e]
                :where [[?e :account/id]]}
             '{:find [?e]
                :where [[?e :account/id]
                        [?e :account/active? true]]})
            (xt/q db)
            (mapv (fn [[id]] {:account/id id})))
       (log/error "No database atom for production schema!"))))

#?(:clj
   (defresolver all-accounts-resolver [env params]
     {::pc/output [{:account/all-accounts [:account/id]}]}
     {:account/all-accounts (get-all-accounts env params)}))

(def resolvers [all-accounts-resolver])
