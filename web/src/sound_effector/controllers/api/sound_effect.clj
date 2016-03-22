(ns sound-effector.controllers.api.sound-effect
  (:require
    [clojure.string :as string]
    [ring.util.response :refer [response]]
    [ring.middleware.params :refer [params-request]]
    [compojure.core :refer [defroutes GET]]
    [sound-effector.models.sound-effect :as model]))

(defn show-list [query]
  (if (string/blank? query)
    (response (model/read))
    (response (model/search query))))

(defroutes routes
           ; FIXME: There should be better way not to do below!
           (GET "/api/sound-effects" request (show-list (get (:query-params (params-request request)) "q"))))
