(ns ch4
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]
            [cljs.math :as math]
            ; TODO: why do I need to explicitly require a protocol method?
            [particle :refer [Particle, new-random-particle, update-pos]]))


(def canvas-id "ch4-canvas")
(def width 400)
(def height 400)


(def state (atom { :particles [] }))

(comment
  (new-random-particle)
  )

(defn setup-particles []
  (reset! state {:particles (take 10 (repeatedly #(new-random-particle width height)))}))

(defn draw-particles []
  (q/background 112 50 126)
  (q/no-stroke)
  (q/fill 45 197 244)
  (doseq [p (:particles @state)]
    (print p)
    (q/ellipse (.-x (:pos p))
               (.-y (:pos p))
               10 10))
  (swap! state assoc :particles (map update-pos (:particles @state))))

(comment
  (update-pos (first (:particles @state)))
  (map update-pos (:particles @state))
  )


(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "particles")
    (q/defsketch fv
      :host "particles"
      :title "particles"
      :setup setup-particles
      :draw draw-particles
      :size [width height])))
