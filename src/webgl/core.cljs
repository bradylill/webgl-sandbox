(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]))

(enable-console-print!)

(def window-width  window/innerWidth)
(def window-height window/innerHeight)

(def camera (THREE.PerspectiveCamera. 75 
                                      (/ window-width
                                         window-height)
                                      0.1
                                      1000))

(def renderer (THREE.WebGLRenderer. #js {:antialias true}))
(def scene    (THREE.Scene.))

(defn init-three! [renderer camera]
  (do
    (.setSize renderer window-width window-height)
    (.appendChild (.-body js/document) (.-domElement renderer))
    (.set (.-position camera) 0 0 5)))

(defn init-lights! [scene]
  (do
    (.add scene (THREE.AmbientLight. 0x808080))
    (let [directional-light (THREE.DirectionalLight. 0xffffff 1)]
      (.set (.-position directional-light) 1 1 1)
      (.add scene directional-light))))

(defn create-box []
  (let [geo       (THREE.BoxGeometry. 1 1 1)
        material  (THREE.MeshBasicMaterial. #js {:color 0x00ff00})]
    (THREE.Mesh. geo material)))

(init-three! renderer camera)
(init-lights! scene)

(def box (create-box))
(.add scene box)

(defn draw [{:keys [x y] :as world-state}]
  (aset box "rotation" "x" x)
  (aset box "rotation" "y" y)
  (.render renderer scene camera))

(big-bang!
  :initial-state {:x 0 :y 0}
  :on-tick
  (fn [event world-state]
    (-> world-state
        (update-in [:x] #(+ 0.07 %))
        (update-in [:y] #(+ 0.05 %))))
  :to-draw draw)
