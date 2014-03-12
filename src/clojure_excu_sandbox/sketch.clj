(ns clojure-excu-sandbox.sketch
  "Utilities for working with Quil sketches."
  (:require [quil.core :refer :all])
  (:import java.awt.event.KeyEvent))

(def key-codes {KeyEvent/VK_UP :up
                KeyEvent/VK_DOWN :down
                KeyEvent/VK_LEFT :left
                KeyEvent/VK_RIGHT :right
                KeyEvent/VK_ALT :alt
                KeyEvent/VK_CONTROL :control
                KeyEvent/VK_SHIFT :shift
                KeyEvent/VK_WINDOWS :command
                KeyEvent/VK_META :command
                KeyEvent/VK_F1 :f1
                KeyEvent/VK_F2 :f2
                KeyEvent/VK_F3 :f3
                KeyEvent/VK_F4 :f4
                KeyEvent/VK_F5 :f5
                KeyEvent/VK_F6 :f6
                KeyEvent/VK_F7 :f7
                KeyEvent/VK_F8 :f8
                KeyEvent/VK_F9 :f9
                KeyEvent/VK_F10 :f10
                KeyEvent/VK_F11 :f11
                KeyEvent/VK_F12 :f12
                KeyEvent/VK_F13 :f13
                KeyEvent/VK_F14 :f14
                KeyEvent/VK_F15 :f15
                KeyEvent/VK_F16 :f16
                KeyEvent/VK_F17 :f17
                KeyEvent/VK_F18 :f18
                KeyEvent/VK_F19 :f19
                KeyEvent/VK_F20 :f20
                KeyEvent/VK_F21 :f21
                KeyEvent/VK_F22 :f22
                KeyEvent/VK_F23 :f23
                KeyEvent/VK_F24 :f24
                KeyEvent/VK_PAGE_UP :page-up
                KeyEvent/VK_PAGE_DOWN :page-down
                KeyEvent/VK_HOME :home
                KeyEvent/VK_END :end})

(defn last-key []
  (let [k (raw-key)]
    (if (key-coded? k)
      (let [c (key-code)]
        (key-codes c c))
      k)))

(defn mouse-position-event [type]
  {:type type
   :position [(mouse-x) (mouse-y)]})

(defn mouse-button-event [type]
  (assoc (mouse-position-event type)
         :button (mouse-button)))

(defn make-sketch! [settings]
  (let [pressed-keys (atom #{})
        keyboard-event (fn [type]
                         {:type type
                          :key (last-key)
                          :pressed-keys @pressed-keys})
        handle-event! (fn [event]
                        (swap! (state :state) (:handle-event settings) event))
        sketch-state (atom nil)
        sketch (apply sketch
                 (apply concat
                   (assoc settings
                     :mouse-moved #(handle-event! (mouse-position-event :mouse-moved))
                     :mouse-clicked #(handle-event! (mouse-button-event :mouse-clicked))
                     :mouse-pressed #(handle-event! (mouse-button-event :mouse-pressed))
                     :mouse-released #(handle-event! (mouse-button-event :mouse-released))
                     :key-pressed #(do
                                     (swap! pressed-keys conj (last-key))
                                     (handle-event! (keyboard-event :key-pressed)))
                     :key-released #(do
                                      (swap! pressed-keys disj (last-key))
                                      (handle-event! (keyboard-event :key-released)))
                     :key-typed #(handle-event! (keyboard-event :key-released))
                     :setup #(do
                               (reset! sketch-state ((:setup settings)))
                               (set-state! :state sketch-state))
                     :draw #((:draw settings) @sketch-state))))]
    [sketch sketch-state]))
