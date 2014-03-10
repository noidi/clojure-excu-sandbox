(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [quil.core :refer [sketch-close]]
            [clojure-excu-sandbox.core :refer [make-sketch!]]))

(defonce sketch (atom nil))

(defn start! []
  (swap! sketch #(if % % (make-sketch!))))

(defn stop! []
  (swap! sketch #(when % (sketch-close %))))

(defn restart! []
  (stop!)
  (refresh :after `start!))
