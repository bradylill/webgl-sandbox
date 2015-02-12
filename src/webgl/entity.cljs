(ns webgl.entity
  (:require [webgl.sample-material :as mat]))

;Create
(defn create-sphere [size]
  (let [geo       (THREE.SphereGeometry. size size size)
        material  (mat/create-material geo)]
    {:mesh (THREE.Mesh. geo material)
     :rx 0 :ry 0 :rz 0}))

(defn add-entities-to-scene! [scene entities]
  (doseq [entity entities]
    (.add scene (:mesh entity))))

(defn create-entities []
  (vector (create-sphere 30)))

;Update
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
      (update-in [:rx]  #(do (aset mesh "rotation" "x" %)
                             (+ 0.001 %)))
      (update-in [:ry]  #(do (aset mesh "rotation" "y" %)
                             (+ 0.001 %)))
      (update-in [:target-scale] #(calculate-new-target current-scale %))
      (update-in [:current-scale] #(update-amplitude! mesh % target-scale))))
