(ns sound-effector.controllers.auth.facebook
  (:require
    [clojure.string :as string]
    [crypto.random :as random]
    [ring.util.codec :refer [url-encode]]
    [ring.util.response :refer [redirect response status content-type header]]
    [compojure.core :refer [defroutes context GET]]
    [liberator.core :refer [resource]]
    [clj-http.client :as http]
    [sound-effector.models.user :as user]))

; The OAuth 2.0 Authorization Framework
; http://tools.ietf.org/html/rfc6749

; OAuth2 is easy - illustrated in 50 lines of Clojure
; https://leonid.shevtsov.me/en/oauth2-is-easy

; FIXME: must fetch this from the database.
(def ^:private provider-id 1)

(def ^:pivate client-config
  {:client-id     (System/getenv "FACEBOOK_CLIENT_ID")
   :client-secret (System/getenv "FACEBOOK_CLIENT_SECRET")
   :callback      (str (System/getenv "APP_PROTOCOL_HOST_PORT") "/auth/facebook/callback")})

(def ^:pivate urls
  {:authorize-url    {:uri     "https://www.facebook.com/dialog/oauth"
                      :queries {:client_id     (:client-id client-config)
                                :redirect_uri  (:callback client-config)
                                :response-type "code"
                                :scope         "public_profile,email"}}
   :access-token-url {:uri     "https://graph.facebook.com/v2.5/oauth/access_token"
                      :queries {:client_id     (:client-id client-config)
                                :client_secret (:client-secret client-config)
                                :redirect_uri  (:callback client-config)}}
   :user-info-url    {:uri     "https://graph.facebook.com/v2.5/me"
                      :queries {:fields "id,name,email"}}})

(defn- put-url-together
  ([uri-and-queries]
   (put-url-together uri-and-queries nil))
  ([uri-and-queries additional-queries]
   (str (:uri uri-and-queries)
        "?"
        (clojure.string/join "&" (map #(str (name (first %)) "=" (url-encode (second %))) (seq (merge (:queries uri-and-queries) additional-queries)))))))

(defn get-access-token [code]
  (try
    (-> (http/get (put-url-together (:access-token-url urls) {:code code}) {:as :json})
        :body
        :access_token)
    ; FIXME: must fix the way to handle and make the logging policy.
    (catch Exception e (do (print e) nil))))

(defn get-user-info [access-token]
  (try
    (-> (http/get (put-url-together (:user-info-url urls) {:access_token access-token}) {:as :json})
        :body)
    ; FIXME: must fix the way to handle and make the logging policy.
    (catch Exception e (do (print e) nil))))

(defn generate-anti-forgery-token []
  (string/replace (random/base64 60) #"[\+=/]" "-"))

(defn redirect-to-authorize-url [request]
  (let [anti-forgery-token (generate-anti-forgery-token)
        return-url (get-in request [:params :return_url] "/")]
    (-> (redirect (put-url-together (:authorize-url urls) {:state anti-forgery-token}))
        (header "Pragma" "no-cache")
        (assoc :session (assoc (:session request {}) :state anti-forgery-token :return-url return-url)))))

(defn- handle-error-result [body session]
  (->
    (response body)
    (status 400)
    (content-type "text/html")
    (header "Pragma" "no-cache")
    (assoc :session (dissoc session :state :return-url))))

(defn calledback [{{code              :code
                    state             :state
                    error             :error
                    error-reason      :error_reason
                    error-description :error_description} :params
                   session                                :session}]
  (let [state-in-session (:state session)]
    (if (and (not (nil? code))
             (= state state-in-session))
      (if-let [access-token (get-access-token code)]
        (if-let [facebook-user-info (get-user-info access-token)]
          (if-let [user (user/read provider-id (:id facebook-user-info))]
            (->
              (redirect (:return-url session "/"))
              (header "Pragma" "no-cache")
              (assoc :session
                     (vary-meta (dissoc (assoc session :user user) :state :return-url)
                                assoc :recreate true)))
            (->
              (redirect "/users/new")
              (header "Pragma" "no-cache")
              (assoc :session (dissoc (assoc session :creating-user
                                                     (assoc facebook-user-info :provider-id provider-id))
                                      :state :return-url))))
          (handle-error-result (str "Couln't get the user information in Facebook!<br/>Access token : " access-token) session))
        (handle-error-result (str "Couln't get the access token!<br/>Code : " code
                                  "<br/>State : " state
                                  "<br/>State in session : " state-in-session) session))
      (handle-error-result (str "Invalid request.<br/>Code : " code
                                "<br/>State : " state
                                "<br/>State in session : " state-in-session
                                "<br/>Error : " error
                                "<br/>Reason : " error-reason
                                "<br/>Description : " error-description
                                "<br/>Session : " session)
                           session))))

(defroutes routes
           (context "/auth" []
             (GET "/facebook" request (redirect-to-authorize-url request))
             (GET "/facebook/callback" request (calledback request))))
