(ns ch2
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]))

(def canvas-id "ch2-canvas")

(def state (atom {}))

(defn setup []
  (q/background 0)
  (swap! state assoc :mvr (m/create-mvr
                              (Vector. 200 200)
                              (Vector. 0 0)
                              10)))
(defn draw []
  (q/background 0)
  (q/stroke-weight 1)
  (q/stroke 5 0)
  (def wind (Vector. -0.5 0))
  (def gravity (Vector. 0 1))
  (let [m (:mvr @state)]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m))
               30 30)
    (if (q/mouse-pressed?)
      (swap! state update :mvr #(m/mvr-apply-force % wind)))
    (swap! state update :mvr #(m/mvr-apply-force % gravity))
    (swap! state update :mvr m/update-mvr)
    (swap! state update :mvr #(m/mvr-edges % 400 400))))

(comment
  @state
  (setup)
  (m/mvr-apply-force (:mvr @state)
                     (Vector. 1 1))
  )

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "force-mvr")
    (q/defsketch fv
      :host "force-mvr"
      :title "force-mvr"    ;; Set the title of the sketch
      :settings #(q/smooth 2) ;; Turn on anti-aliasing
      :setup setup
      :draw draw
      :size [400 400])
    ))
