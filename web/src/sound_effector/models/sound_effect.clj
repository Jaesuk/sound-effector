(ns sound-effector.models.sound-effect
  (:require [clojure.java.jdbc :as sql]))

(def db-spec
  (or (System/getenv "DATABASE_URL") "postgresql://localhost:5432/sound_effector"))

(defn all []
  (into [] (sql/query db-spec ["SELECT * FROM sound_effects ORDER BY id DESC"])))

(defn create [title url]
  (sql/insert! db-spec :sound_effects [:title :url] [title url]))

(defn delete [id]
  (sql/delete! db-spec :sound_effects ["id = ?" id]))
