(ns sound-effector.web
  (:require
    [ring.adapter.jetty :as ring]
    [ring.middleware.defaults :refer [wrap-defaults api-defaults site-defaults]]
    [ring.middleware.json :as json-middleware]
    [compojure.core :refer [defroutes routes GET]]
    [compojure.route :as route]
    [sound-effector.controllers.sound-effect :as controller]
    [sound-effector.controllers.api.sound-effect :as api-controller]
    [sound-effector.models.schema :as schema]
    [sound-effector.views.layout :as layout])
  (:gen-class))

(defroutes site-routes
           controller/routes
           (route/resources "/")
           (route/not-found (layout/response-404)))

(defroutes api-routes
           api-controller/routes)

(def application
  (routes
    (-> api-routes
        (json-middleware/wrap-json-response)
        (json-middleware/wrap-json-body))
    (wrap-defaults site-routes site-defaults)))

(defn start [port]
  (ring/run-jetty application {:port  port
                               :join? false}))

(defn -main []
  (schema/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "8080"))]
    (start port)))
