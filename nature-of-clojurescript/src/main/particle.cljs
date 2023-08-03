(ns particle)

(defn check-x-edge [p width]
  (cond (<= (.-x (:pos p)) 0)
        (-> p
            (assoc :pos (0 (.-y (:pos p))))
            (assoc :vel (map * (:vel p) [-1 1]))
            )
        (>= (.-x (:pos p)) width)
        (-> p
            (assoc :pos [width (.-y (:pos p))])
            (assoc :vel (map * (:vel p) [-1 1]))
            )
        :else p))


(defn check-y-edge [p height]
  (cond (<= (.-y (:pos p)) 0)
        (-> p
            (assoc :pos [(.-x (:pos p)) 0])
            (assoc :vel (map * (:vel p) [1 -1])))
        (>= (.-y (:pos p)) height)
        (-> p
            (assoc :pos [(.-x (:pos p)) height])
            (assoc :vel (map * (:vel p) [1 -1])))
        :else p))

(defprotocol ParticleProto
  (edges [this] "edge detection, updates velocity")
  (apply-force [this force-vector] "applies a force")
  (update [this] "updates position based on velocity"))

; TODO: how spec key types
(defrecord Particle [pos vel width height lifetime r decay]
  ParticleProto
  (edges [this]
    (-> this
        (check-x-edge width)
        (check-y-edge height)))
  (apply-force [this fv]
    (assoc this :vel (map + (:vel this) fv)))
  (update [this]
    (let [new-pos (map + (:pos this) (:vel this))
          new-life (if (> (:lifetime this) 0) (- (:lifetime this) (or decay 0.5)) 0)]
      (-> this
          (assoc :pos new-pos)
          (assoc :lifetime new-life)
          )
      )))



(defn new-random-particle [pos width height lifetime r decay]
  (->Particle pos
              [(- (rand-int 10) 5)
               (- (rand-int 10) 5)]
              width
              height
              lifetime
              r
              decay))


(comment

  (update (new-random-particle [0 0] 100 100))


  (apply-force (->Particle [10 10] [0 0] 100 100)
               [-1 -1])

  (defn apply-n-times [f n data]
    (if (zero? n)
      data
      (recur f (dec n) (f data))))


  (apply-n-times update-p 11 testpart)

  )
