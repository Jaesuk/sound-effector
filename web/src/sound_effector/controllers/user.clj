(ns sound-effector.controllers.user
  (:require
    [clojure.data.json :as json]
    [ring.middleware.anti-forgery :refer [*anti-forgery-token*]]
    [ring.util.response :refer [redirect response status content-type header]]
    [compojure.core :refer [defroutes context GET ANY]]
    [cemerick.friend :as friend]
    [cemerick.friend.workflows :refer [make-auth]]
    [liberator.core :refer [resource]]
    [liberator.representation :refer [ring-response]]
    [sound-effector.models.user :as model]
    [sound-effector.models.role :as role]
    [sound-effector.views.user :as view]))

(defn show-create-form [{{creating-user :creating-user} :session
                         :as                            request}]
  (if-not (nil? creating-user)
    (view/show-create-form request creating-user)
    (->
      (redirect "/")
      (header "Pragma" "no-cache"))))

(defn cancel-creating [{session :session}]
  (->
    (redirect "/")
    (header "Pragma" "no-cache")
    (assoc :session (dissoc session :creating-user))))

(defn create [{{creating-user :creating-user} :session}]
  {::user (model/create (:name creating-user)
                        (:email creating-user)
                        (:provider-id creating-user)
                        (:id creating-user))})

(defroutes routes
           (context "/users" []
             (GET "/new" request (show-create-form request))
             (GET "/new/cancel" request (cancel-creating request))
             (ANY "/" []
               ; Example of liberator resource with post redirect and setting flash
               ; https://gist.github.com/jbarber/143351e2db1bebb4c910
               (resource
                 :allowed-methods [:post]
                 :available-media-types ["text/html" "application/json"]

                 :post!
                 #(create (:request %))

                 :post-redirect?
                 #(let [ctx %
                        media-type (get-in ctx [:representation :media-type])]
                   (condp = media-type
                     "text/html" {:location "/sound-effects"}
                     "application/json" [false {:location nil}]))

                 :handle-see-other
                 #(let [ctx %
                        media-type (get-in ctx [:representation :media-type])]
                   (condp = media-type
                     "text/html" (ring-response
                                   (let [session (get-in ctx [:request :session])]
                                     (if-let [user (::user ctx)]
                                       (->
                                         (redirect (:location ctx))
                                         (assoc :session (dissoc session :creating-user))
                                         (friend/merge-authentication (make-auth {:identity (:id user) :user user :roles #{::role/user}}))
                                         )
                                       (->
                                         (response "Um... I'm sorry. We have a problem.")
                                         (status 500)
                                         (content-type "text/html")))))))

                 :handle-created
                 #(let [ctx %
                        media-type (get-in ctx [:representation :media-type])]
                   (condp = media-type
                     "application/json" (ring-response
                                          (let [session (get-in ctx [:request :session])]
                                            (if-let [user (::user ctx)]
                                              (->
                                                (response (json/write-str user :value-fn (fn [_ value] (if (instance? java.sql.Timestamp value) (str value) value))))
                                                (status 201)
                                                (content-type "application/json")
                                                (assoc :session (dissoc session :creating-user))
                                                (friend/merge-authentication (make-auth {:identity (:id user) :user user :roles #{::role/user}})))
                                              (->
                                                (response (json/write-str {:message "Um... I'm sorry. We have a problem."}))
                                                (status 500)
                                                (content-type "application/json")))
                                            ))))))))
