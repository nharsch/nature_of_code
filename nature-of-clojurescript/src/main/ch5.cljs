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
(def cons-len 20)
(def chain-len 5)

; stateful!
(def engine)
(def circles)
(def world)
(def ground)
(def state)



(defn cleanup-cirlces [circles]
  ; TODO: will these automatically clean up constraints?
  (filter #(< (.. (:body %) -position -y) height) circles))



(defn setup []
  (set! engine (.create m/Engine))
  (set! world (.-world engine))
  ;; (set! ground (p/boundary 200 300 (- width  50) 20 0.1))
  ;; (.add m/Composite world (:body ground))
  (let [canvas (.getElementById js/document "defaultCanvas0")  ; hacky but works, not sure how to grab onto quil canvas html el
        canvas-mouse (.create m/Mouse canvas)
        opts #js { "mouse" canvas-mouse }
        mConstraint (.create m/MouseConstraint engine opts)]
    (.add m/Composite world mConstraint)
    ; retina fix
    (set! (.-pixelRatio (.-mouse mConstraint)) (q/pixel-density))
    )
  (set! state (atom {:chain [] :clicked? false}))
  (let [chain (let [xstart (/ width 2)]
                (for [xoff (range chain-len)]
                  (if (= xoff 0)
                    (p/circle xstart 100 10 true)
                    (p/circle (+ xstart (* xoff cons-len)) 100 10)
                    )))]
    ; add chain to state
    (swap! state assoc :chain chain)
    ; add chain-links to world
    (doseq [c chain] (.add m/Composite world (:body c)))
    ; add constraints to world
    (doseq [[idx c] (map-indexed vector chain)]
      (if (not (= idx 0))
        (let [prev (:body (nth chain (- idx 1)))]
                                        ; create constraint connecting to previous
          (.add m/Composite world
                (.create m/Constraint #js {"bodyA" (:body (nth chain (- idx 1)))
                                           "bodyB" (:body c)
                                           "length" cons-len
                                           "stiffness" 0.1})))))))


(defn draw []
  (q/background 51)
  (q/no-stroke)
  (.update m/Engine engine)
  ;; (p/render ground)

  ;; (println (map #(.-isStatic (:body %)) (:chain @state)))

  (doseq [b (:chain @state)]
    (p/render b))

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
