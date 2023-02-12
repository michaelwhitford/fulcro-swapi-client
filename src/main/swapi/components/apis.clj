(ns swapi.components.apis
  (:require [martian.core :as martian]
            [martian.yaml :as yaml]
            [martian.interceptors :as interceptors]
            [martian.clj-http :as martian-http]))


(def swapi-interceptors (concat martian/default-interceptors
                                [interceptors/default-encode-body
                                 interceptors/default-coerce-response
                                 martian-http/perform-request]))

(def swapi (martian/bootstrap-openapi "https://swapi.dev/api"
                                      (yaml/yaml->edn (slurp "openapi/swapi.yaml"))
                                      {:interceptors swapi-interceptors}))

(comment
  (martian/explore swapi)
  (martian/explore swapi :planet)
  (martian/request-for swapi :planets)
  (martian/response-for swapi :planets)
  (martian/request-for swapi :planet {:id "10"})
  ; a valid id gets a good response
  (martian/response-for swapi :planet {:id "10"})
  ; this errors out with an exception from clj-http
  ; the openapi spec file describes 404 as a valid response with a json body
  (martian/response-for swapi :planet {:id "40003"})
  )
