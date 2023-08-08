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

; stateful!
(def state (atom nil))

(defrecord Vehicle [pos vel max-force max-speed orientation])


(defn steer-vehicle-to-target [v target-posv]
  (let [desiredv (u/set-magnitude (map - target-posv (:pos v))
                                  (:max-speed v))
        ; steering vector is distance minus current vel
        steerv (map - desiredv (:vel v))
        ; apply force
        new-vel (map + (:vel v) steerv)
        new-pos (map + (:pos v) new-vel)
        ]
    (assoc v :vel new-vel :pos new-pos)))

(comment
  (steer-vehicle-to-target (->Vehicle [100 100] [0 0] 10 10) [1 1])
  )

(defn setup []
  (reset! state {:driver (->Vehicle [10 10] [0 0] 10 1)
                 :target [200 200]}))

(defn draw []
  (q/background 51)
  (q/no-stroke)
  ;; (q/frame-rate 1)

  ;; draw vehicle
  (let [[x y] (get-in @state [:driver :pos])]
    (q/rect x y 10 10))

  ;; draw target
  (let [[x y] (:target @state)]
    (q/ellipse x y 20 20))

  (swap! state assoc :target [(q/mouse-x) (q/mouse-y)])
  (swap! state update :driver #(steer-vehicle-to-target % (:target @state)))
  (println (:pos (:driver @state))))


(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "autonomous")
    (q/defsketch fv
      :host "autonomous"
      :title "autonomous"
      :setup setup
      :draw draw
      :size [width height])))
