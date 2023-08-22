(ns ch6
  (:require
            [quil.core :as q :include-macros true]
            [util :as u]
            [lvector :as lv]
            [vehicle :as vh :refer [->Vehicle]]
            [path :refer [->Path show-path dist-to-path]]))

(def canvas-id "ch6-canvas")
(def width 400)
(def height 400)
(def cons-len 20)
(def chain-len 5)

(def seek-state (atom nil))

(defn seek [v target-posv]
  (let [desiredv (lv/set-magnitude (map - target-posv (:pos v))
                                  (:max-speed v))
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (map - desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (map + (:vel v) steerv)
        new-pos (u/edges (map + (:pos v) new-vel) height width)]
    (assoc v :vel new-vel :pos new-pos)))

(defn flee [v target-posv]
  (let [; desired vectir is opposite
        desiredv (lv/scale-vector (map - target-posv (:pos v))
                                  -1)
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (map - desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (map + (:vel v) steerv)
        new-pos (u/edges (map + (:pos v) new-vel) width height)
        ]
    (assoc v :vel new-vel :pos new-pos)))



(defn setup []
  (reset! seek-state {:driver (->Vehicle [10 10]
                                    [0 0]
                                    0.2
                                    4
                                    7)
                 :target [200 200]}))

(defn draw []
  (q/background 51)
  (q/no-stroke)
  ;; (q/frame-rate 1)

  ;; draw vehicle
  (let [driver (:driver @seek-state)
        [x y] (:pos driver)
        r (:r driver)]
    (q/stroke 255)
    (q/stroke-weight 2)
    (q/fill 255)
    (q/push-matrix)
    (q/translate x y)
    (q/rotate (lv/vheading (:vel driver)))
    (q/triangle (- r) (/ (- r) 2)
                (- r) (/ r 2)
                r 0)
    (q/pop-matrix))

  ;; draw target
  (let [[x y] (:target @seek-state)]
    (q/ellipse x y 20 20))

  (swap! seek-state assoc :target [(q/mouse-x) (q/mouse-y)])
  (if (q/mouse-pressed?)
    (swap! seek-state update :driver #(flee % (:target @seek-state)))
    (swap! seek-state update :driver #(seek % (:target @seek-state)))))


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "autonomous")
;;     (q/defsketch fv
;;       :host "autonomous"
;;       :title "autonomous"
;;       :setup setup
;;       :draw draw
;;       :size [width height])))

;; ---

(def pursue-state (atom nil))

(def lookahead-steps 7)

(defn pursue [pv tv]
  (let [pursuit-v (map -
                       (map + (:pos tv) (lv/scale-vector (:vel tv) lookahead-steps))
                       (:pos pv))
        ; limit speed
        desiredv (lv/set-magnitude pursuit-v (:max-speed pv))
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (map - desiredv (:vel pv)) (:max-force pv))
        ; apply force
        new-vel (map + (:vel pv) steerv)
        new-pos (u/edges (map + (:pos pv) (:vel pv)) width height)]
    ;; slowdown on arrival
    (let [distance (lv/vmag (map - (:pos tv) (:pos pv)))
          slowdown-r (* 1.7 (:r tv))]
      (if (< distance slowdown-r)
        (do
          (assoc pv
                 :vel (lv/set-magnitude new-vel (u/maprange [0 slowdown-r] [0 (:max-speed tv)] distance))
                 :pos new-pos))
        (assoc pv :vel new-vel :pos new-pos)))))

(defn evade [pv tv]
  (let [pursuit-v (lv/scale-vector (map -
                                       (map + (:pos tv) (lv/scale-vector (:vel tv) 4))
                                       (:pos pv))
                                  -1)
        desiredv (lv/set-magnitude pursuit-v (:max-speed pv))
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (map - desiredv (:vel pv)) (:max-force pv))
        ; apply force
        new-vel (map + (:vel pv) steerv)
        new-pos (u/edges (map + (:pos pv) new-vel) width height)]
    (assoc pv :vel new-vel :pos new-pos)))

(defn pursue-setup []
  (reset! pursue-state {:driver (->Vehicle [10 10] [0 0] 0.2 7 16)
                        :target (->Vehicle [100 100] [1 0] 2 5 20)}))

(defn update-target [t]
  (let [next-pos (u/edges (map + (:pos t) (lv/vlimit (:vel t) (:max-speed t)))
                          width
                          height)]
    (assoc t :pos next-pos :vel (lv/vlimit (:vel t) (:max-speed t)))))


(defn pursue-draw []
  (q/background 51)
  (q/no-stroke)
  ;; (q/frame-rate 10)

  ;; draw vehicle
  (let [driver (:driver @pursue-state)
        [x y] (:pos driver)
        r (:r driver)]
    (q/stroke 255)
    (q/stroke-weight 2)
    (q/fill 255)
    (q/push-matrix)
    (q/translate x y)
    (q/rotate (lv/vheading (:vel driver)))
    (q/triangle (- r) (/ (- r) 2)
                (- r) (/ r 2)
                r 0)
    (q/pop-matrix))

  ;; draw target
  (let [[x y] (:pos (:target @pursue-state))
        r (:r (:target @pursue-state))]
    (q/ellipse x y r r))

  ;; update target
  (if (< (lv/vmag (map - (:pos (:driver @pursue-state)) (:pos (:target @pursue-state))))
         (:r (:target @pursue-state)))
    ; spawn new location & vel if caught
    (swap! pursue-state assoc :target (assoc (:target @pursue-state)
                                             :pos [(rand-int width) (rand-int height)]
                                             :vel [(- 3 (rand-int 6)) (- 3 (rand-int 6))]))
    ; else just update
    (swap! pursue-state update :target update-target))

  (if (q/mouse-pressed?)
    (swap! pursue-state update :driver #(evade % (:target @pursue-state)))
    (swap! pursue-state update :driver #(pursue % (:target @pursue-state)))))


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "pursue")
;;     (q/defsketch fv
;;       :host "pursue"
;;       :title "pursue"
;;       :setup pursue-setup
;;       :draw pursue-draw
;;       :size [width height])))


(def wander-state (atom nil))
(def wander-radius 10)

(defrecord Wanderer [pos vel max-force max-speed r wtheta current-path paths])

(defn wander [w]
  (let [
                                        ; set new pos from previous vals
        new-pos (map + (:pos w) (:vel w))
                                        ; angle relative to the line described by vel vector
        theta (+ (:wtheta w) (lv/vheading (:vel w)))
        x (* wander-radius (Math/cos theta))
        y (* wander-radius (Math/sin theta))
        wp (map +
                                        ; line of vel * 100 + pos
                (map +
                     (lv/set-magnitude (:vel w) 100)
                     (:pos w))
                                        ; plus x y offset found from angle of wander offset plus wander radius
                [x y])
                                        ; steering vector is line from  pos to wp
        wv (map - wp (:pos w))
        steer (lv/set-magnitude wv (:max-force w))
        new-vel (lv/vlimit  (map + (:vel w) steer) (:max-speed w))
        new-wt (+ (:wtheta w) (- 0.3 (* 0.6 (rand))))]
    (assoc w :pos (u/edges new-pos width height) :vel new-vel :wtheta new-wt))
  )

(defn wander-setup []
  (reset! wander-state { :w (->Wanderer [100 100] [0.1 0] 0.4 1 16 0)}))

(defn wander-draw []
  (q/background 51)
  (q/no-stroke)
  ;; draw wanderer
  (let [w (:w @wander-state)
        [x y] (:pos w)
        r (:r w)]
    (q/stroke 255)
    (q/stroke-weight 2)
    (q/fill 255)
    (q/push-matrix)
    (q/translate x y)
    (q/rotate (lv/vheading (:vel w)))
    (q/triangle (- r) (/ (- r) 2)
                (- r) (/ r 2)
                r 0)
    (q/pop-matrix))
  ; update wanderer
  (swap! wander-state update :w wander)
  )


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "wander")
;;     (q/defsketch fv
;;       :host "wander"
;;       :title "wander"
;;       :setup wander-setup
;;       :draw wander-draw
;;       :size [width height])))


(defn angle-setup [])

(defn draw-vector [v, pos, scayl]
  (let [arrow-size 6]
    (q/push-matrix)
    (apply q/translate pos)
    (q/stroke 0)
    (q/stroke-weight 2)
    (q/rotate (lv/vheading v))
    (let [len (* scayl (lv/vmag v))]
      (q/line 0 0 len 0)
      (q/line len 0 (- len arrow-size) arrow-size)
      (q/line len 0 (- len arrow-size) (- 0 arrow-size)))
    (q/pop-matrix)))


(defn angle-draw []
  (q/background 200)
  (let [mouseLoc [(q/mouse-x) (q/mouse-y)]
        centerLoc [(/ width 2) (/ height 2)]
        ; v is the normalized vector betwee
        v (map #(* 75 %) (lv/vnorm (map - mouseLoc centerLoc)))
        xaxis [75, 0]]
    (draw-vector v [(/ width 2) (/ height 2)] 1)
    (draw-vector xaxis [(/ width 2) (/ height 2) (/ height 2)] 1)))


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "angle-between")
;;     (q/defsketch fv
;;       :host "angle-between"
;;       :title "angle-between"
;;       :setup angle-setup
;;       :draw angle-draw
;;       :size [width height])))

;; ---

(defn projection-setup [])

(defn projection-draw []
  (q/background 0)
  (q/stroke-weight 4)
  (q/stroke 255)
  (let [pos [100 200]
        mouse [(q/mouse-x) (q/mouse-y)]
        a (map - mouse pos)
        path [200 100]
        v (lv/vector-projection a path)]
    (q/line (pos 0)
            (pos 1)
            (+ (pos 0) (path 0))
            (+ (pos 1) (path 1)))
    (q/no-stroke)
    (q/fill 0 255 0)
    (q/ellipse (+ (a 0) (pos 0))
               (+ (a 1) (pos 1))
               16 16)
    (q/fill 255 0 0)
    (q/ellipse (+ (v 0) (pos 0))
               (+ (v 1) (pos 1))
               16 16)))


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "projection-between")
;;     (q/defsketch fv
;;       :host "projection-between"
;;       :title "projection-between"
;;       :setup projection-setup
;;       :draw projection-draw
;;       :size [width height])))

; --
;
(def simple-path-follow-state (atom nil))
(defn simple-path-follow-setup []
  (reset! simple-path-follow-state { :driver (->Vehicle [10 10]
                                                        [1 1]
                                                        0.2
                                                        2
                                                        7)
                                    :path (->Path [0 (/ height 2)] [width (/ height 2)] 20)
                                    }))



(defn simple-path-follow-draw []
  (q/background 0)
  (show-path (:path @simple-path-follow-state))
  (vh/show-vehicle (:driver @simple-path-follow-state))
  ;; TODO: update
  (swap! simple-path-follow-state update :driver #(-> %
                                                      (vh/follow-path (:path @simple-path-follow-state))
                                                      vh/update-pos
                                                      (update :pos u/edges width height)))
)


;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "simple-path-follow-between")
;;     (q/defsketch fv
;;       :host "simple-path-follow-between"
;;       :title "simple-path-follow-between"
;;       :setup simple-path-follow-setup
;;       :draw simple-path-follow-draw
;;       :size [width height])))

; ---
(def complex-path-follow-state (atom nil))
(defn rand-height []
  (+ (/ height 2)
     (* 0.5 (* height (- 0.5 (rand))))))
(defn complex-path-follow-setup []
  (reset! complex-path-follow-state {:driver (->Vehicle [10 (/ height 2)]
                                                        [1 0.5]
                                                        0.2
                                                        4
                                                        7)
                                    :path-points [[0 (/ height 2)]
                                                  [(/ width 5) (/ height 2)]
                                                  [(* 2 (/ width 5)) (rand-height)]
                                                  [(* 3 (/ width 5)) (rand-height)]
                                                  [(* 4 (/ width 5)) (rand-height)]
                                                  [width (/ height 2)]]
                                    :path-radius 20}))




(defn complex-path-follow-draw []
  (q/background 0)
  (let [
        pos (:pos (:driver @complex-path-follow-state))
        paths (map (fn [[a b]] (->Path a b (:path-radius @complex-path-follow-state)))
                   (partition 2 1 (:path-points @complex-path-follow-state)))
        closest-path (apply min-key #(dist-to-path pos %) paths)
        ]
    (doseq [p paths]
      (println (= p closes-path))
      (show-path p (= p closest-path)))
    (vh/show-vehicle (:driver @complex-path-follow-state))
    (swap! complex-path-follow-state update :driver #(-> %
                                                         (vh/follow-path closest-path)
                                                         vh/update-pos
                                                         (update :pos u/edges width height))))

)


(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "complex-path-follow-between")
    (q/defsketch fv
      :host "complex-path-follow-between"
      :title "complex-path-follow-between"
      :setup complex-path-follow-setup
      :draw complex-path-follow-draw
      :size [width height])))
