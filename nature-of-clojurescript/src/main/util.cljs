(ns util (:require [goog.dom :as d]
                               [p5 :as p5]))

(defn render-sketch-to-canvas [sketch-fn id]
  (if (d/getHTMLElement id)
    (p5. sketch-fn id)))
