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
                            (* 0.01 (:mass m)))]
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

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "force-mvr")
;;     (q/defsketch fv
;;       :host "force-mvr"
;;       :title "force-mvr"    ;; Set the title of the sketch
;;       ;; :settings #(q/smooth 2) ;; Turn on anti-aliasing
;;       :setup setup
;;       :draw draw
;;       :size [400 400])))

(def drag-state (atom  {}))

(defn setup-drag []
  (q/background 0)
  (swap! drag-state assoc :mvrs [(m/create-mvr
                                  (Vector. 100 100)
                                  (Vector. 0 0)
                                  10)
                                 (m/create-mvr
                                  (Vector. 200 100)
                                  (Vector. 0 0)
                                  20)
                                 (m/create-mvr
                                  (Vector. 350 100)
                                  (Vector. 0 0)
                                  20)
                                 (m/create-mvr
                                  (Vector. 300 100)
                                  (Vector. 0 0)
                                  20)]))

(defn draw-drag []
  (q/background 0)
  (q/fill 255 125)
  (q/no-stroke)
  (q/rect 0 200 400 200)
  (q/stroke-weight 1)
  (q/stroke 5 0)
  (doseq [[i m] (map-indexed vector (:mvrs @drag-state))]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m)) (* 2 (:mass m)) (* 2 (:mass m)))
    (let [wind (Vector. -0.5 0)
          ;; gravity is scaled to mass
          gravity (u/vmult (Vector. 0 0.5) (:mass m))
          ;; friction is opposing vel, normalized, mag set to friction coefficient * mass
          drag (-> (.copy (:vel m))
                   .normalize
                   (u/vmult -1) ; reverse
                   ;; (u/vmult (:vel m)) ; square
                   (.setMag (* (.magSq (:vel m)) ; speed
                               1)) ; const
                   )]
      (swap! drag-state assoc-in [:mvrs i]
             (-> m
                 (#(if (>= (.-y (:loc m)) 200) (m/mvr-apply-force % drag) %))
                 (m/mvr-apply-force gravity)
                 m/update-mvr
                 (m/mvr-edges 400 400))))))

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "drag-mvr")
;;     (q/defsketch fv
;;       :host "drag-mvr"
;;       :title "drag-mvr"
;;       :setup setup-drag
;;       :draw draw-drag
;;       :size [400 400])))

(def grav-state (atom  {}))

(defn setup-grav []
  (q/background 0)
  ;; (q/frame-rate 10)
  (swap! grav-state assoc :mvrs [
                                 (m/create-mvr (Vector. 200 200) (Vector. 0 0) 100)
                                 (m/create-mvr (Vector. 200 10) (Vector. 0 0) 10)
                                 (m/create-mvr (Vector. 200 30) (Vector. 0 0) 1)
                                 ]))



(defn draw-grav []
  (q/background 0)
  (q/stroke 255)
  (doseq [[i m] (map-indexed vector (:mvrs @grav-state))]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m)) (* 2 (:mass m)) (* 2 (:mass m)))
    (let [gforces (m/get-gforces-for-m m (:mvrs @grav-state))]
      (swap! grav-state assoc-in [:mvrs i]
             (-> m
                 (m/apply-forces-to-mvr gforces)
                 m/update-mvr
                 ;; (m/mvr-edges 400 400)
                 )))))

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "grav-mvr")
    (q/defsketch fv
      :host "grav-mvr"
      :title "grav-mvr"
      :setup setup-grav
      :draw draw-grav
      :size [400 400])))
