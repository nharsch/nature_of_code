(ns path
  (:require [quil.core :as q :include-macros true]
            [lvector :as lv]))

(defrecord Path [start end radius])

(defn dist-to-path
  "find dist to normal point if on segment, or start or end"
  [point path]
  (let [p1 (:start path)
        p2 (:end path)
        norm (lv/get-normal-point point p1 p2)]
    (if (lv/point-on-line-segment? norm p1 p2)
      ; return dist to norm if on line seg
      (lv/dist point norm)
      ; return closest end point
      (->> [p1 p2]
          (map (partial lv/dist point))
          (apply min)))))
(do
  (= 1 (dist-to-path [0 1] (->Path [-1 0] [1 0])))
  (= 1 (dist-to-path [2 0] (->Path [-1 0] [1 0]))))

(defn show-path [path is-closest]
  (let [path-start (:start path)
        path-end (:end path)
        r (:radius path)]
    (do
      (q/stroke-weight (* 2 r))
      (if is-closest
        (q/stroke 10 30 80)
        (q/stroke 100))
      (q/line (first path-start) (last path-start)
              (first path-end) (last path-end))
      (q/stroke-weight 1)
      (q/stroke 255)
      (q/line (first path-start) (last path-start)
              (first path-end) (last path-end)))))
