(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]
            [webgl.entity :as entity]))

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

(defn init! [{:keys [scene renderer camera window-size entities] :as app-state}]
  (do
    (init-three! renderer camera window-size)
    (init-lights! scene)
    (entity/add-entities-to-scene! scene entities)
    app-state))

(defn start []
  (-> app-state
      (assoc :window-size [window/innerWidth window/innerHeight])
      (assoc :scene (THREE.Scene.))
      (assoc :camera (create-camera [window/innerWidth window/innerHeight]))
      (assoc :renderer (THREE.WebGLRenderer.))
      (assoc :entities (entity/create-entities))
      (init!)))

(defn tick [event app-state]
  (-> app-state
      (update-in [:entities] #(map entity/update-entity %))))

(defn draw [{:keys [scene camera renderer]}]
  (.render renderer scene camera))

(big-bang!
  :initial-state (start)
  :on-tick tick
  :to-draw draw)
