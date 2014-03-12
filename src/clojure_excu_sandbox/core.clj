(ns clojure-excu-sandbox.core
  (:require [quil.core :refer :all]
            [clojure-excu-sandbox.sketch :as sketch]))

(defn setup []
  (background 255)
  {:cursor {:position [0 0]
            :size 10}})

(defn draw [state]
  (background 128)
  (let [[x y] (get-in state [:cursor :position])
        d (get-in state [:cursor :size])]
    (ellipse x y d d)))

(defn handle-event [state event]
  (case (:type event)
    :key-pressed (case (:key event)
                   :up (update-in state [:cursor :size] #(* % 1.5))
                   :down (update-in state [:cursor :size] #(/ % 1.5))
                   state)
    :mouse-moved (assoc-in state [:cursor :position] (:position event))
    state))

(defn make-sketch! []
  (sketch/make-sketch! {:title "Clojure-excu"
                        :size [800 600]
                        :setup #'setup
                        :draw #'draw
                        :handle-event #'handle-event}))
