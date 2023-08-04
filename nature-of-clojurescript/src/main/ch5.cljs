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
(def circles)
(def world)
(def ground)
(def state)



(defn setup []
  (set! engine (.create m/Engine))
  (set! world (.-world engine))
  (set! ground (p/boundary 200 200 (- width  50) 20 -0.3))
  (set! state (atom {:circles []}))
  (.add m/Composite world (:body  ground))
  )



(defn draw []
  (q/background 51)
  (q/no-stroke)
  ;; (q/frame-rate 10)
  (.update m/Engine engine)

  ;; TODO: draw circles
  ;; draw gound
  (p/render ground)

  (doseq [b (:circles @state)]
    (p/render b))

  (if (q/mouse-pressed?)
    (let [circle (p/circle (q/mouse-x) (q/mouse-y) 10)]
      (.add m/Composite world (:body circle))
      (swap! state update :circles conj circle))
    )

  )

(comment
  (.-position (:body (first (:circles @state))))
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
