(ns webgl.entity
  (:require [webgl.sample-material :as mat]))

;Create
(defn create-sphere [size]
  (let [geo       (THREE.SphereGeometry. size size size)
        material  (mat/create-material geo)]
    {:mesh (THREE.Mesh. geo material)
     :rx 0 :ry 0 :rz 0
     :amplitude 0}))

(defn add-entities-to-scene! [scene entities]
  (doseq [entity entities]
    (.add scene (:mesh entity))))

(defn create-entities []
  (vector (create-sphere 30)))

;Update
(defn lerp-to-target [current target rate]
  (+ (* current (- 1 rate)) (* target rate)))

(defn apply-amplitude! [mesh amplitude]
  (aset mesh "material" "uniforms" "amplitude" "value" amplitude)
  amplitude)

(defn scale-entity
  [{:keys [mesh amplitude] :as entity} max-amplitude expand-rate]
  (let [amplitude (lerp-to-target amplitude max-amplitude expand-rate)]
    (update-in entity [:amplitude] #(apply-amplitude! mesh amplitude))))

(defn spin-entity [{:keys [mesh] :as entity} spin-rate]
  (-> entity
        (update-in [:rx]            #(do (aset mesh "rotation" "x" %)
                                         (+ spin-rate %)))
        (update-in [:ry]            #(do (aset mesh "rotation" "y" %)
                                         (+ spin-rate %)))))

(defn update-entity [{:keys [mesh scale target-scale direction] :as entity}]
  (let [spin-rate 0.0005
        expand-rate 0.005
        max-amplitude 2]
  (-> entity
      (scale-entity max-amplitude expand-rate)
      (spin-entity spin-rate))))
