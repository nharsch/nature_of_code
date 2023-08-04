(ns ch5
  (:require
            [quil.core :as q :include-macros true]
            [util :as u]
            ["matter-js" :as m]
            [physics :as p]
            ))

(def canvas-id "ch5-canvas")
(def width 400)
(def height 400)

(def engine)
(def boxes)
(def world)
(def ground)
(def state)



(defn setup []
  (set! engine (.create m/Engine))
  (set! world (.-world engine))
  (set! ground (p/boundary 200 height (- width  50) 100))
  (set! state (atom {:boxes []}))
  (.add m/Composite world (:body  ground))
  )



(defn draw []
  (q/background 51)
  (q/no-stroke)
  ;; (q/frame-rate 10)
  (.update m/Engine engine)

  ;; TODO: draw boxes
  ;; draw gound
  (p/render ground)

  (doseq [b (:boxes @state)]
    (p/render b))

  (if (q/mouse-pressed?)
    (let [box (p/box (q/mouse-x) (q/mouse-y) 20 20)]
      (.add m/Composite world (:body box))
      (swap! state update :boxes conj box))
    )

  )

(comment
  (.-position (:body (first (:boxes @state))))
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
