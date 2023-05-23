(ns intro
  (:require [goog.object :as g]
            [goog.dom :as d]
            [p5 :as p5]))

; set xoff at beginning of 1D noise space
;; (defonce xoff (atom 0))
; set yoff for from xoff, different part of noise space
;; (defonce yoff (atom 10000))
(defonce start (atom 0))
(def height 320)
(def width 640)
(def speed 0.01)
(def drift-range 10)


(defn perlin-setup [p]
  (.createCanvas p width height)
  (.frameRate p 30))

(defn perlin-draw [p]
  (def xoff (atom @start))
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


(def one-d-perlin
  (new p5
       (fn [p]
         (set! (.-setup p) (fn [] (perlin-setup p)))
         (set! (.-draw p) (fn [] (perlin-draw p))))
       "1D-perlin-noise"))

(defonce zoff (atom 0))
(defn two-d-perlin-draw [p]
  (.loadPixels p)
  (.pixelDensity p 1)
  (let [yoff (atom 0)]
    (doseq [y (range height)]
      (let [xoff (atom 0)]
        (doseq [x (range width)]
          ;; update pixel
          (let [index (* 4 (+ x (* y width)))
                r (* (.noise p @xoff @yoff @zoff) 255)]
            (do
                                        ; TODO: clean this up
              (aset (.-pixels p) index r)
              (aset (.-pixels p) (+ index 1) r)
              (aset (.-pixels p) (+ index 2) r)
              (aset (.-pixels p) (+ index 3) 255))
            (reset! xoff (+ @xoff speed)))))
      (reset! yoff (+ @yoff speed))))
  (.updatePixels p)
  (reset! zoff (+ @zoff (/ speed 2))))

(def two-d-perlin
  (new p5
       (fn [p]
         (set! (.-setup p) (fn [] (perlin-setup p)))
         (set! (.-draw p) (fn [] (two-d-perlin-draw p))))
       "2D-perlin-noise"))
