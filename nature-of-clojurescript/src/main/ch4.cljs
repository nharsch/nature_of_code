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

(def origv (Vector. (/ width 2) (/ height 2)))

(def spawn-fn #(p/new-random-particle origv width height 255 2 5))

(defn setup-particles []
  ;; (q/frame-rate 30)
  (reset! state {:particles (take 10 (repeatedly spawn-fn))}))

(defn cleanup-particles [parts]
  (filter #(> (:lifetime %) 0) parts)
  )

(defn draw-particles []
  (q/background 112 50 126)
  (q/no-stroke)
  (q/fill 45 197 244)
  (println (count (:particles @state)))
  (doseq [p (:particles @state)]
    ;; (q/stroke 0 (:lifetime p))
    (q/fill 175 (:lifetime p))
    (q/ellipse (.-x (:pos p))
               (.-y (:pos p))
               (:r p)
               (:r p)))
  (swap! state assoc :particles (->> (conj (:particles @state) (spawn-fn))
                                     (map #(p/apply-force % (Vector. 0 1)))
                                     (map p/update)
                                     (map p/edges)
                                     cleanup-particles
                                     )))



(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "particles")
    (q/defsketch fv
      :host "particles"
      :title "particles"
      :setup setup-particles
      :draw draw-particles
      :size [width height])))
