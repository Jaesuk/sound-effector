(ns sound-effector.views.sound-effect
  (:require [sound-effector.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as anti-forgery]))

(defn show-create-form []
  [:div {:id "sound-effect-create-form"}
   (form/form-to [:post "/sound-effects"]
                 (anti-forgery/anti-forgery-field)
                 [:div {:class "form-group"}
                  (form/label "title" "Title")
                  (form/text-field {:class "form-control" :placeholder "What would you call this sound?"} "title")]
                 [:div {:class "form-group"}
                  (form/label "url" "URL")
                  (form/text-field {:class "form-control" :placeholder "The source URL of this sound."} "url")]
                 (form/submit-button {:class "btn btn-primary"} "Submit"))])

(defn index [sound-effects]
  (layout/common "Sound Effects"
                 [:h1 "Sound Effects"]
                 (show-create-form)
                 [:div
                  (map
                    (fn [sound-effect]
                      [:div {:class "row"}
                       [:div {:class "col-md-11 col-sm-11 col-xs-11"}
                        [:h2 (h (:title sound-effect))]
                        [:p
                         [:a {:href (:url sound-effect)} (h (:url sound-effect))]]]
                       [:div {:class "col-md-1 col-sm-1 col-xs-1"}
                        (form/form-to [:delete (str "/sound-effects/" (:id sound-effect))]
                                      (anti-forgery/anti-forgery-field)
                                      [:button {:type "submit" :class "btn btn-default"}
                                       [:i {:class "glyphicon glyphicon-remove"}]])]])
                    sound-effects)]))
