(ns sound-effector.core
  (:require [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.index :as esi]))

(def db-spec
  (or (System/getenv "DATABASE_URL") "postgresql://localhost:5432/sound_effector"))

(def migratus-config {:store         :database
                      :migration-dir "migrations"
                      :db            db-spec})

(def es-spec
  (or (System/getenv "SEARCHBOX_URL") "http://localhost:9200"))

(defn elasticsearch-migrate []
  (when (not (esi/exists? (esr/connect es-spec) "sound_effector"))
    (println "Creating elasticsearch indices...") (flush)
    (print "- Createing sound_effector index...") (flush)
    (esi/create (esr/connect es-spec)
                "sound_effector"
                :mappings {"sound_effect"
                           {:properties {:id         {:type "long"}
                                         :title      {:type "string"}
                                         :url        {:type "string"}
                                         :created_at {:type "date"}}}})
    (println "done.") (flush)
    (println "elasticsearch indices are ready!") (flush)
    ))
