(ns ch2
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]))

(def canvas-id "ch2-canvas")

(def state (atom {}))

(defn setup []
  (q/background 0)
  (swap! state assoc :mvrs [(m/create-mvr
                             (Vector. 100 200)
                             (Vector. 0 0)
                             10)
                            (m/create-mvr
                             (Vector. 200 200)
                             (Vector. 0 0)
                             20)]))

(defn draw []
  (q/background 0)
  (q/stroke-weight 1)
  (q/stroke 5 0)
  (doseq [[i m] (map-indexed vector (:mvrs @state))]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m)) (* 2 (:mass m)) (* 2 (:mass m)))
    (let [wind (Vector. -0.5 0)
          ;; gravity is scaled to mass
          gravity (u/vmult (Vector. 0 0.5) (:mass m))
          ;; friction is opposing vel, normalized, mag set to friction coefficient * mass
          friction (.setMag (.normalize (u/vmult (:vel m) -1))
                            (* 0.01 (:mass m)))
          ]
      (swap! state assoc-in [:mvrs i]
             (-> m
                 (#(if (q/mouse-pressed?) (m/mvr-apply-force % wind) %))
                 (#(if (>= (.-y (:loc %)) 400) (m/mvr-apply-force % friction) %))
                 (m/mvr-apply-force gravity)
                 m/update-mvr
                 (m/mvr-edges 400 400))))))


(comment
  (def v (Vector. 1 1))
  v
  (.normalize (u/vmult v -1))
  (map-indexed vector (:mvrs @state))
  (get-in @state [:mvrs 1])
  (m/mvr-apply-force (:mvr @state)
                     (Vector. 1 1)))

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "force-mvr")
    (q/defsketch fv
      :host "force-mvr"
      :title "force-mvr"    ;; Set the title of the sketch
      ;; :settings #(q/smooth 2) ;; Turn on anti-aliasing
      :setup setup
      :draw draw
      :size [400 400])))
