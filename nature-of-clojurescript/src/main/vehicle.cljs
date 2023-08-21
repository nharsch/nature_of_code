(ns vehicle
  (:require [lvector :as lv]
            [quil.core :as q :include-macros true]
            [util :as u]))


(defrecord Vehicle [pos vel max-force max-speed r])

(defn update-pos [v]
  (assoc v :pos (lv/vadd (:pos v) (:vel v))))

(defn seek [v target-posv]
  (let [desiredv (lv/set-magnitude(lv/vsub target-posv (:pos v))
                                  (:max-speed v))
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (lv/vsub desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (lv/vadd (:vel v) steerv)]
    (assoc v :vel new-vel)))

(defn flee [v target-posv]
  (let [; desired vectir is opposite
        desiredv (lv/scale-vector (lv/vsub target-posv (:pos v)) -1)
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (lv/vsub desiredv (:vel v)) (:max-force v))
        ; apply force
        new-vel (lv/vadd (:vel v) steerv)
        ]
    (assoc v :vel new-vel)))

(defn evade [pv tv]
  "pv: pursuing vehicle
   tv: target vehicle"
  (let [pursuit-v (lv/scale-vector (lv/vsub
                                       (lv/vadd (:pos tv) (lv/scale-vector (:vel tv) 4))
                                       (:pos pv))
                                  -1)
        desiredv (lv/set-magnitude pursuit-v (:max-speed pv))
        ; steering vector is distance minus current vel
        steerv (lv/vlimit (lv/vsub desiredv (:vel pv)) (:max-force pv))
        ; apply force
        new-vel (lv/vadd (:vel pv) steerv)]
    (assoc pv :vel new-vel)))

(defn follow-path [v path]
  (let [future (lv/vadd (:pos v) (lv/vmult (:vel v) 20))
        target (lv/vector-projection future (:start path) (:end path))
        dist (lv/dist future target)]
    (if (> dist (:radius path))
      (seek v target)
      v)))

(defn show-vehicle [vehicle]
  (let [driver vehicle
        [x y] (:pos vehicle)
        r (:r vehicle)]
    (q/stroke 255)
    (q/stroke-weight 2)
    (q/fill 255)
    (q/push-matrix)
    (q/translate x y)
    (q/rotate (lv/vheading (:vel driver)))
    (q/triangle (- r) (/ (- r) 2)
                (- r) (/ r 2)
                r 0)
    (q/pop-matrix)))
