(ns intro
  (:require [util :as u]
            [p5 :refer [Vector]]))


; set xoff at beginning of 1D noise space
;; (defonce xoff (atom 0))
; set yoff for from xoff, different part of noise space
;; (defonce yoff (atom 10000))
(defonce start (atom 0))
(def canvas-name "intro-canvas")
(def height 320)
(def width 640)
(def speed 0.01)
(def drift-range 10)


(defn perlin-setup [p]
  (.createCanvas p width height)
  (.frameRate p 30))

(defn perlin-draw [p]
  (def xoff (atom @start))
  (.background p 80)
  (.beginShape p)
  (.stroke p 255)
  (.noFill p)
  (doseq [x (range width)]
    (do
     (reset! xoff (+ @xoff speed))
     (.stroke p 255)
     (.vertex p x (* (.noise p @xoff) height))
     ))
  (.endShape p)
  (u/text-on-canvas p "1d perlin noise" 255)
  (reset! start (+ @start speed)))




(defonce zoff (atom 0))

(defn get-pix-index-for-canvas [width x y]
  (* 4 (+ x (* y width))))

(defn two-d-perlin-draw [p]
  ;; retina display fix
  (.pixelDensity p 1)
  (.loadPixels p)
  (doseq [y (range height)
          x (range width)]
    (let [r (* (.noise p (* x speed) (* y speed) @zoff) 255)
          index (get-pix-index-for-canvas width x y)]
      (do
        (aset (.-pixels p) index r)
        (aset (.-pixels p) (+ index 1) r)
        (aset (.-pixels p) (+ index 2) r)
        (aset (.-pixels p) (+ index 3) 255))))
  ;; NOTE: I prefer this style, but it bogs down rendering as I swap the whole array value each time instead of setting them directly
  ;; (set! (. p -pixels)
  ;;       (js/Uint8ClampedArray. (clj->js (reduce into
  ;;                                              (for [y (range height)
  ;;                                                    x (range width)]
  ;;                                                ;; get noise value
  ;;                                                (let [r (* (.noise p (* x speed) (* y speed) @zoff) 255)]
  ;;                                                  [r r r 255]))))))
  (.updatePixels p)
  ;; move through z axis every draw loop
  (u/text-on-canvas p "2d perlin noise" 255)
  (reset! zoff (+ @zoff (* speed 0.7))))






;; random number distribution
;;
(def randomCounts (atom (vec (replicate 20 0))))
(defn rc-setup [p]
  (.createCanvas p width (/ height 2))
  (.background p 255))

(comment
  (update [0 1] 0 inc)
  (count @randomCounts))

(defn rc-draw [p]
  (u/text-on-canvas p "random number distro" 1)
  (swap! randomCounts
         (fn [rc] (update rc
                          (rand-int (count rc)) ;; index
                          #(mod (inc %) (/ height 2))))) ;; restart height after max
  (.stroke p 0)
  (.fill p 175)
  (let [w (/ width (count @randomCounts))]
    (doseq [x (range (count @randomCounts))]
      (.rect p
             (* x w) (- (/ height 2) (nth @randomCounts x))
             (- w 1) (nth @randomCounts x))))
)

(defonce walker-state (atom {}))

(defn setup-walker [p]
  (.createCanvas p width height)
  (.background p 0)
  (swap! walker-state assoc :pos
         (.createVector p (/ width 2) (/ height 2))))

(defn draw-walker [p]
  (do
    (u/text-on-canvas p "random walker - tend to right" 255)
    (.stroke p 255 100)
    (.strokeWeight p 2)
    (.point p (.-x (:pos @walker-state))
              (.-y (:pos @walker-state)))
    (swap! walker-state assoc :pos
           (.add Vector
                 (:pos @walker-state)
                 (u/weighted-random-vec 0.3 0 1)))))

; gaussian distro
(defn gaus-setup [p]
  (.createCanvas p width height)
  (.background p 255))

(defn gaus-draw [p]
  (let [x (.randomGaussian p (/ width 2) 30)
        y (.randomGaussian p (/ height 2) 30)
        ]
    (.noStroke p)
    (.fill p 0 10)
    (.ellipse p x y 16 16)
    )
  (u/text-on-canvas p "gaussian distro" 0))

;; custom distribution
(comment
  (u/weighted-random-vec 0 0 10)
  )


(defonce monte-walker-state (atom {}))

(defn monte-setup-walker [p]
  (.createCanvas p width height)
  (.background p 0)
  (swap! monte-walker-state assoc :pos
         (.createVector p (/ width 2) (/ height 2))))

(defn draw-mont-walker [p]
  (do
    (u/text-on-canvas p "random walker - montecarlo" 255)
    (.stroke p 255 100)
    (.strokeWeight p 2)
    (let [new-pos (.add Vector
                        (:pos @monte-walker-state)
                        (u/weighted-random-vec 0.3 0
                                               (Math/floor (* 10 (u/montecarlo)))))]
      (.line p (.-x (:pos @monte-walker-state))
               (.-y (:pos @monte-walker-state))
               (.-x new-pos)
               (.-y new-pos))
      (.stroke p 126)
      (swap! monte-walker-state assoc :pos new-pos))))

(defn perlin-landscape-setup [p]
  (.createCanvas p width height (.-WEBGL p))
  (u/text-on-canvas p "perlin landscape" 0)
  )

(def perlin-landscape-state (atom
                             {:cell 10
                              :step 0.05
                              :mag 10}))


(defn perlin-landscape-draw [p]
  (.stroke p 80)
  (.fill p 255)
  ;; (.rotateZ p 1)
  (.translate p -100 -80 0)
  (.rotateX p 0.75)
  (.clear p)
  (let [cell 10
        noise-size 0.05
        step (:step @perlin-landscape-state)
        mag 100]
    (doseq [y (range 20)]
      (.beginShape p (.-QUAD_STRIP p))
      (doseq [x (range 20)]
        (let [n1 (.noise p (+ step (* noise-size x)) (* noise-size y))
              n2 (.noise p (+ step (* noise-size x)) (* noise-size (+ 1 y)))]
          (.vertex p
                   (* cell x)
                   (* cell y)
                   (* mag n1))
          (.vertex p
                   (* cell x)
                   (* (+ 1 y) cell)
                   (* mag n2))))
      (.endShape p)))
  (swap! perlin-landscape-state update :step #(+ % 0.05))
)
;; (:step @perlin-landscape-state)

(do
  (u/render-sketch-to-canvas perlin-setup perlin-draw canvas-name)
  (u/render-sketch-to-canvas perlin-setup two-d-perlin-draw canvas-name)
  (u/render-sketch-to-canvas rc-setup rc-draw canvas-name)
  (u/render-sketch-to-canvas setup-walker draw-walker canvas-name)
  (u/render-sketch-to-canvas gaus-setup gaus-draw canvas-name)
  (u/render-sketch-to-canvas monte-setup-walker draw-mont-walker canvas-name)
  (u/render-sketch-to-canvas perlin-landscape-setup perlin-landscape-draw canvas-name)
  )
