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
  (cond (or (<= (.-x (:loc m)) 0) (>= (.-x (:loc m)) width)) (assoc m :vel (u/vmult (:vel m) (Vector. -1 1)))
        (or (<= (.-y (:loc m)) 0) (>= (.-y (:loc m)) height)) (assoc m :vel (u/vmult (:vel m) (Vector. 1 -1)))(>= (.-y (:loc m)) height) (assoc m :vel (u/vmult (:vel m) (Vector. 1 -1)))
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


(defn update-mvr [m]
  ;; (println m)
  (-> m
      (assoc :loc (u/vadd (:loc m) (:vel m)))))

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
