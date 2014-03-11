(ns clojure-excu-sandbox.core
  (:require [quil.core :refer :all]))

(defn setup []
  (background 255))

(defn draw []
  (background 255)
  (ellipse (mouse-x) (mouse-y) 10 10))

(defn make-sketch! []
  (sketch
    :title "Clojure-excu"
    :size [800 600]
    :setup #'setup
    :draw #'draw))
