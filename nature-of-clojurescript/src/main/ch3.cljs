(ns ch3
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]
            [cljs.math :as math]))

(def canvas-id "ch3-canvas")

(def width 400)
(def height 400)

*clojurescript-version*


(def state (atom  0))

(comment
  (math/to-degrees (.heading (Vector. 0 0)))
  (math/to-radians 1)
  (u/from-angle 1)
  )

(defn setup []
  (q/background 0))

(defn draw []
  (q/background 0)
  (q/stroke 200)
  (q/translate (/ width 2) (/ height 2))
  (q/rotate (math/to-radians @state))
  (q/line  -50 0 50 0)
  (q/ellipse  50 0 8 8)
  (q/ellipse  -50 0 8 8)
  (swap! state #(+ 1 %)))


(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "force-mvr")
    (q/defsketch fv
      :host "force-mvr"
      :title "force-mvr"    ;; Set the title of the sketch
      :settings #(q/smooth 2) ;; Turn on anti-aliasing
      :setup setup
      :draw draw
      :size [width height])))


(def grav-state (atom  {}))

(defn setup-grav []
  (q/background 0)
  (swap! grav-state assoc :mvrs [
                                 (m/create-mvr (Vector. 200 200) (Vector. 0 0) 100)
                                 (m/create-mvr (Vector. 200 10) (Vector. 1 0) 10)
                                 (m/create-mvr (Vector. 200 30) (Vector. -1 0) 1)
                                 ]))



(defn draw-grav []
  (q/background 0)
  (q/stroke-weight 2)
  (q/stroke 255)
  (q/fill 51)
  (doseq [[i m] (map-indexed vector (:mvrs @grav-state))]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m)) (* 2 (:mass m)) (* 2 (:mass m)))
    (q/push-matrix)
    ;; (q/translate (.-x (:loc m) (.-y (:loc m))))
    (q/translate (.-x (:loc m)) (.-y (:loc m)))
    (q/rotate (.heading (:vel m)))
    (q/line 0 0 (* 10 (.mag (:vel m))) 0)
    (q/line (* 10 (.mag (:vel m))) 0
            (- (* 10 (.mag (:vel m))) 5) -5)
    (q/line (* 10 (.mag (:vel m))) 0
            (- (* 10 (.mag (:vel m))) 5) 5)
    (q/pop-matrix)
    (let [gforces (m/get-gforces-for-m m (:mvrs @grav-state))]
      (swap! grav-state assoc-in [:mvrs i]
             (-> m
                 (m/apply-forces-to-mvr gforces)
                 m/update-mvr
                 ;; (m/mvr-edges 400 400)
                 )))))

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "grav-mvr")
    (q/defsketch fv
      :host "grav-mvr"
      :title "grav-mvr"
      :setup setup-grav
      :draw draw-grav
      :size [400 400])))
