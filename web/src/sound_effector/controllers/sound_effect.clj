(ns sound-effector.controllers.sound-effect
  (:require
    [clojure.string :as string]
    [clojure.data.json :as json]
    [ring.util.response :refer [redirect]]
    [ring.middleware.params :refer [params-request]]
    [compojure.core :refer [defroutes context GET POST DELETE]]
    [compojure.coercions :refer [as-int]]
    [liberator.core :refer [resource]]
    [sound-effector.models.sound-effect :as model]
    [sound-effector.views.sound-effect :as view])
  (:import
    org.apache.commons.validator.routines.UrlValidator)
  )

(def url-validator (UrlValidator. (into-array ["http" "https"])))

(defn show-list [request]
  (let [query (get-in request [:params :q])]
    (if (string/blank? query)
      (view/index (model/read))
      (view/index (model/search query) query))))

(defn show-json-list [request]
  (let [query (get (:query-params (params-request request)) "q")]
    (if (string/blank? query)
      (json/write-str (model/read) :value-fn (fn [_ value] (if (instance? java.sql.Timestamp value) (str value) value)))
      (model/search query))))

(defn create [title url]
  (if (and (not (string/blank? title)) (.isValid url-validator url))
    (model/create title url))
  ; TODO: error message and parameters will be returned.
  (redirect "/sound-effects"))

(defn delete [id]
  (model/delete id)
  (redirect "/sound-effects"))

(defroutes routes
           (GET "/" [] (redirect "/sound-effects"))
           (context "/sound-effects" []
             (GET "/" []
               (resource :available-media-types ["text/html" "application/json"]
                         :handle-ok
                         #(let [ctx %
                                media-type (get-in ctx [:representation :media-type])]
                           (condp = media-type
                             "text/html" (show-list (:request ctx))
                             "application/json" (show-json-list (:request ctx))
                             ))
                         ))
             (POST "/" [title url] (create title url))
             (DELETE "/:id" [id :<< as-int] (delete id))))
