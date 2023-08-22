(ns lvector
  (:require [cljs.math :as math]))


; TODO: move inline tests to test suite?

(defn vadd [v1 v2] (mapv + v1 v2))
(= (vadd [1 2] [3 4]) [4 6])

(defn vsub [v1 v2] (mapv - v1 v2))
(= (vsub [3 4] [1 2]) [2 2])

(defn vmult [v s] (mapv #(* % s) v))
(= (vmult [1 2] 2) [2 4])

(defn vdiv [v s] (mapv #(/ % s) v))
(= (vdiv [1 2] 2) [0.5 1])

(defn from-angle [radians]
  "returns unit vector"
  [(math/cos radians) (math/sin radians)])
(and
  (= (from-angle (math/to-radians 0)) [1 0])
  (= (map math/round (from-angle (math/to-radians 90))) [0 1])
  (= (map math/round (from-angle (math/to-radians 180))) [-1 0])
  (= (map math/round (from-angle (math/to-radians 270))) [0 -1])
  (= (map math/round (from-angle (math/to-radians 360))) [1 0]))

(defn vmag [v]
  (math/sqrt (reduce + (map #(* % %) v))))
(= (vmag [3 4]) 5)

(defn scale-vector [v scale-factor]
  (map (partial * scale-factor) v))
(= (vmag (scale-vector [3 4] 2)) 10)


(defn set-magnitude [v new-magnitude]
  (let [current-magnitude (vmag v)
        scale-factor (/ new-magnitude current-magnitude)]
    (scale-vector v scale-factor)))
(= (vmag (set-magnitude [3 4] 1)) 1)

(defn dist [v1 v2]
  (abs (vmag (vsub v1 v2))))

(defn vheading [vec]
  (math/atan2 (second vec) (first vec)))
(= (vheading [1 1]) (math/to-radians 45))

(defn vlimit [v limit-mag]
  (if (< (vmag v) limit-mag)
    v
    (set-magnitude v limit-mag)))

(defn vdot [v1 v2]
  (reduce + (map * v1 v2)))
(= 20 (vdot [2 2] [10 0]))

(defn vnorm [v]
  (let [mag (vmag v)]
    (if (zero? mag)
      v
      (mapv #(/ % mag) v))))
(= 1 (Math/round (vmag (vnorm [5 5]))))

(defn vector-projection [target start end]
  (let [v1 (vsub target start)
        v2 (vsub end start)
        sp (vdot v1 (vnorm v2))]
    (-> v2
        vnorm
        (vmult sp)
        (vadd start))))
(= [3 0]
   (vector-projection [3 2] [2 0] [4 0])
   )

(defn get-normal-point [target start end]
  (let [ap (vsub target start)
        ab (vnorm (vsub end start))]
    (vadd start (vmult ab (vdot ap ab)))))

(defn point-on-line-segment? [point p1 p2]
  (let [v1 (vsub p2 p1)
        v2 (vsub point p1)
        cross-product (- (* (v1 0) (v2 1)) (* (v1 1) (v2 0)))
        dot-product1 (+ (* (v1 0) (v2 0)) (* (v1 1) (v2 1)))
        dot-product2 (+ (* (v1 0) (v1 0)) (* (v1 1) (v1 1)))]
    (and (zero? cross-product)
         (<= 0 dot-product1 dot-product2))))
(do
  (= true (point-on-line-segment? [3 1] [0 1] [4 1]))
  (= false (point-on-line-segment? [3 1] [0 1] [2 1])))
