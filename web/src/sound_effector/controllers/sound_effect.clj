(ns sound-effector.controllers.sound-effect
  (:require
    [clojure.string :as string]
    [ring.util.response :as ring-response]
    [compojure.core :refer [defroutes GET POST DELETE]]
    [compojure.coercions :refer [as-int]]
    [sound-effector.models.sound-effect :as model]
    [sound-effector.views.sound-effect :as view])
  (:import
    org.apache.commons.validator.routines.UrlValidator)
  )

(def url-validator (UrlValidator. (into-array ["http" "https"])))

(defn show-list []
  (view/index (model/all)))

(defn create [title url]
  (if (and (not (string/blank? title)) (.isValid url-validator url))
    (model/create title url)
    (println "TODO error message and parameters will be returned."))
  (ring-response/redirect "/sound-effects"))

(defn delete [id]
  (model/delete id)
  (ring-response/redirect "/sound-effects"))

(defroutes routes
           (GET "/" [] (ring-response/redirect "/sound-effects"))
           (GET "/sound-effects" [] (show-list))
           (POST "/sound-effects" [title url] (create title url))
           (DELETE "/sound-effects/:id" [id :<< as-int] (delete id)))
