(defproject clojure-excu-sandbox "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.namespace "0.2.4"]
                 [quil "1.7.0"]
                 [lonocloud/synthread "1.0.5"]]
  :profiles {:dev {:source-paths ["dev"]
                   :dependencies [[robert/hooke "1.3.0"]]}})
