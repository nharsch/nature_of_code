(ns util (:require [p5 :refer [Vector]]))


(defn render-sketch-to-canvas [setup-fn draw-fn id]
  (if (.getElementById js/document id)
    (p5. (fn [s]
           (set! (.-setup s) (fn [] (setup-fn s)))
           (set! (.-draw s) (fn [] (draw-fn s))))))
  (println "no id found: " id))

(defn text-on-canvas [p text color]
  (do
    (.noStroke p)
    (.fill p color)
    (.text p text 10 10)))

(defn weighted-random-vec [xprob yprob dist]
  (let [xr (- (rand 2) 1)
        yr (- (rand 2) 1)]
    (Vector. (if (< xprob xr) (* dist -1) dist)
             (if (< yprob yr) (* dist -1) dist))))

(defn montecarlo []
  (let [r1 (rand 1)
        r2 (rand 1)]
    (if (< r2 r1) r1 (montecarlo))))

(defn create-div [mount-id div-id]
  (let [m (.getElementById js/document mount-id)]
    (if (and m (not (.getElementById js/document div-id)))
      (let [d (.createElement js/document "div")]
        (.setAttribute d "id" div-id)
        (.append m d)))))


(def vadd (.-add Vector))
(def vsub (.-sub Vector))
(def vmult (.-mult Vector))
