(ns particle
  (:require [p5 :refer [Vector]]
            [util :as u]))

(defn check-x-edge [p width]
  (cond (<= (.-x (:pos p)) 0)
        (-> p
            (assoc :pos (Vector. 0 (.-y (:pos p))))
            (assoc :vel (u/vmult (:vel p) (Vector. -1 1)))
            )
        (>= (.-x (:pos p)) width)
        (-> p
            (assoc :pos (Vector. width (.-y (:pos p))))
            (assoc :vel (u/vmult (:vel p) (Vector. -1 1)))
            )
        :else p))


(defn check-y-edge [p height]
  (cond (<= (.-y (:pos p)) 0)
        (-> p
            (assoc :pos (Vector. (.-x (:pos p)) 0))
            (assoc :vel (u/vmult (:vel p) (Vector. 1 -1))))
        (>= (.-y (:pos p)) height)
        (-> p
            (assoc :pos (Vector. (.-x (:pos p)) height))
            (assoc :vel (u/vmult (:vel p) (Vector. 1 -1))))
        :else p))

(defprotocol ParticleProto
  (edges [this] "edge detection, updates velocity")
  (apply-force [this force-vector] "applies a force")
  (update-pos [this] "updates position based on velocity")
  )

; TODO: how spec key types
(defrecord Particle [pos vel width height r lifetime]
  ParticleProto
  (edges [this]
    (-> this
        (check-x-edge width)
        (check-y-edge height)))
  (apply-force [this fv]
    (assoc this :vel (u/vadd (:vel this) fv))
    )
  (update-pos [this]
    (assoc this :pos (u/vadd (:pos this) (:vel this)))))

(defn new-random-particle [pos width height r lifetime]
  (->Particle pos
              (Vector. (- (rand-int 10) 5)
                       (- (rand-int 10) 5))
              width
              height
              r
              lifetime))


(comment
  (update-pos (new-random-particle 100 100))

  (update-pos testpart)

  (apply-force (->Particle (Vector. 10 10) (Vector. 0 0) 100 100)
               (Vector. -1 -1))

  (defn apply-n-times [f n data]
    (if (zero? n)
      data
      (recur f (dec n) (f data))))


  (apply-n-times update-pos 11 testpart)

  )
