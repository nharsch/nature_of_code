(ns util (:require [p5 :refer [Vector]]))


(defn render-sketch-to-canvas [setup-fn draw-fn id]
  (if (.getElementById js/document id)
    (p5. (fn [p]
           (set! (.-setup p) (fn [] (setup-fn p)))
           (set! (.-draw p) (fn [] (draw-fn p))))))
  (println "no id found: " id))

(defn text-on-canvas [p text color]
  (do
    (.noStroke p)
    (.fill p color)
    (.text p text 10 10)))

(defn weighted-random-vec [xprob yprob]
  (let [xr (- (rand 2) 1)
        yr (- (rand 2) 1)]
    (Vector. (if (< xprob xr) -1 1)
             (if (< yprob yr) -1 1))))
