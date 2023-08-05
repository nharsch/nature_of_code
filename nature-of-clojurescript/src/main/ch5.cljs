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
(def cons-len 40)

; stateful!
(def engine)
(def circles)
(def world)
(def ground)
(def state)


(defn cleanup-cirlces [circles]
  ; TODO: will these automatically clean up constraints?
  (filter #(< (.. (:body %) -position -y) height) circles))

(defn update-chain! [x y]
  (if (> (count (:chain @state)) 0)
    (let [prev (first (:chain @state))
          new (p/circle x (+ cons-len y) 10)
          constraint (.create m/Constraint #js {
                                                "bodyA" (:body prev)
                                                "bodyB" (:body new)
                                                "length" cons-len
                                                "stiffness" 0.4
                                                })]
      (println (:y (last (:chain @state))))
      ; create constraint but for now, let's not track it
      (.add m/Composite world (:body new))
      (.add m/Composite world constraint)
      ; add the circle to the chain
      (swap! state update :chain conj new))
    ; base case add just a circle
    (let [new (p/circle x y 10)]
      (.add m/Composite world (:body new))
      (swap! state update :chain conj new))))



(defn setup []
  (set! engine (.create m/Engine))
  (set! world (.-world engine))
  (set! ground (p/boundary 200 200 (- width  50) 20 -0.3))
  (.add m/Composite world (:body  ground))
  (set! state (atom {:chain []
                     :clicked? false})))


(defn draw []
  (q/background 51)
  (q/no-stroke)
  (.update m/Engine engine)
  (p/render ground)

  ;; (println (count ( :chain @state)))

  (doseq [b (:chain @state)]
    (p/render b))

  (if (and (q/mouse-pressed?) (not (:clicked? @state)))
    (do
      (swap! state assoc :clicked? true)
      (update-chain! (q/mouse-x) (q/mouse-y))))

  (if (and (not (q/mouse-pressed?)) (:clicked? @state))
    (swap! state assoc :clicked? false))

  (swap! state update :chain cleanup-cirlces)
  )


(comment
  (.-position (:body (first (:chain @state))))
  (.. (:body (first (:chain @state))) -position -x)
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
