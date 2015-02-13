(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]
            [webgl.ui :as ui]
            [webgl.entity :as entity]))

(enable-console-print!)

(def app-state {})

(def width (- window/innerWidth 400))
(def height window/innerHeight)

(defn create-camera [[window-width window-height]]
  (let [aspect (/ window-width window-height)
        fov 75
        near-clip 0.1
        far-clip 1000]
    (THREE.PerspectiveCamera. fov aspect near-clip far-clip)))

(defn init-three! [renderer camera [window-width window-height]]
  (do
    (.setSize renderer window-width window-height)
    (.setClearColor renderer 0xffffff)
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
    (ui/render-ui)
    app-state))

(defn start []
  (-> app-state
      (assoc :window-size [width height])
      (assoc :scene (THREE.Scene.))
      (assoc :camera (create-camera [width height]))
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
