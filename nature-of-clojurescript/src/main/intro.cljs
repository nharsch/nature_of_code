(ns intro
  (:require [goog.object :as g]
            [goog.dom :as d]
            [p5 :as p5]
            [util :as u]))

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


(u/render-sketch-to-canvas
 perlin-setup
 perlin-draw
 "1D-perlin-noise")

(defonce zoff (atom 0))
(defn two-d-perlin-draw [p]
  ;; retina display fix
  (.pixelDensity p 1)
  (.loadPixels p)
  (doseq [y (range height)
          x (range width)]
    (let [r (* (.noise p (* x speed) (* y speed) @zoff) 255)
          index (get-pix-index-for-canvas width x y)]
      (do
        (aset (.-pixels p) index r)
        (aset (.-pixels p) (+ index 1) r)
        (aset (.-pixels p) (+ index 2) r)
        (aset (.-pixels p) (+ index 3) 255))))
  ;; NOTE: I prefer this style, but it bogs down rendering as I swap the whole array value each time instead of setting them directly
  ;; (set! (. p -pixels)
  ;;       (js/Uint8ClampedArray. (clj->js (reduce into
  ;;                                              (for [y (range height)
  ;;                                                    x (range width)]
  ;;                                                ;; get noise value
  ;;                                                (let [r (* (.noise p (* x speed) (* y speed) @zoff) 255)]
  ;;                                                  [r r r 255]))))))
  (.updatePixels p)
  ;; move through z axis every draw loop
  (reset! zoff (+ @zoff (* speed 0.7))))

(defn get-pix-index-for-canvas [width x y]
  (* 4 (+ x (* y width))))


(comment
  (/ 3276800 4)
  (js/Uint8ClampedArray. [0 255 400])
  (* 4 width height)
  (count (reduce into (for [y (range 10)
                            x (range 10)]
                        [0 1 2 3])))
  (reduce into (for [x (range width)
                     y (range height)]
                 ['a 'b 'c 'd]))
  (get-pix-index-for-canvas width 0 0)
  )


(u/render-sketch-to-canvas perlin-setup two-d-perlin-draw "2D-perlin-noise")
