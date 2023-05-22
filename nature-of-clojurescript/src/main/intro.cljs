(ns intro
  (:require [goog.object :as g]
            [goog.dom :as d]
            [p5 :as p5]))

;; (defonce perlin-div (d/getEelment "perlin-noise"))
;;
;; (println


(defn perlin-setup [p]
  (.createCanvas p 640 640)
  (.frameRate p 30))

(defonce xoff (atom 0))
(defonce yoff (atom 0))

(def speed 0.02)
(def drift-range 10)

(defn perlin-draw [p]
  (.background p 100)
  (.fill p 200)
  (.stroke p 200)
  (reset! xoff (+ @xoff speed))
  (reset! yoff (+ @yoff speed))
  ;; subtle movement
  (.ellipse p
            (+ (.-mouseX p) (.map p (.noise p @xoff) 0 1 (* -1 drift-range) drift-range))
            (+ (.-mouseY p) (.map p (.noise p @yoff) 0 1 (* -1 drift-range) drift-range))
            24 24))

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
