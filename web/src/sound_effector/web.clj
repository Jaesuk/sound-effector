(ns sound-effector.web
  (:require
    [clojure.data.json :as json]
    [migratus.core :as migratus]
    [ring.adapter.jetty :as ring]
    [ring.logger :as logger]
    [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
    [ring.util.response :refer [redirect header status response content-type]]
    [compojure.core :refer [defroutes GET]]
    [compojure.route :refer [resources not-found]]
    [cemerick.friend :as friend]
    [sound-effector.core :refer [migratus-config elasticsearch-migrate]]
    [sound-effector.models.role :as role]
    [sound-effector.controllers.auth.facebook-with-friend :as auth-facebook-with-friend]
    [sound-effector.controllers.user :as user-controller]
    [sound-effector.controllers.sound-effect :as sound-effect-controller]
    [sound-effector.views.layout :as layout]
    [sound-effector.views.index :as index-view])
  (:gen-class))

(defroutes site-routes
           user-controller/routes
           sound-effect-controller/routes
           (GET "/" request (index-view/index request))
           (GET "/test" request (friend/authorize #{::role/user} (str "Hello, <br/>" request "<br/>" (get-in request [:session ::friend/identity :current]))))
           (friend/logout (GET "/logout" [] (redirect "/")))
           (resources "/")
           (not-found (layout/response-404)))

(defn- unauthenticated-handler [request]
  (let [media-type (get-in request [:headers "accept"])]
    (if (= media-type "application/json")
      (->
        (response (json/write-str {:message "You need to login first. ;)"}))
        (status 403)
        (content-type "application/json"))
      (friend/default-unauthenticated-handler request))))

(defn- unauthorized-handler [request]
  (let [media-type (get-in request [:headers "accept"])]
    (if (= media-type "application/json")
      (->
        (response (json/write-str {:message "You don't have the permission."}))
        (status 403)
        (content-type "application/json"))
      (->
        (response "You don't have the permission.")
        (status 403)))))

(def application
  (-> site-routes
      (friend/authenticate {:allow-anon?             true
                            :workflows               [(auth-facebook-with-friend/workflow)]
                            :unauthenticated-handler unauthenticated-handler
                            :unauthorized-handler    unauthorized-handler})
      (wrap-defaults site-defaults)))

(defn start [port]
  (ring/run-jetty
    ; FIXME: must be seperated by profiles.
    (logger/wrap-with-logger application)
    {:port port :join? false}))

(defn migrate []
  (migratus/migrate migratus-config)
  (elasticsearch-migrate))

(defn -main []
  (migrate)
  (let [port (Integer. (or (System/getenv "PORT") "3000"))]
    (start port)))
