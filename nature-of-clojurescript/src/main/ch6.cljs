(ns ch6
  (:require
            [quil.core :as q :include-macros true]
            [util :as u]
            ))

(def canvas-id "ch6-canvas")
(def width 400)
(def height 400)
(def cons-len 20)
(def chain-len 5)

(def seek-state (atom nil))

(defrecord Vehicle [pos vel max-force max-speed r])


(defn seek [v target-posv]
  (let [desiredv (u/set-magnitude (map - target-posv (:pos v))
                                  (:max-speed v))
        ; steering vector is distance minus current vel
        steerv (u/vlimit (map - desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (map + (:vel v) steerv)
        new-pos (u/edges (map + (:pos v) new-vel) height width)
        ]
    (assoc v :vel new-vel :pos new-pos)))

(defn flee [v target-posv]
  (let [; desired vectir is opposite
        desiredv (u/scale-vector (map - target-posv (:pos v))
                                  -1)
        ; steering vector is distance minus current vel
        steerv (u/vlimit (map - desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (map + (:vel v) steerv)
        new-pos (u/edges (map + (:pos v) new-vel) width height)
        ]
    (assoc v :vel new-vel :pos new-pos)))


(comment
  (seek (->Vehicle [100 100] [0 0] 10 10) [1 1])
  )

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
    (q/rotate (u/vheading (:vel driver)))
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


(defn pursue [pv tv]
  (let [pursuit-v (map -
                       (map + (:pos tv) (u/scale-vector (:vel tv) 2))
                       (:pos pv))
        ; limit speed
        desiredv (u/set-magnitude pursuit-v (:max-speed pv))
        ; steering vector is distance minus current vel
        steerv (u/vlimit (map - desiredv (:vel pv)) (:max-force pv))
        ; apply force
        new-vel (map + (:vel pv) steerv)
        new-pos (u/edges (map + (:pos pv) new-vel) width height)]
    (assoc pv :vel new-vel :pos new-pos)))

(defn evade [pv tv]
  (let [pursuit-v (u/scale-vector (map -
                                       (map + (:pos tv) (u/scale-vector (:vel tv) 2))
                                       (:pos pv))
                                  -1)
        desiredv (u/set-magnitude pursuit-v (:max-speed pv))
        ; steering vector is distance minus current vel
        steerv (u/vlimit (map - desiredv (:vel pv)) (:max-force pv))
        ; apply force
        new-vel (map + (:vel pv) steerv)
        new-pos (u/edges (map + (:pos pv) new-vel) width height)]
    (assoc pv :vel new-vel :pos new-pos)))

(defn pursue-setup []
  (println "pursue setup")
  (reset! pursue-state {:driver (->Vehicle [10 10] [0 0] 0.2 4 7)
                        :target (->Vehicle [100 100] [1 0] 2 5 20)}))

(defn update-target [t]
  (let [next-pos (u/edges (map + (:pos t) (u/vlimit (:vel t) (:max-speed t)))
                          width
                          height)]
    (assoc t :pos next-pos)))


(defn pursue-draw []
  (q/background 51)
  (q/no-stroke)

  ;; draw vehicle
  (let [driver (:driver @pursue-state)
        [x y] (:pos driver)
        r (:r driver)]
    (q/stroke 255)
    (q/stroke-weight 2)
    (q/fill 255)
    (q/push-matrix)
    (q/translate x y)
    (q/rotate (u/vheading (:vel driver)))
    (q/triangle (- r) (/ (- r) 2)
                (- r) (/ r 2)
                r 0)
    (q/pop-matrix))

  ;; draw target
  (let [[x y] (:pos (:target @pursue-state))
        r (:r (:target @pursue-state))]
    (q/ellipse x y r r))

  ;; update target
  (if (< (u/vmag (map - (:pos (:driver @pursue-state)) (:pos (:target @pursue-state))))
         (/ (:r (:target @pursue-state)) 2))
    ; spawn new location & vel if caught
    (swap! pursue-state assoc :target (assoc (:target @pursue-state)
                                             :pos [(rand-int width) (rand-int height)]
                                             :vel [(- 3 (rand-int 6)) (- 3 (rand-int 6))]))
    ; else just update
    (swap! pursue-state update :target update-target))


  (if (q/mouse-pressed?)
    (swap! pursue-state update :driver #(evade % (:target @pursue-state)))
    (swap! pursue-state update :driver #(pursue % (:target @pursue-state)))))


(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "pursue")
    (q/defsketch fv
      :host "pursue"
      :title "pursue"
      :setup pursue-setup
      :draw pursue-draw
      :size [width height])))
