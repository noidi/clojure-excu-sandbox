(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [quil.core :refer :all]
            [robert.hooke :refer [remove-hook add-hook]]
            [clojure-excu-sandbox.core
             :refer [make-sketch! setup draw handle-event]]))

(defonce app (atom {:sketch nil
                    :state :stopped}))

(defmacro defcommand [name & body]
  `(defn ~name
     ([]
       (swap! app ~name))
     (~@body)))

(defcommand start! [app]
  (-> app
    (update-in [:sketch] #(if % % (make-sketch!)))
    (assoc :state :running)))

(defcommand stop! [app]
  (-> app
    (update-in [:sketch] #(when % (sketch-close %)))
    (assoc :state :stopped)))

(defcommand resume! [app]
  (if (= (:state app) :paused)
    (do
      (sketch-start (:sketch app))
      (assoc app :state :running))
    app))

(defcommand pause! [app]
  (condp = (:state app)
    :running (do
               (sketch-stop (:sketch app))
               (assoc app :state :paused))
    :paused (resume! app)
    app))

(defn keep-hooked [target-var key f]
  (add-hook target-var key f)
  (add-watch target-var key
             (fn [k r o n]
               (add-hook target-var key f))))

(defn pause-on-exception-hook [f & args]
  (try
    (apply f args)
    (catch Throwable t
      (pause!)
      (.printStackTrace t)
      ;; Assume the first arg is the app state, and return it unmodified
      (first args))))

(defn only-when-running-hook [f & args]
  (when (= (:state @app) :running)
    (apply f args)))

(keep-hooked #'setup ::pause-on-exception-hook #'pause-on-exception-hook)
(keep-hooked #'draw ::pause-on-exception-hook #'pause-on-exception-hook)
(keep-hooked #'handle-event ::pause-on-exception-hook #'pause-on-exception-hook)
(keep-hooked #'draw ::only-when-running-hook #'only-when-running-hook)
(keep-hooked #'handle-event ::only-when-running-hook #'only-when-running-hook)

(defn restart! []
  (stop!)
  (refresh :after `start!))
