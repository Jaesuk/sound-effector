(ns sound-effector.views.layout
  (:require
    [cemerick.friend :as friend]
    [hiccup.page :as page]))

(defn profile-image [user]
  ; TODO: implements more social networks' profiles later.
  (let [first-provider (first (:providers user))
        first-provider-id (first first-provider)
        first-provider-user-id (second first-provider)]
    (cond
      (= 1 first-provider-id)
      (str "http://graph.facebook.com/" first-provider-user-id "/picture?type=square"))))

(defn navigation [request]
  [:div {:class "nav"}
   (let [current-user-id (get-in request [:session ::friend/identity :current])]
     (if-let [user (get-in request [:session ::friend/identity :authentications current-user-id :user])]
       [:div {:class "col-md-3 col-sm-4 col-xs-4 pull-right"}
        [:div {:class "media"}
         [:div {:class "media-left"}
          [:img {:class "media-object img-circle" :src (profile-image user)}]]
         [:div {:class "media-body"}
          [:h4 {:class "media-heading"} (:name user)]
          [:a {:class "btn btn-default" :href "/logout"} "Logout"]]]]
       [:div {:class "col-md-3 col-sm-4 col-xs-4 pull-right"}
        "Join or login with" [:br]
        [:a {:class "btn btn-fb" :href "/sound-effects"}
         [:i {:class "fa fa-facebook"} " Facebook"]]]))])

(defn common [request title & content]
  (page/html5
    [:head
     [:meta {:charset "utf-8"}]
     [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
     [:title title]
     (page/include-js "/lib/jquery/dist/jquery.min.js")
     (page/include-css "/lib/bootstrap/dist/css/bootstrap.min.css")
     (page/include-js "/lib/bootstrap/dist/js/bootstrap.min.js")
     (page/include-css "/lib/font-awesome/css/font-awesome.min.css")
     (page/include-css "/css/sound_effector.css")]
    [:body
     [:div {:id "content" :class "container"}
      (navigation request)
      content]]))

(defn response-404 []
  (common "Page Not Found"
          "Page Not Found"))
