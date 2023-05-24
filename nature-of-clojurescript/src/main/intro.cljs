(ns intro
  (:require [util :as u]))

; set xoff at beginning of 1D noise space
;; (defonce xoff (atom 0))
; set yoff for from xoff, different part of noise space
;; (defonce yoff (atom 10000))
(defonce start (atom 0))
(def canvas-name "intro-canvas")
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
  (u/text-on-canvas p "1d perlin noise" 255)
  (reset! start (+ @start speed)))




(defonce zoff (atom 0))

(defn get-pix-index-for-canvas [width x y]
  (* 4 (+ x (* y width))))

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
  (u/text-on-canvas p "2d perlin noise" 255)
  (reset! zoff (+ @zoff (* speed 0.7))))






;; random number distribution
;;
(def randomCounts (atom (vec (replicate 20 0))))
(defn rc-setup [p]
  (.createCanvas p width height))

(comment
  (update [0 1] 0 inc)
  (count @randomCounts))

(defn rc-draw [p]
  (.background p 255)
  (swap! randomCounts
         (fn [rc] (update rc (rand-int (count rc)) inc)))
  (.stroke p 0)
  (.fill p 175)
  (let [w (/ width (count @randomCounts))]
    (doseq [x (range (count @randomCounts))]
      (.rect p
             (* x w) (- height (nth @randomCounts x))
             (- w 1) (nth @randomCounts x))))
  (u/text-on-canvas p "random number distro" 0)
  )

(do
  (u/render-sketch-to-canvas perlin-setup perlin-draw canvas-name)
  (u/render-sketch-to-canvas perlin-setup two-d-perlin-draw canvas-name)
  (u/render-sketch-to-canvas rc-setup rc-draw canvas-name)
)
