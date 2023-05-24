(ns ch1
  (:require [util :as u]
            [p5 :refer [Vector]]))

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

(defn setup [p]
  (.createP p "random walk")
  (.createCanvas p width height)
  (.background p 0)
  (swap! state assoc :pos
         (.createVector p (/ width 2) (/ height 2))))

(defn draw [p]
  (do
    (.stroke p 255 100)
    (.strokeWeight p 2)
    (.point p (.-x (:pos @state))
              (.-y (:pos @state)))
    (swap! state assoc :pos
           (.add (:pos @state) (.random2D Vector)))))

(u/render-sketch-to-canvas setup draw "ch1-canvas")
