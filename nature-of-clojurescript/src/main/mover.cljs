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


(defn create-mvr [loc vel acc mass]
  {:loc loc
   :vel vel
   :mass mass
   :acc acc})

(defn update-mvr [m]
  (let [v (u/vadd (:vel m) (:acc m))
        l (u/vadd (:loc m) v)
        a (Vector. 0 0)]
    (-> m
        (assoc :vel v)
        (assoc :loc l)
        (assoc :acc a))))

(defn mvr-apply-force [m f]
  (let [acc (u/vadd (:acc m) (u/vdiv f (:mass m)))]
    (assoc m :acc acc)))

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
