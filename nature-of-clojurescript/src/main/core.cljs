(ns core
  (:require [quil.core :as q :include-macros true]))

(defn setup []
  (q/frame-rate 0.1)
  (q/background 200))

(defn draw []
  (q/stroke (q/random 255))
  (q/stroke-weight (q/random 10))
  (q/clear)
  (q/background 200)
  (q/fill (q/random 255))

  (let [diam (q/random 200)
        x (/ (q/width) 2)
        y (/ (q/height) 2)]
    (q/ellipse x y diam diam)))

(q/defsketch hello
  :setup setup
  :draw draw
  :host "test"
  :size [323 200])

(defn init []
  (println  "test")
  (draw))
