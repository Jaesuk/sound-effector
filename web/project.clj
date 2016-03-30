(defproject sound-effector "0.1.0"
  :description "Sound Effector"
  :url "https://github.com/Lion-Hwang/sound-effector"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.postgresql/postgresql "9.4.1208"]
                 [migratus "0.8.13"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring-middleware-format "0.7.0"]
                 [compojure "1.4.0"]
                 [hiccup "1.0.5"]
                 [commons-validator/commons-validator "1.5.0"]
                 [clojurewerkz/elastisch "2.2.1"]]
  :main ^:skip-aot sound-effector.web
  :uberjar-name "sound-effector-standalone.jar"
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler sound-effector.web/application
         :init    sound-effector.models.schema/migrate}
  :profiles {:dev     {:dependencies [[javax.servlet/servlet-api "2.5"]
                                      [ring-mock "0.1.5"]]}
             :uberjar {:omit-source true
                       :aot         :all}})
