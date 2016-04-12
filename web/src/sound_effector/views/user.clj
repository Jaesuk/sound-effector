(ns sound-effector.views.user
  (:require [sound-effector.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as anti-forgery]))

(defn show-create-form [request creating-user]
  (layout/common request
                 "Sound Effector - Join us :D!!!"
                 [:h1 (str "Welcome, " (:name creating-user) "! :D")]
                 [:div {:class "media"}
                  [:div {:class "media-left"}
                   [:img {:class "media-object img-circle" :src (str "http://graph.facebook.com/" (:id creating-user) "/picture?type=square")}]]
                  [:div {:class "media-body"}
                   [:h4 {:class "media-heading"} (:name creating-user)]
                   (:email creating-user)]]
                 [:p "Do you confirm the informations to join us?"]
                 [:div {:class "row"}
                  (form/form-to [:post "/users/new"]
                                (anti-forgery/anti-forgery-field)
                                [:div {:class "col-md-1 col-sm-2 col-xs-2"}
                                 (form/submit-button {:class "btn btn-primary"} "Confirm")]
                                [:div {:class "col-md-1 col-sm-1 col-xs-1"} "&nbsp;"]
                                [:div {:class "col-md-1 col-sm-2 col-xs-2"}
                                 [:a {:class "btn btn-default" :href "/users/new/cancel"} "Cancel"]])]))
