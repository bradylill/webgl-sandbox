(ns webgl.ui
  (:require [om.core :as om]
            [om.dom :as dom]
            [webgl.sample-material :as mat]))

(defonce ui-state (atom {:shader-programs
                         [{:title "Vertex"
                           :content mat/vertex-shader}
                          {:title "Fragment"
                           :content mat/fragment-shader}]}))

(defn editor-view [data owner]
  (reify
    om/IRender
    (render [this]
      (apply dom/div nil
             (om/build-all shader-edit-box (:shader-programs data))))))

(defn shader-edit-box  [data owner]
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (dom/p nil (:title data))
               (dom/textarea #js {:cols 40 :rows 15} (:content data))))))

(defn render-ui []
  (om/root editor-view ui-state {:target  (. js/document  (getElementById "ui"))}))
