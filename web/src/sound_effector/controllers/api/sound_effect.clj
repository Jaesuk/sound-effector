(ns sound-effector.controllers.api.sound-effect
  (:require
    [clojure.string :as string]
    [ring.util.response :refer [response]]
    [compojure.core :refer [defroutes GET]]
    [sound-effector.models.sound-effect :as model]))

(defn show-list [query]
  (if (string/blank? query)
    (response (model/read))
    (response (model/search query))))

(defroutes routes
           (GET "/api/sound-effects*" {params :query-params} (show-list (get params "q"))))
