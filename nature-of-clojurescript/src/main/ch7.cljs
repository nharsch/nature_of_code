(ns ch7
  (:require [quil.core :as q]
            [quil.sketch :as qs]
            [util :as u]))

(def canvas-id "ch7-canvas")
(def width 600)
(def height 300)
(def state (atom nil))
(def res 200)


(defn make-3d-array [ncols nrows]
  (->> #(->> (fn [] (rand-int 2))
             repeatedly
             (take ncols)
             vec)
       repeatedly
       (take nrows)
       vec))




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
  (let [cells (:cells @state)
        cwidth (/ width res)
        rheight (/ height res)]
    (doseq [r (range res) c (range res)]
      (q/fill (- 255  (* 255 (get-in cells [r c]))))
      (q/rect (* c cwidth) (* r rheight) cwidth rheight)))
  (swap! state update :cells update-cells))

;; (if (.getElementById js/document canvas-id)
;;   (do
;;     (u/create-div canvas-id "game-of-life")
;;     (qs/defsketch fv
;;       :host "game-of-life"
;;       :title "game-of-life"
;;       :setup gol-setup
;;       :draw gol-draw
;;       :size [width height])))

;; ----


(def ca-state (atom nil))

;; (defn ca-update-cell [])

;; each combination of possible neighborhood states can be thought of as a binary number
;; IE: [2r000 2r001 2r010 2r011 2r100 2r101 2r111] or 0 - 7
;; so rules can be defined simply as vectors
(def rules
  {
   30 [0 1 1 1 1 0 0 0]
   110 [0 1 1 0 1 1 1 0]
   90 [0 1 0 1 1 0 1 0]
   })
(rules 30)

(defn seed-cells [len]
  (-> (repeat len 0) vec (assoc (/ len 2) 1))
  )
(= (seed-cells 3) [0 1 0])


(defn binary-args-to-int [args]
  (js/parseInt (apply str args) 2))
(= 2 (binary-args-to-int [0 1 0]))

; TODO: this could be faster with bitwise operation
(defn neighborhood->next-cell [neighbors rule]
  (-> neighbors
      binary-args-to-int
      rule))


(do
  (=  (neighborhood->next-cell [0 1 0] (rules 30)) 1)
  (=  (neighborhood->next-cell '(0 1 0) (rules 30)) 1)
  (=  (neighborhood->next-cell [0 0 1] (rules 30)) 1)
  (=  (neighborhood->next-cell [1 1 1] (rules 30)) 0))


; TODO: this could be faster with bitwise operation
(defn next-gen [cells rule]
  (vec (for [coords (partition 3 1 (range (+ 2 (count cells))))]
         (-> (concat [0] cells [0])     ; pad cells with 0 for edges
             vec
             (mapv (vec coords))        ; get neighborhood values
             (neighborhood->next-cell rule)))))
(=  (next-gen [0 0 1 0 0] (rules 30)) [0 1 1 1 0])

(defn ca-setup []
  (reset! ca-state {:selected-rule 30
                    :generation 0
                    :cells (conj (vec (replicate (- res 1) (vec (replicate res 0)))) (seed-cells res))}))


(defn ca-draw []
  (q/background 255)
  ;; (q/frame-rate 20)
  (let [csize (/ width (count (first (:cells @ca-state))))
        visible-rows (/ height csize)
        cells (:cells @ca-state)]
    (doseq [[ri row] (map-indexed vector cells)]
      (doseq [c (range (count row))]
        (q/no-stroke)
        (q/fill (- 255  (* 255 (row c))))
        (q/rect (* c csize) (* ri csize) csize csize)))
    (swap! ca-state update :generation inc)
    (swap! ca-state update :cells #(subvec (conj % (next-gen
                                              (last cells)
                                              (rules (:selected-rule @ca-state))))
                                           (if (< visible-rows (count cells))
                                             (- (count cells) visible-rows)
                                             0)))))

(defn on-click [e]
  (swap! ca-state assoc :selected-rule (rand-nth (keys rules))))

(defn change-rule [n]
  (swap! ca-state assoc :selected-rule n))

(if (.getElementById js/document canvas-id)
  (do
    (u/create-div canvas-id "simple-ca")
    ;; TODO: clean up
    (let [b1 (.createElement js/document "button")
          b2 (.createElement js/document "button")
          b3 (.createElement js/document "button")
          m (.getElementById js/document canvas-id)]
      (set! (.-innerHTML b1) "rule 30")
      (set! (.-onclick b1) #(change-rule 30))
      (set! (.-innerHTML b2) "rule 90")
      (set! (.-onclick b2) #(change-rule 90))
      (set! (.-innerHTML b3) "rule 110")
      (set! (.-onclick b3) #(change-rule 110))
      (.appendChild m b1)
      (.appendChild m b2)
      (.appendChild m b3))
    (qs/defsketch fv
      :host "simple-ca"
      :title "simple-ca"
      :setup ca-setup
      :draw ca-draw
      :size [width height])))
