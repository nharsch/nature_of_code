(ns ch4
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]
            [cljs.math :as math]
            [particle :as p]))

(def canvas-id "ch4-canvas")
(def width 400)
(def height 400)


(def state (atom { :particles [] }))
(comment
  (p/new-random-particle)
  )

(defn setup-particles []
  ;; (q/frame-rate 1)
  (reset! state {:particles (take 10 (repeatedly
                                      #(p/new-random-particle (Vector. (/ width 2) (/ height 2))
                                                            width
                                                            height)))}))

(defn draw-particles []
  (q/background 112 50 126)
  (q/no-stroke)
  (q/fill 45 197 244)
  (doseq [p (:particles @state)]
    (q/ellipse (.-x (:pos p))
               (.-y (:pos p))
               10 10))
  (swap! state assoc :particles (->> (:particles @state)
                                     (map #(p/apply-force % (Vector. 0 1)))
                                     (map p/update-pos)
                                     (map p/edges)
                                     )))

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
