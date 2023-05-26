(ns ch1
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]))

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
  (q/ellipse (.-x (.add Vector (:pos @state) (:vel @state)))
             (.-y (.add Vector (:pos @state) (:vel @state)))
             30 30)
    (swap! state assoc :pos
           (.add Vector (:pos @state) (:vel @state))))


(q/defsketch example                  ;; Define a new sketch named example
  :host "ch1-canvas"
  :title "ball moves"    ;; Set the title of the sketch
  :settings #(q/smooth 2)             ;; Turn on anti-aliasing
  :setup setup                        ;; Specify the setup fn
  :draw draw                          ;; Specify the draw fn
  :size [width height])                    ;; You struggle to beat the golden ratio

(defn setup-rv [p]
  (.createCanvas p width height)
  (.background p 0)
  (swap! state assoc :pos
         (.createVector p (/ width 2) (/ height 2)))
  (swap! state assoc :vel
         (.createVector p 1 0)))

(defn draw-rv [p]
  (do
    (.stroke p 0 60)
    (.strokeWeight p 2)
    (let [start (Vector. (/ width 2) (/ height 2))
          v (.random2D Vector)
          end (.add Vector start (.mult v (rand-int (/ height 2))))
          ]
      (.line p
             (.-x start)
             (.-y start)
             (.-x end)
             (.-y end)))))
