(ns path
 (:require [quil.core :as q :include-macros true]))

(defrecord Path [start end radius])

(defn show-path [path]
  (let [path-start (:start path)
        path-end (:end path)
        r (:radius path)]
    (do
      (q/stroke-weight (* 2 r))
      (q/stroke 100)
      (q/line (first path-start) (last path-start)
              (first path-end) (last path-end))
      (q/stroke-weight 1)
      (q/stroke 255)
      (q/line (first path-start) (last path-start)
              (first path-end) (last path-end)))))
