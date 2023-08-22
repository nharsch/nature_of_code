(ns ch7
  (:require [quil.core :as q]
            [util :as u]))

(def canvas-id "ch7-canvas")
(def width 800)
(def height 800)
(def state (atom nil))

(defn make-3d-array [ncols nrows]
  (->> #(->> (fn [] (rand-int 2))
             repeatedly
             (take ncols)
             vec)
       repeatedly
       (take nrows)
       vec))

(def res 100)


;; (defn sum-neighbors [[r c] cells]
;;   (reduce +
;;    (for [[nc nr] [[-1 -1] [0 -1] [1 -1]
;;                   [-1  0]        [1  0]
;;                   [-1  1] [0  1] [1  1]]]
;;      (get-in cells [(+ r nr) (+ c nc)]))))

; skipping calcs on out of bounds does improve speed
(defn sum-neighbors [[r c] cells]
  (let [neighbors [[-1 -1] [0 -1] [1 -1]
                   [-1  0]        [1  0]
                   [-1  1] [0  1] [1  1]]
        in-bounds? (fn [r c] (and (>= r 0) (< r res) (>= c 0) (< c res)))]
    (reduce (fn [acc [nr nc]]
              (if (in-bounds? (+ r nr) (+ c nc))
                (+ acc (get-in cells [(+ r nr) (+ c nc)]))
                acc))
            0
            neighbors)))
(comment
  (do
    (= 3 (sum-neighbors [1 1]
                        [[0 1 0]
                         [0 1 0]
                         [1 0 1]]))
    (= 2 (sum-neighbors [1 1]
                        [[0 0 0]
                         [0 0 0]
                         [1 0 1]]))
    (= 1 (sum-neighbors [2 2]
                        [[0 1 0]
                         [1 1 0]
                         [1 0 1]]))))

(defn get-cell-next [[r c] cells]
  (let [cell (get-in cells [r c])
        neighbors (sum-neighbors [r c] cells)]
    (if (< 0 cell)
      (cond (< neighbors 2) 0
            (and (<= neighbors 3) (<= 2 neighbors)) 1
            (< 3 neighbors) 0)
      (cond (= neighbors 3) 1
            :else 0))))

(do
  (= 0 (get-cell-next [1 1] [[0 0 0]
                             [0 1 0]
                             [0 0 1]]))
  (= 1 (get-cell-next [1 1] [[1 0 0]
                             [0 1 0]
                             [0 0 1]]))
  (= 1 (get-cell-next [1 1] [[0 1 0]
                             [0 1 0]
                             [1 0 1]]))
  (= 0 (get-cell-next [1 1] [[1 1 0]
                             [0 1 0]
                             [1 0 1]]))
  (= 1 (get-cell-next [1 1] [[1 1 0]
                             [0 0 0]
                             [0 0 1]]))
  (= 0 (get-cell-next [1 1] [[1 1 0]
                             [0 0 1]
                             [0 0 1]]))
  (= 0 (get-cell-next [1 1] [[0 0 0]
                             [0 0 0]
                             [0 0 0]]))
  (= 0 (get-cell-next [0 0] [[1 1 0]
                             [0 0 1]
                             [0 0 1]]))
  (= 1 (get-cell-next [0 1] [[1 1 0]
                             [0 1 1]
                             [0 0 1]])))

; TODO: clean up
(defn update-cells [cells]
  (vec (map-indexed (fn [ri row]
                      (vec
                       (map-indexed
                        (fn [ci col] (get-cell-next [ri ci] cells))
                        row)))
                    cells)))

(defn gol-setup []
  (reset! state {:cells (make-3d-array res res)}))

(defn gol-draw []
  (q/background 0)
  ;; (q/frame-rate 10)
  (let [cells (:cells @state)
        cwidth (/ width res)
        rheight (/ height res)]
    (doseq [r (range res) c (range res)]
      (q/fill (- 255  (* 255 (get-in cells [r c]))))
      (q/rect (* c cwidth) (* r rheight) cwidth rheight)))
  (swap! state update :cells update-cells))

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "game-of-life")
    (q/defsketch fv
      :host "game-of-life"
      :title "game-of-life"
      :setup gol-setup
      :draw gol-draw
      :size [width height])))
