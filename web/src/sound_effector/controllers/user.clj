(ns sound-effector.controllers.user
  (:require
    [ring.util.response :refer [redirect response status content-type header]]
    [compojure.core :refer [defroutes context GET POST]]
    [sound-effector.models.user :as model]
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

(defn create [{{creating-user :creating-user} :session
               flash                          :flash
               session                        :session}]
  (if-let [user (model/create (:name creating-user)
                              (:email creating-user)
                              (:provider-id creating-user)
                              (:id creating-user))]
    (->
      (redirect "/users/new/done")
      (assoc :flash (assoc flash :user user)
             :session (dissoc session :creating-user)))
    (->
      (response "Um... I'm sorry. We have a problem.")
      (status 500)
      (content-type "text/html"))))

(defroutes routes
           (context "/users" []
             (GET "/new" request (show-create-form request))
             (GET "/new/done" [] (redirect "/sound-effects"))
             (GET "/new/cancel" request (cancel-creating request))
             (POST "/new" request (create request))))
