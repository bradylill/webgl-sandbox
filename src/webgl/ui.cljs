(ns webgl.ui
  (:require [om.core :as om]
            [om.dom :as dom]))

(defn title  [data owner]
  (reify
    om/IRender
    (render  [this]
      (dom/h1 nil  (:text data)))))

(defn render-ui []
  (om/root title  {:text "Webgl Sandbox"}
           {:target  (. js/document  (getElementById "ui"))}))
