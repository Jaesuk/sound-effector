(ns sound-effector.views.sound-effect
  (:require [sound-effector.views.layout :as layout]
            [hiccup.core :refer [h]]
            [hiccup.form :as form]
            [ring.util.anti-forgery :as anti-forgery]
            [clojure.string :as string]))

(defn show-create-form []
  [:div
   (form/form-to [:post "/sound-effects"]
                 (anti-forgery/anti-forgery-field)
                 [:div {:class "form-group"}
                  (form/label "title" "Title")
                  (form/text-field {:class "form-control" :placeholder "What would you call this sound?"} "title")]
                 [:div {:class "form-group"}
                  (form/label "url" "URL")
                  (form/text-field {:class "form-control" :placeholder "The source URL of this sound."} "url")]
                 (form/submit-button {:class "btn btn-primary"} "Submit"))])

(defn show-search-form [query]
  [:div
   (form/form-to {:class "form-inline"} [:get "/sound-effects"]
                 [:div {:class "input-group"}
                  (form/text-field {:class "form-control" :placeholder "Search..."} "q" query)
                  [:div {:class "input-group-btn"}
                   [:button {:type "submit" :class "btn btn-default"}
                    [:i {:class "glyphicon glyphicon-search"}]]
                   [:button {:type    "button" :class "btn btn-default"
                             :onclick "document.location.href='/sound-effects';"}
                    [:i {:class "glyphicon glyphicon-th-list"}]]]])])

(defn show-list
  ([request sound-effects] (show-list request nil sound-effects))
  ([request query sound-effects]
   (layout/common
     request
     "Sound Effects"
     [:h1 "Sound Effects"]
     (show-create-form)
     [:hr]
     (show-search-form query)
     [:div
      (if-not (empty? sound-effects)
        (map
          (fn [sound-effect]
            [:div {:class "row"}
             [:div {:class "col-md-11 col-sm-10 col-xs-10"}
              [:h2 (h (:title sound-effect))]
              [:p {:class "text-left"}
               [:a {:href (:url sound-effect)} (h (:url sound-effect))]]]
             [:div {:class "col-md-1 col-sm-2 col-xs-2" :style "text-align: center"}
              [:small (if (string/blank? (:uploader_name sound-effect)) "Unknown" (:uploader_name sound-effect))]
              (form/form-to [:delete (str "/sound-effects/" (:id sound-effect))]
                            (anti-forgery/anti-forgery-field)
                            [:button {:type "submit" :class "btn btn-default"}
                             [:i {:class "glyphicon glyphicon-remove"}]])]])
          sound-effects)
        (if (string/blank? query)
          [:h3 {:class "text-danger"} "No sound effects found."]
          [:h3 {:class "text-danger"} (str "No sound effects found by " query)]
          ))])))
