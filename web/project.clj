(defproject sound-effector "0.1.0"
  :description "Sound Effector"
  :url "https://github.com/Lion-Hwang/sound-effector"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/core.cache "0.6.5"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [migratus "0.8.13"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-anti-forgery "1.0.1"]
                 [ring-logger "0.7.6"]
                 [compojure "1.4.0"]
                 [liberator "0.13"]
                 [com.cemerick/friend "0.2.1"]
                 [hiccup "1.0.5"]
                 [commons-codec/commons-codec "1.10"]
                 [commons-validator/commons-validator "1.5.0"]
                 [org.apache.httpcomponents/httpclient "4.5.1"]
                 [clj-http "2.1.0" :exclusions [org.apache.httpcomponents/httpcore]]
                 [slingshot "0.12.2"]
                 [cheshire "5.5.0"]
                 [clojurewerkz/elastisch "2.2.1"]]
  :main ^:skip-aot sound-effector.web
  :uberjar-name "sound-effector-standalone.jar"
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler sound-effector.web/application
         :init    sound-effector.web/migrate
         :port    3000}
  :profiles {:dev     {:dependencies [[javax.servlet/servlet-api "2.5"]
                                      [ring-mock "0.1.5"]]}
             :uberjar {:omit-source true
                       :aot         :all}})
