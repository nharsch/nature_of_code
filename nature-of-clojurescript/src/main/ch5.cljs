(ns ch5
  (:require
            [quil.core :as q :include-macros true]
            [util :as u]
            ["matter-js" :as m]
            ))

(def canvas-id "ch5-canvas")
(def width 400)
(def height 400)

(def engine)
(def boxes)
(def world)
(def ground)


(def state (atom {}))

(defn setup []
  (set! engine (.create m/Engine))
  (set! world (.-world engine))
  (set! ground (.rectangle m/Bodies 0 300 width 100 #js {"isStatic" true}))
  (.add m/Composite world ground)
  )



(defn draw []
  (q/background 51)
  (q/no-stroke)
  (.update m/Engine engine)
  ;; TODO: draw boxes
  ;; draw gound
  (println (.-position ground))
)



(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "physics-engine")
    (q/defsketch fv
      :host "physics-engine"
      :title "physics-engine"
      :setup setup
      :draw draw
      :size [width height])))
