(ns physics
  (:require
            [quil.core :as q :include-macros true]
            ["matter-js" :as m]))

(defprotocol Renderable
  (render [this] "show matterjs entity in p5"))

(defrecord Boundary [x y w h body]
  Renderable
  (render [this]
    (let [pos (.-position (:body this))
          angle (.-angle (:body this))]
      (q/push-matrix)
      (q/translate (.-x pos) (.-y pos))
      (q/rotate angle)
      (q/rect-mode :center)
      (q/stroke-weight 1)
      (q/no-stroke)
      (q/fill 0)
      (q/rect 0 0 (.-w this) (.-h this))
      (q/pop-matrix))))

(defrecord BoxThing [x y w h body]
  Renderable
  (render [this]
    (let [pos (.-position (:body this))
          angle (.-angle (:body this))]
      (q/push-matrix)
      (q/translate (.-x pos) (.-y pos))
      (q/rotate angle)
      (q/rect-mode :center)
      (q/stroke-weight 1)
      (q/stroke 255)
      (q/fill 127)
      (q/rect 0 0 (.-w this) (.-h this))
      (q/pop-matrix))))

(defn boundary [x y w h]
  (->Boundary x y w h
              (.rectangle m/Bodies x y w h #js {"friction" 0.3
                                                "restitution" 0.6
                                                "isStatic" true})))

(defn box [x y w h]
  (->BoxThing x y w h
              (.rectangle m/Bodies x y w h #js {"friction" 0.3
                                                "restitution" 0.6})))

(comment
  (boundary 100 100 10 10)
  (box 100 100 10 10)

  )
