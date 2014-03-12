(ns user
  (:require [clojure.tools.namespace.repl :refer [refresh]]
            [quil.core :refer :all, :exclude [state sketch-state]]
            [robert.hooke :refer [remove-hook add-hook]]
            [lonocloud.synthread :as ->]
            [clojure-excu-sandbox.core
             :refer [make-sketch! setup draw handle-event]]))

(defonce app (atom {:state :stopped}))

(defmacro defcommand [name & body]
  `(defn ~name
     ([]
       (swap! app ~name))
     (~@body)))

(defcommand start! [app]
  (-> app
    (->/when-not (:sketch app)
      (->/let [[sketch sketch-state] (make-sketch!)]
        (assoc :sketch sketch
               :sketch-state sketch-state
               :state :running)))))

(defcommand stop! [app]
  (-> app
    (->/when-let [sketch (:sketch app)]
      (->/aside _ (sketch-close sketch))
      (dissoc :sketch))))

(defcommand resume! [app]
  (-> app
    (->/when (= (:state app) :paused)
      (->/aside _ (sketch-start (:sketch app)))
      (assoc :state :running))))

(defcommand pause! [app]
  (-> app
    (->/case (:state app)
      :running (->
                 (->/aside _ (sketch-stop (:sketch app)))
                 (assoc :state :paused))
      :paused resume!)))

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

(defn sketch-state []
  (-> @app :sketch-state))
