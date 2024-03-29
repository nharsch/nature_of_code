(ns mover
  (:require [p5 :refer [Vector]]
            [quil.core :as q :include-macros true]
            [util :as u]))


; TODO: take map for args?
; TODO: spec?


(defn create-mover [loc vel top-speed]
  {:loc loc
   :vel vel
   :top-speed top-speed})
(defn move-mvr-toward-point [mvr pnt mag]
  (let [dv (-> (u/vsub pnt (:loc mvr))
               .normalize
               (.mult mag))
        vel (-> dv
                (u/vadd (:vel mvr))
                (.limit (:top-speed mvr)))
        loc (u/vadd (:loc mvr) vel)]
    (-> mvr
        (assoc :loc loc)
        (assoc :vel vel))))


(defn create-mvr [loc vel mass]
  {:loc loc
   :vel vel
   :mass mass})


(defn mvr-apply-force [m f]
  (assoc m :vel (u/vadd (:vel m) (u/vdiv f (:mass m)))))

(defn mvr-edges [m width height]
  (cond (<= (.-x (:loc m)) 0)
        (-> m
            (assoc :loc (Vector. 0 (.-y (:loc m))))
            (assoc :vel (u/vmult (:vel m) (Vector. -1 1))))
        (>= (.-x (:loc m)) width)
        (-> m
            (assoc :loc (Vector. width (.-y (:loc m))))
            (assoc :vel (u/vmult (:vel m) (Vector. -1 1))))
        (<= (.-y (:loc m)) 0)
        (-> m
            (assoc :loc (Vector. (.-x (:loc m)) 0))
            (assoc :vel (u/vmult (:vel m) (Vector. 1 -1))))
        (>= (.-y (:loc m)) height)
        (-> m
            (assoc :loc (Vector. (.-x (:loc m)) height))
            (assoc :vel (u/vmult (:vel m) (Vector. 1 -1))))
        :else m))

(comment
  (mvr-edges (create-mvr (Vector. 100 200)
                         (Vector. 0 10)
                         1)
             200 200)
  (mvr-edges (create-mvr (Vector. 200 100)
                         (Vector. 1 0)
                         1)
             200 200)
  (u/vmult  (:vel  (create-mvr (Vector. 200 200)
                               (Vector. 0 20)
                               1))
            (Vector. 0 -1))
  (u/vmult (Vector. 0 1) (Vector. 0 -1))
  )

(defn calc-gravity-between-movers [m1 m2]
  ;; TO BE applied to m1
  (let [force (u/vsub (:loc m2) (:loc m1))
        dist-sqr (.magSq force)
        G 0.001
        strength (* G (:mass m1) (:mass m2))]
    (.setMag force strength)
    force))

(comment
  (calc-gravity-between-movers (create-mvr (Vector. 100 200)
                                           (Vector. 0 0)
                                           10)
                               (create-mvr (Vector. 100 100)
                                           (Vector. 0 0)
                                           1))
  )


(defn update-mvr [m]
  (assoc m :loc (u/vadd (:loc m) (:vel m))))

(comment
  (mvr-apply-force
   (create-mvr
    (Vector. 200 200)
    (Vector. 1 1)
    (Vector. 0 -0.5)
    10)
   (Vector. -1 -1)
   )
  (u/vdiv (Vector. -1 -1) 10)
  (update
   (create-mvr (Vector. 200 200) (Vector. 1 1) (Vector. 0 -0.5) 10)
   :acc
   (fn [c])(u/vadd % (u/vdiv f (:mass m))))
  )

(defn get-gforces-for-m [m mvrs]
  (let [other-vectors (filter #(not (.equals (:loc m) (:loc %))) mvrs)
        gforces (map #(calc-gravity-between-movers m %) other-vectors)]
    gforces))

(defn apply-forces-to-mvr [m forces]
  (reduce (fn [m f] (mvr-apply-force m f)) m forces))
