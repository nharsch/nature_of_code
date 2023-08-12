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


;; TODO: replace these to calc with native clojure data structures and replace in code
(def vadd (.-add Vector))
(def vsub (.-sub Vector))
(def vmult (.-mult Vector))
(def vdiv (.-div Vector))
(def from-angle (.-fromAngle Vector))

; clojure vector support
(defn vmag [v]
  (Math/sqrt (reduce + (map #(* % %) v))))

(defn scale-vector [v scale-factor]
  (map (partial * scale-factor) v))

(defn set-magnitude [v new-magnitude]
  (let [current-magnitude (vmag v)
        scale-factor (/ new-magnitude current-magnitude)]
    (scale-vector v scale-factor)))

(defn vheading [vec]
  (Math/atan2 (second vec) (first vec)))

(defn vlimit [v limit-mag]
  (if (< (vmag v) limit-mag)
    v
    (set-magnitude v limit-mag)))


(defn edges [posv width height]
  "constrain position wrapped to canvas size"
  (let [[x y] posv]
       [(mod x width) (mod y height)]))

(defn maprange [[a1 a2] [b1 b2] s]
	(+ b1 (/ (* (- s a1) (- b2 b1)) (- a2 a1))))
