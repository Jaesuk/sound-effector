(ns sound-effector.views.layout
  (:require [hiccup.page :as page]))

(defn common [title & content]
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
     [:div {:id "content" :class "container"} content]]))

(defn response-404 []
  (common "Page Not Found"
          "Page Not Found"))
