(ns sound-effector.web
  (:require
    [migratus.core :as migratus]
    [ring.adapter.jetty :as ring]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.middleware.format :refer [wrap-restful-format]]
    [compojure.core :refer [defroutes routes GET]]
    [compojure.route :as route]
    [sound-effector.controllers.sound-effect :as controller]
    [sound-effector.controllers.api.sound-effect :as api-controller]
    [sound-effector.core :refer [migratus-config elasticsearch-migrate]]
    [sound-effector.views.layout :as layout])
  (:gen-class))

(defroutes site-routes
           controller/routes
           (route/resources "/")
           (route/not-found (layout/response-404)))

(defroutes api-routes
           api-controller/routes)

(defroutes application
           (wrap-restful-format api-routes :formats [:json])
           (wrap-defaults site-routes site-defaults))

(defn start [port]
  (ring/run-jetty application {:port  port
                               :join? false}))

(defn -main []
  (migratus/migrate migratus-config)
  (elasticsearch-migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
