(ns sound-effector.web
  (:require
    [compojure.core :refer [defroutes GET]]
    [ring.adapter.jetty :as ring]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [compojure.route :as route]
    [sound-effector.controllers.sound-effect :as controller]
    [sound-effector.models.schema :as schema]
    [sound-effector.views.layout :as layout])
  (:gen-class))

(defroutes routes
           controller/routes
           (route/resources "/")
           (route/not-found (layout/response-404)))

(def application (wrap-defaults routes site-defaults))

(defn start [port]
  (ring/run-jetty application {:port  port
                               :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
