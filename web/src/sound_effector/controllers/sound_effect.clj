(ns sound-effector.controllers.sound-effect
  (:require
    [clojure.string :as string]
    [clojure.data.json :as json]
    [ring.util.response :refer [redirect]]
    [ring.middleware.params :refer [params-request]]
    [compojure.core :refer [defroutes context ANY]]
    [compojure.coercions :refer [as-int]]
    [cemerick.friend :as friend]
    [liberator.core :refer [resource]]
    [sound-effector.models.role :as role]
    [sound-effector.models.sound-effect :as model]
    [sound-effector.views.sound-effect :as view])
  (:import
    org.apache.commons.validator.routines.UrlValidator)
  )

(def url-validator (UrlValidator. (into-array ["http" "https"])))

(defn show-list [request]
  (let [query (get-in request [:params :q])]
    (if (string/blank? query)
      (view/show-list request (model/read))
      (view/show-list request query (model/search query)))))

(defn show-json-list [request]
  (let [query (get (:query-params (params-request request)) "q")]
    (if (string/blank? query)
      ; FIXME: Find same date formatter!
      (json/write-str (model/read) :value-fn (fn [_ value] (if (instance? java.sql.Timestamp value) (str value) value)))
      (model/search query))))

(defn create [title url]
  (if (and (not (string/blank? title)) (.isValid url-validator url))
    (model/create title url)))

(defn delete [id]
  (model/delete id))

(defroutes routes
           (ANY "/sound-effects" []
             (friend/authorize
               #{::role/user}
               (resource
                 :allowed-methods [:get :post]
                 :available-media-types ["text/html" "application/json"]

                 :handle-ok
                 #(let [ctx %
                        media-type (get-in ctx [:representation :media-type])]
                   (condp = media-type
                     "text/html" (show-list (:request ctx))
                     "application/json" (show-json-list (:request ctx))))

                 :post!
                 #(let [title (get-in % [:request :params :title])
                        url (get-in % [:request :params :url])]
                   (create title url))
                 :post-redirect? (fn [_] {:location "/sound-effects"}))))

           (ANY ["/sound-effects/:id{[0-9]+}"] [id :<< as-int]
             (friend/authorize
               #{::role/user}
               (resource
                 :allowed-methods [:post :delete]
                 :available-media-types ["text/html" "application/json"]

                 :post!
                 #(let [method (get-in % [:request :params :_method])]
                   (if (= "DELETE" method) (delete id)))
                 :post-redirect? (fn [_] {:location "/sound-effects"})

                 :delete!
                 (fn [_] (delete id))))))
