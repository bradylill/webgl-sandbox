(defproject webgl "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2202"]
                 [rm-hull/big-bang "0.0.1-SNAPSHOT"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]]

  :plugins [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src"]

  :cljsbuild { 
    :builds [{:id "webgl"
              :source-paths ["src"]
              :compiler {
                :output-to "webgl.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
