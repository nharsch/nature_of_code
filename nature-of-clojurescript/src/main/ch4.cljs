(ns ch4
  (:require [util :as u]
            ; TODO: build a custom vecor class that will be data
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]
            [cljs.math :as math]
            [particle :as p]))

(def canvas-id "ch4-canvas")
(def width 400)
(def height 400)
(def lifetime 255)
(def decay 10)
(def radius 7)
(def origv [(/ width 2) (/ height 2)])
(def grav [0 1])
(def state (atom {}))


(defn spawn-part [locv width height lifetime r decay]
  (p/new-random-particle locv width height lifetime r decay))

(defn cleanup-particles [parts]
  (filter #(> (:lifetime %) 0) parts))

(defn update-particles [parts spawn-fn force]
  (->> parts
       (map #(p/apply-force % force))
       (map p/update)
       (map p/edges)
       cleanup-particles))


(defn setup-particles []
  ;; (q/frame-rate 30)
  (reset! state {:emitter-locs (set [origv])
                 :particles []}))

(comment
  ;; (set [(Vector. 0 0) (Vector. 0 0)])
  (map - [0 1] [0 1] [0 1])
  (map * [0 2] [0 2])
  )



(defn draw-particles []
  (q/background 112 50 126)
  (q/no-stroke)
  (q/fill 45 197 244)
  ;; (println (:particles @state))

  ; TODO: add emitter at mouse position on click
  (if (q/mouse-pressed?)
    (swap! state update :emitter-locs #(conj % [(q/mouse-x) (q/mouse-y)])))

  ; spawn particle for every emitter
  (doseq [loc (:emitter-locs @state)]
    (swap! state update :particles #(conj % (spawn-part [(first loc) (second loc)] width height lifetime radius decay))))


  ; draw particles
  (doseq [p (:particles @state)]
    ;; (q/stroke 0 (:lifetime p))
    (q/fill 175 (:lifetime p))
    (q/ellipse (first (:pos p))
               (second (:pos p))
               (:r p)
               (:r p)))

  ; update particles
  (swap! state update :particles #(update-particles % spawn-part grav)))



(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "particles")
    (q/defsketch fv
      :host "particles"
      :title "particles"
      :setup setup-particles
      :draw draw-particles
      :size [width height])))
