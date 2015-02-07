(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]))

(enable-console-print!)

(def window-width  window/innerWidth)
(def window-height window/innerHeight)

(def scene    (THREE.Scene.))
(def camera (THREE.PerspectiveCamera. 75
                                      (/ window-width
                                         window-height)
                                      0.1
                                      1000))

(def renderer (THREE.WebGLRenderer.))

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

(def vertex-shader
  "void main ()  {
     gl_Position = projectionMatrix *
                   modelViewMatrix *
                   vec4 ( position, 1.0 );
  }")

(def fragment-shader
 "void main ()  {
   gl_FragColor = vec4 (1.0, 0.0, 1.0, 1.0);
 }")

(defn create-material []
  (THREE.ShaderMaterial. #js
                        {:vertexShader vertex-shader
                         :fragmentShader fragment-shader}))

(defn create-box []
  (let [geo       (THREE.BoxGeometry. 1 1 1)
        material  (create-material)]
    (THREE.Mesh. geo material)))

(defn add-box-to-scene! [scene]
  (let [box (create-box)]
    (.add scene box)
    box))

(defn init! [scene renderer camera]
  (init-three! renderer camera)
  (init-lights! scene))

(defn draw [{:keys [x y box] :as world-state}]
  (aset box "rotation" "x" x)
  (aset box "rotation" "y" y)
  (.render renderer scene camera))

(init! scene renderer camera)

(big-bang!
  :initial-state {:x 0 :y 0 :box (add-box-to-scene! scene)}
  :on-tick
  (fn [event world-state]
    (-> world-state
        (update-in [:x] #(+ 0.07 %))
        (update-in [:y] #(+ 0.05 %))))
  :to-draw draw)
