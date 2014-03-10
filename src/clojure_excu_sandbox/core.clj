(ns clojure-excu-sandbox.core
  (:require [quil.core :refer :all]))

(defn setup []
  (smooth)
  (background 255))

(defn make-sketch! []
  (sketch
    :title "Clojure-excu"
    :setup setup))
