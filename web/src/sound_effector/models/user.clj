(ns sound-effector.models.user
  (:require [clojure.java.jdbc :as sql]
            [sound-effector.core :refer [db-spec]]))

(defn create [name email provider-id provider-user-id]
  (sql/with-db-transaction
    [trans-conn db-spec]
    ; FIXME: find the better way as more clojure way.
    (let [user (first (sql/insert! db-spec :users {:name  name
                                                   :email email}))]
      (sql/insert! db-spec
                   :user_providers {:user_id          (:id user)
                                    :provider_id      provider-id
                                    :provider_user_id provider-user-id})
      user)))

(defn read
  ([id]
   (first (sql/query db-spec ["SELECT * FROM users WHERE id = ?" id])))
  ([provider-id provider-user-id]
   (first (sql/query db-spec ["SELECT a.* FROM users a JOIN user_providers b ON (b.provider_id = ? AND b.provider_user_id = ? AND b.user_id = a.id)" provider-id provider-user-id]))))
