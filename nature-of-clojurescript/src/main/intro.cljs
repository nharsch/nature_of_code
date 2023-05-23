(ns intro
  (:require [goog.object :as g]
            [goog.dom :as d]
            [p5 :as p5]))

; set xoff at beginning of 1D noise space
(defonce xoff (atom 0))
; set yoff for from xoff, different part of noise space
(defonce yoff (atom 10000))
(defonce start (atom 0))
(def height 320)
(def width 640)
(def speed 0.01)
(def drift-range 10)


(defn perlin-setup [p]
  (.createCanvas p width height)
  (.frameRate p 30))

(defn perlin-draw [p]
  (.background p 80)
  (.beginShape p)
  (.stroke p 255)
  (.noFill p)
  (doseq [x (range width)]
    (do
     (reset! xoff (+ @xoff speed))
     (.stroke p 255)
     (.vertex p x (* (.noise p @xoff) height))
     ))
  (.endShape p)
  (reset! start (+ @start speed)))



(def perlin
  (new p5
       (fn [p]
         (set! (.-setup p) (fn [] (perlin-setup p)))
         (set! (.-draw p) (fn [] (perlin-draw p))))
       "perlin-noise"))

(comment
  (.clear perlin)
  (.random perlin)
  (.noise perlin 100)
  )
