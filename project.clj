(defproject webgl "0.1.0-SNAPSHOT"
  :description "FIXME: write this!"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2760"]
                 [rm-hull/big-bang "0.0.1-SNAPSHOT"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]]

  :plugins [[lein-cljsbuild "1.0.4"]]

  :source-paths ["src"]

  :cljsbuild {
    :builds [{:id "webgl"
              :source-paths ["src"]
              :compiler {
                :output-to "webgl.js"
                :output-dir "out"
                :optimizations :none
                :source-map true}}]})
