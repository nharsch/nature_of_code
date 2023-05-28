(ns mover
  (:require [p5 :refer [Vector]]
            [util :as u]))


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
