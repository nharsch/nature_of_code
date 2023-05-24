(ns util (:require [p5 :as p5]))


(defn render-sketch-to-canvas [setup-fn draw-fn id]
  (if (.getElementById js/document id)
    (p5. (fn [p]
           (set! (.-setup p) (fn [] (setup-fn p)))
           (set! (.-draw p) (fn [] (draw-fn p))))))
  (println "no id found: " id))

(defn text-on-canvas [p text color]
  (do
    (.fill p color)
    (.text p text 10 10)))
