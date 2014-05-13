(ns webgl.core
  (:require [big-bang.core :refer [big-bang!]]))

(enable-console-print!)

(big-bang!
  :initial-state {:x 0 :y 0}
  :on-tick
    (fn [event world-state]
      (update-in world-state [:x] inc))
  :to-draw (fn [{:keys [x y]}]
             (println "(" x ", " y ")")))
