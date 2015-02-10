(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]
            [webgl.sample-material :as mat]))

(enable-console-print!)

(def app-state {})

(defn create-camera [[window-width window-height]]
  (let [aspect (/ window-width window-height)
        fov 75
        near-clip 0.1
        far-clip 1000]
    (THREE.PerspectiveCamera. fov aspect near-clip far-clip)))

(defn init-three! [renderer camera [window-width window-height]]
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

(defn add-entities-to-scene! [scene entities]
  (doseq [entity entities]
    (.add scene (:mesh entity))))

(defn create-sphere [size]
  (let [geo       (THREE.SphereGeometry. size size size)
        material  (mat/create-material geo)]
    {:mesh (THREE.Mesh. geo material)
     :rx 0 :ry 0 :rz 0}))

(defn create-entities []
  (vector (create-sphere 30)))

(defn init! [{:keys [scene renderer camera window-size entities] :as app-state}]
  (do
    (init-three! renderer camera window-size)
    (init-lights! scene)
    (add-entities-to-scene! scene entities)
    app-state))

(defn start []
  (-> app-state
      (assoc :window-size [window/innerWidth window/innerHeight])
      (assoc :scene (THREE.Scene.))
      (assoc :camera (create-camera [window/innerWidth window/innerHeight]))
      (assoc :renderer (THREE.WebGLRenderer.))
      (assoc :entities (create-entities))
      (init!)))

(defn lerp-to-target [current target rate]
  (+ (* current (- 1 rate)) (* target rate)))

(defn update-amplitude! [mesh current target]
  (let [current (lerp-to-target current target 0.2)]
    (aset mesh "material" "uniforms" "amplitude" "value" current)
    current))

(defn calculate-new-target [current target]
  (let [current (if (nil? current) 0 current)
        target  (if (nil? target) 0 target)
        distance (- target current)]
   (if (<= distance 0.1)
     (+ 0.2 target)
     target)))

(defn update-entity [{:keys [mesh current-scale target-scale] :as entity}]
  (-> entity
      #_(update-in [:rx]  #(do (aset mesh "rotation" "x" %)
                             (+ 0.001 %)))
      #_(update-in [:ry]  #(do (aset mesh "rotation" "y" %)
                             (+ 0.001 %)))
      #_(update-in [:target-scale] #(calculate-new-target current-scale %))
      (update-in [:current-scale] #(update-amplitude! mesh % 4))
      entity))

(defn tick [event app-state]
  (-> app-state
      (update-in [:entities] #(map update-entity %))))

(defn draw [{:keys [scene camera renderer]}]
  (.render renderer scene camera))

(big-bang!
  :initial-state (start)
  :on-tick tick
  :to-draw draw)
