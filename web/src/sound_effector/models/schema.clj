(ns sound-effector.models.schema
  (:require [clojure.java.jdbc :as sql]
            [sound-effector.models.sound-effect :as sound-effect]))

(defn migrated? []
  (->
    (sql/query
      sound-effect/db-spec
      ["SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'sound_effects'"])
    first
    :count
    pos?
    ))

(defn migrate []
  (when (not (migrated?))
    (println "Creating database structures...") (flush)
    (print "- Createing sound_effect table...") (flush)
    (sql/db-do-commands
      sound-effect/db-spec
      (sql/create-table-ddl
        :sound_effects
        [:id :serial "PRIMARY KEY"]
        [:title :varchar "NOT NULL"]
        [:url :varchar "NOT NULL"]
        [:created_at :timestamp "NOT NULL" "DEFAULT CURRENT_TIMESTAMP"]))
    (println "done.")
    (println "Database structures is ready!")
    )
  )
