(defproject irresponsible/tentacles "0.6.5-SNAPSHOT"
  :description "Clojure library for working with the Github v3 API"
  :url "https://github.com/clj-commons/tentacles"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0" :scope "provided"]
                 [clj-http "3.9.1"]
                 [cheshire "5.8.1"]
                 [com.cemerick/url "0.1.1"]
                 [org.clojure/data.codec "0.1.1"]
                 [environ "1.1.0"]]
  :pedantic? :abort)
