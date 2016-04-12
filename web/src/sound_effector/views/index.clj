(ns sound-effector.views.index
  (:require [sound-effector.views.layout :as layout]))

(defn index [request]
  (layout/common request
                 "Sound Effector"
                 [:h1 "Sound Effector"]
                 [:div "I don't know what to say! :D"]))
