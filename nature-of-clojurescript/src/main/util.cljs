(ns util (:require [goog.dom :as d]
                               [p5 :as p5]))

(defn render-sketch-to-canvas [setup-fn draw-fn id]
  (if (d/getHTMLElement id)
    (p5. (fn [p]
           (set! (.-setup p) (fn [] (setup-fn p)))
           (set! (.-draw p) (fn [] (draw-fn p))))
         id)))
