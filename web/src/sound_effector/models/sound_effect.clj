(ns sound-effector.models.sound-effect
  (:require [clojure.java.jdbc :as sql]
            [clojurewerkz.elastisch.rest :as esr]
            [clojurewerkz.elastisch.rest.document :as esd]
            [clojurewerkz.elastisch.query :as esq]
            [sound-effector.core :refer [db-spec es-spec]]))

(defn search [query]
  (loop [hits (:hits (:hits (esd/search (esr/connect es-spec)
                                        "sound_effector"
                                        "sound_effect"
                                        :query (esq/query-string {:query query}))))
         result []]
    (if (empty? hits)
      result
      (recur (next hits) (conj result (:_source (first hits)))))))

(defn create [title url uploader]
  ; TODO: apply the transaction.
  (let [sound-effect (first (sql/insert! db-spec
                                         :sound_effects {:title            title
                                                         :url              url
                                                         :uploader_id (:id uploader)}))]
    (esd/create (esr/connect es-spec)
                "sound_effector"
                "sound_effect"
                sound-effect
                :id (str (:id sound-effect)))
    sound-effect))

(defn read
  ([]
   (into [] (sql/query db-spec ["SELECT a.*, b.name uploader_name FROM sound_effects a LEFT JOIN users b ON (a.uploader_id = b.id) ORDER BY id DESC"])))
  ([id]
   (first (sql/query db-spec ["SELECT a.*, b.name uploader_name FROM sound_effects a LEFT JOIN users b ON (a.uploader_id = b.id) WHERE a.id = ?" id]))))

(defn delete [id]
  ; TODO: apply the transaction.
  (esd/delete (esr/connect es-spec) "sound_effector" "sound_effect" (str id))
  (sql/delete! db-spec :sound_effects ["id = ?" id]))
