(ns intro
  (:require [goog.object :as g]
            [goog.dom :as d]
            [p5 :as p5]))

;; (defonce perlin-div (d/getEelment "perlin-noise"))

(defn setup[p]
  (.createCanvas p 640 640))

(defn draw[p]
  (.fill p 255)
  (.ellipse p (.-mouseX p) (.-mouseY p) 80 80))

(defonce parent-id "perlin-noise")

;; (when-not (d/getElement parent-id) (d/append js/document.body (d/createDom "div" #js {:id parent-id})))


(new p5
     (fn [p]
       (set! (.-setup p) (fn [] (setup p)))
       (set! (.-draw p) (fn [] (draw p))))
     parent-id)
