(ns ch3
  (:require [util :as u]
            [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [mover :as m]
            [cljs.math :as math]))

(def canvas-id "ch3-canvas")

(def width 400)
(def height 400)

*clojurescript-version*


(def state (atom  0))

(comment
  (math/to-degrees (.heading (Vector. 0 0)))
  (math/to-radians 1)
  (u/from-angle 1)
  )

(defn setup []
  (q/background 0))

(defn draw []
  (q/background 0)
  (q/stroke 200)
  (q/translate (/ width 2) (/ height 2))
  (q/rotate (math/to-radians @state))
  (q/line  -50 0 50 0)
  (q/ellipse  50 0 8 8)
  (q/ellipse  -50 0 8 8)
  (swap! state #(+ 1 %)))


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "force-mvr")
;;     (q/defsketch fv
;;       :host "force-mvr"
;;       :title "force-mvr"    ;; Set the title of the sketch
;;       :settings #(q/smooth 2) ;; Turn on anti-aliasing
;;       :setup setup
;;       :draw draw
;;       :size [width height])))


(def grav-state (atom  {}))

(defn setup-grav []
  (q/background 0)
  (swap! grav-state assoc :mvrs [
                                 (m/create-mvr (Vector. 200 200) (Vector. 0 0) 100)
                                 (m/create-mvr (Vector. 200 10) (Vector. 1 0) 10)
                                 (m/create-mvr (Vector. 200 30) (Vector. -1 0) 1)
                                 ]))



(defn draw-grav []
  (q/background 0)
  (q/stroke-weight 2)
  (q/stroke 255)
  (q/fill 51)
  (doseq [[i m] (map-indexed vector (:mvrs @grav-state))]
    (q/ellipse (.-x (:loc m)) (.-y (:loc m)) (* 2 (:mass m)) (* 2 (:mass m)))
    (q/push-matrix)
    ;; (q/translate (.-x (:loc m) (.-y (:loc m))))
    (q/translate (.-x (:loc m)) (.-y (:loc m)))
    (q/rotate (.heading (:vel m)))
    (q/line 0 0 (* 10 (.mag (:vel m))) 0)
    (q/line (* 10 (.mag (:vel m))) 0
            (- (* 10 (.mag (:vel m))) 5) -5)
    (q/line (* 10 (.mag (:vel m))) 0
            (- (* 10 (.mag (:vel m))) 5) 5)
    (q/pop-matrix)
    (let [gforces (m/get-gforces-for-m m (:mvrs @grav-state))]
      (swap! grav-state assoc-in [:mvrs i]
             (-> m
                 (m/apply-forces-to-mvr gforces)
                 m/update-mvr
                 ;; (m/mvr-edges 400 400)
                 )))))

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "grav-mvr")
;;     (q/defsketch fv
;;       :host "grav-mvr"
;;       :title "grav-mvr"
;;       :setup setup-grav
;;       :draw draw-grav
;;       :size [400 400])))



(def tf-angle (atom  0))
(def tf-rad (atom 120))

(defn setup-trig-funs []
  (q/background 0))


(defn draw-trig-funs []
  (q/translate 200 200)
  (let [r 130
        x (* @tf-rad (Math/cos @tf-angle))
        y (* @tf-rad (Math/sin @tf-angle))]
    ;; (q/background 0)
    (q/stroke-weight 2)
    (q/stroke 255)
    (q/no-fill)
    ;; (q/ellipse 0 0 (* @tf-rad 2) (* @tf-rad 2))
    (q/stroke-weight 12)
    (q/stroke 252 238 33)
    (q/point x y)
    (swap! tf-angle #(+ % 0.02))
    (swap! tf-rad #(- % 0.07))
    )
  )

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "trig-funs-mvr")
;;     (q/defsketch fv
;;       :host "trig-funs-mvr"
;;       :title "trig-funs-mvr"
;;       :setup setup-trig-funs
;;       :draw draw-trig-funs
;;       :size [400 400])))

(def start-angle (atom  0))

(defn setup-harm-funs []
  (q/background 255)
  )

(comment (Math/cos 1))

(defn draw-harm-funs []
  (let [period 30
        amplitude 100
        s (* amplitude
             (Math/sin (/ (* (* 2 Math/PI) (q/frame-count))
                          period)))
        c (* amplitude
             (Math/cos (/ (* (* 2 Math/PI) (q/frame-count))
                          period)))
        t (* amplitude
             (Math/tan (/ (* (* 2 Math/PI) (q/frame-count))
                          period)))]
    ;; (println x)
    (q/background 255)
    ;; (q/stroke 1)
    ;; (q/stroke-weight 1)
    (q/fill 200)
    (q/translate 0 (/ height 2))

    (doseq [x (range (/ width 10))] (q/ellipse (* 10 x)
                                        (* amplitude (Math/sin (/ (+ @start-angle (* 10 x)) period)))
                                        10
                                        10))

    ;; (q/begin-shape)
    ;; (doseq [x (range width)] (q/vertex x (* amplitude (Math/cos (/ (+ @start-angle x) period)))))
    ;; (q/end-shape)

    ;; (q/begin-shape)
    ;; (doseq [x (range width)] (q/vertex x (* amplitude (Math/tan (/ (+ @start-angle x) period)))))
    ;; (q/end-shape)

    (swap! start-angle inc)
    ;; (q/line 0 10 c 10)
    ;; (q/line 0 20 t 20)
    )
)

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "harm-funs-mvr")
;;     (q/defsketch fv
;;       :host "harm-funs-mvr"
;;       :title "harm-funs-mvr"
;;       :setup setup-harm-funs
;;       :draw draw-harm-funs
;;       :size [width height])))

(def af-phase (atom 0))
(defn setup-additive-funs []
  (q/background 255))


(defn wave-fn [amplitude period]
  (fn [x phase]
    (* amplitude (Math/sin (+ phase
                              (/ (* (* 2 Math/PI) x) period))))))

(def waves (for [x (range 4)] (wave-fn (+ 10 (rand 70))
                                       (+ 10 (rand 20)))))


(defn draw-additive-funs []
  ;; (println x)
    (q/background 255)
    ;; (q/stroke 1)
    ;; (q/stroke-weight 1)
    (q/fill 200)
    (q/translate 0 (/ height 2))

  (doseq [x (range (/ width 10))]
    (let [y (reduce + (map  #(% x @af-phase) waves))]
      (q/ellipse (* 10 x) y 10 10)))

    ;; (q/begin-shape)
    ;; (doseq [x (range width)] (q/vertex x (* amplitude (Math/cos (/ (+ @start-angle x) period)))))
    ;; (q/end-shape)

    ;; (q/begin-shape)
    ;; (doseq [x (range width)] (q/vertex x (* amplitude (Math/tan (/ (+ @start-angle x) period)))))
    ;; (q/end-shape)

    (swap! af-phase #(+ % 0.1))
    ;; (q/line 0 10 c 10)
    ;; (q/line 0 20 t 20)

)

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "additive-funs-mvr")
;;     (q/defsketch fv
;;       :host "additive-funs-mvr"
;;       :title "additive-funs-mvr"
;;       :setup setup-additive-funs
;;       :draw draw-additive-funs
;;       :size [width height])))

(def sf-state (atom {:bob (Vector. 205 400)
                     :anchor (Vector. 200 0)
                     :rest-length 150
                     :y 125
                     :k 0.008
                     :velocity (Vector. 0 0)
                     :gravity (Vector. 0 0.1)
                     }))

(defn setup-spring-forces [])

(defn draw-spring-forces []
  ;; (println x)
  (q/background 112 50 126)
  (q/no-stroke)
  (q/fill 45 197 244)
  (q/stroke 255)
  (q/line (.-x (:anchor @sf-state))
          (.-y (:anchor @sf-state))
          (.-x (:bob @sf-state))
          (.-y (:bob @sf-state)))
  (q/ellipse (.-x (:bob @sf-state))
               (.-y (:bob @sf-state))
               64 64)
  (q/ellipse (.-x (:anchor @sf-state))
               (.-y (:anchor @sf-state))
               32 32)


    (let [f (u/vsub (:bob @sf-state)
                        (:anchor @sf-state))
          x (- (.mag f) (:rest-length @sf-state))
          force (-> f
                   .normalize
                   (.mult (:k @sf-state))
                   (.mult -1)
                   (.mult x)
                   )]
      (swap! sf-state update :velocity u/vadd force)
      (swap! sf-state update :bob u/vadd (:velocity @sf-state))
      (swap! sf-state update :velocity u/vmult 0.99)
      (swap! sf-state update :velocity u/vadd (:gravity @sf-state)))
  (if (q/mouse-pressed?)
    (do
      (swap! sf-state assoc :bob (Vector. (q/mouse-x) (q/mouse-y)))
      (swap! sf-state assoc :velocity (Vector. 0 0)))
    )
  )

(comment
  @sf-state
  (:velocity @sf-state)
  (:y @sf-state)
  )

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "spring-forces-mvr")
    (q/defsketch fv
      :host "spring-forces-mvr"
      :title "spring-forces-mvr"
      :setup setup-spring-forces
      :draw draw-spring-forces
      :size [width height])))
