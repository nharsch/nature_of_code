(ns ch1
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]))

(comment
  (.createVector (p5.))
  (createVector)
  (.mag (js/createVector 50 50))
  (js/Vector)
  (def v (js/createVector (/ width 2) (/ height 2)))
  (.random2D Vector))

(def width 400)
(def height 400)

(defonce state (atom {}))

(defn setup []
  (swap! state assoc :pos
         (Vector. (/ width 2) (/ height 2)))
  (swap! state assoc :vel
         (Vector. 0.05 0.05)))

(defn draw []
  (q/background 0)
  (q/stroke 155 100)
  (q/stroke-weight 2)
  (q/ellipse (.-x (u/vadd (:pos @state) (:vel @state)))
             (.-y (u/vadd (:pos @state) (:vel @state)))
             30 30)
  (swap! state assoc :pos
         (u/vadd (:pos @state) (:vel @state))))

(if (.getElementById js/document "ch1-canvas")
  (do
    (u/create-div "ch1-canvas" "ball-moves")
    (q/defsketch example ;; Define a new sketch named example
      :host "ball-moves"
      :title "ball-moves"   ;; Set the title of the sketch
      :settings #(q/smooth 2) ;; Turn on anti-aliasing
      :setup setup            ;; Specify the setup fn
      :draw draw              ;; Specify the draw fn
      :size [width height])))                    ;; You struggle to beat the golden ratio

(def rv-state (atom {}))
(defn setup-rv []
  (q/background 0)
  (swap! rv-state assoc :mvrs (for [x (range 10)]
                                (m/create-mover (Vector. x 0)
                                                (Vector. 0 0)
                                                10))))

; TODO: array of movers
(defn draw-rv []
  (q/background 0)
  ;; (q/stroke-weight 1)
  (q/stroke 5 0)
  (doseq [m (:mvrs @rv-state)]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m))
               10 10))
  (let [updated-mvrs (map
                      (fn [m] (m/move-mvr-toward-point m
                                                       (Vector. (q/mouse-x) (q/mouse-y))
                                                       0.5))
                      (:mvrs @rv-state))]
    (swap! rv-state assoc :mvrs updated-mvrs)
    ))

(if (.getElementById js/document "ch1-canvas")
  (do
    (u/create-div "ch1-canvas" "random-vec")
    (q/defsketch rv
      :host "random-vec"
      :title "random-vec"   ;; Set the title of the sketch
      :settings #(q/smooth 2) ;; Turn on anti-aliasing
      :setup setup-rv         ;; Specify the setup fn
      :draw draw-rv
      :size [width height])))
