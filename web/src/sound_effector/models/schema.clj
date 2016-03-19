(ns sound-effector.models.schema
  (:require [clojure.java.jdbc :as sql]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]
            [sound-effector.models.sound-effect :as sound-effect]))

(defn database-migrated? []
  (->
    (sql/query
      sound-effect/db-spec
      ["SELECT COUNT(*) FROM information_schema.tables WHERE table_name = 'sound_effects'"])
    first
    :count
    pos?))

(defn search-engine-migrated? []
  (esi/exists? (esr/connect sound-effect/es-spec) "sound_effector"))

(defn migrate []
  (when (not (database-migrated?))
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
    (println "done.") (flush)
    (println "database structures are ready!") (flush))
  (when (not (search-engine-migrated?))
    (println "Creating search engine indices...") (flush)
    (print "- Createing sound_effector index...") (flush)
    (esi/create (esr/connect sound-effect/es-spec)
                "sound_effector"
                :mappings {"sound_effect"
                           {:properties {:id         {:type "long"}
                                         :title      {:type "string"}
                                         :url        {:type "string"}
                                         :created_at {:type "date"}}}})
    (println "done.") (flush)
    (println "Search engine indices are ready!") (flush)))
