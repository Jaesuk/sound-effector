(ns sound-effector.web
  (:require
    [migratus.core :as migratus]
    [ring.adapter.jetty :as ring]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [compojure.core :refer [defroutes]]
    [compojure.route :refer [resources not-found]]
    [sound-effector.controllers.sound-effect :as controller]
    [sound-effector.core :refer [migratus-config elasticsearch-migrate]]
    [sound-effector.views.layout :as layout])
  (:gen-class))

(defroutes site-routes
           controller/routes
           (resources "/")
           (not-found (layout/response-404)))

(def application
  (wrap-defaults site-routes site-defaults))

(defn start [port]
  (ring/run-jetty application {:port  port
                               :join? false}))

(defn migrate []
  (migratus/migrate migratus-config)
  (elasticsearch-migrate))

(defn -main []
  (migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
