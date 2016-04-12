(ns sound-effector.models.user
  (:require [clojure.java.jdbc :as sql]
            [sound-effector.core :refer [db-spec]]))

(defn- make-providers [id]
  (loop [providers (sql/query db-spec ["SELECT provider_id, provider_user_id FROM user_providers WHERE user_id = ?" id])
         result {}]
    (if (nil? providers)
      result
      (let [provider (first providers)]
        (recur (next providers)
               (assoc result (:provider_id provider) (:provider_user_id provider)))))))

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
      (assoc user :providers (make-providers (:id user))))))

(defn read
  ([id]
   (let [user (first (sql/query db-spec ["SELECT * FROM users WHERE id = ?" id]))]
     (assoc user :providers (make-providers (:id user)))))
  ([provider-id provider-user-id]
   (let [user (first (sql/query db-spec ["SELECT a.* FROM users a JOIN user_providers b ON (b.provider_id = ? AND b.provider_user_id = ? AND b.user_id = a.id)" provider-id provider-user-id]))]
     (assoc user :providers (make-providers (:id user))))))
