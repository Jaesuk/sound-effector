(ns sound-effector.controllers.api.sound-effect
  (:require
    [ring.util.response :refer [response]]
    [compojure.core :refer [defroutes GET]]
    [sound-effector.models.sound-effect :as model]))

(defn show-list []
  (response (model/read)))

(defroutes routes
           (GET "/api/sound-effects" [] (show-list)))
