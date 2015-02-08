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
    (.set (.-position camera) 0 0 100)))

(defn init-lights! [scene]
  (do
    (.add scene (THREE.AmbientLight. 0x808080))
    (let [directional-light (THREE.DirectionalLight. 0xffffff 1)]
      (.set (.-position directional-light) 1 1 1)
      (.add scene directional-light))))

(def vertex-shader
  "
  uniform float amplitude;
  attribute float displacement;
  varying vec3 vNormal;

  void main ()  {
    vNormal = normal;

    vec3 newPosition = position +
                       normal *
                       vec3(displacement * amplitude);

    gl_Position = projectionMatrix *
                  modelViewMatrix *
                  vec4(newPosition, 1.0);
  }")

(def fragment-shader
  "
  varying vec3 vNormal;

  void main ()  {
    vec3 light = vec3(0.5, 0.2, 1.0);
    light = normalize(light);

    float dProd = max(0.0, dot(vNormal, light));

    gl_FragColor = vec4(dProd, dProd, dProd, 1.0);
  }")

(defn attributes [geo]
  (let [values (for [v (.-vertices geo)] (* (rand) 5))]
    {:displacement {:type "f"
                    :value values}}))

(def uniforms {:amplitude {:type "f"
                           :value 0}})

(defn create-material [geo]
  (THREE.ShaderMaterial. (clj->js
                           {:uniforms       uniforms
                            :attributes     (attributes geo)
                            :vertexShader   vertex-shader
                            :fragmentShader fragment-shader})))

(defn create-sphere []
  (let [geo       (THREE.SphereGeometry. 30 30 30)
        material  (create-material geo)]
    (THREE.Mesh. geo material)))

(defn add-sphere-to-scene! [scene]
  (let [sphere (create-sphere)]
    (.add scene sphere)
    sphere))

(defn init! [scene renderer camera]
  (init-three! renderer camera)
  (init-lights! scene))

(defn draw [{:keys [x y sphere] :as world-state}]
  (aset sphere "rotation" "x" x)
  (aset sphere "rotation" "y" y)
  (.render renderer scene camera))

(init! scene renderer camera)

(defn lerp-to-target [current target rate]
  (+ (* current (- 1 rate)) (* target rate)))

(defn update-amplitude! [sphere current target]
  (let [current (lerp-to-target current target 0.2)]
    (aset sphere "material" "uniforms" "amplitude" "value" current)
    current))

(defn calculate-new-target [current target]
  (let [distance (- target current)]
   (if (<= distance 0.1)
     (+ 0.2 target)
     target)))

(big-bang!
  :initial-state {:x 0 :y 0
                  :sphere (add-sphere-to-scene! scene)
                  :target 0 :current 0}
  :on-tick
  (fn [event {:keys [sphere target current] :as world-state}]
    (-> world-state
        (update-in [:target] #(calculate-new-target current %))
        (update-in [:current] #(update-amplitude! sphere % target))
        (update-in [:x] #(+ 0.001 %))
        (update-in [:y] #(+ 0.001 %))))
  :to-draw draw)
