(ns async-tea-party.core
  (:gen-class)
  (:require [clojure.core.async :as async]))

(def good-tea-service {:chan (async/chan 10) :name "good service"})
(def bad-tea-service {:chan (async/chan 10) :name "bad service"})
(def result-channel (async/chan 10))

(defn request-tea-service [{:keys [chan name]}]
  (async/go
    (Thread/sleep (int (* (java.lang.Math/random) 1000)))
    (async/>! chan
              (str "tea compliments of " name))))

(defn request-tea []
  (request-tea-service good-tea-service)
  (request-tea-service bad-tea-service)
  (async/go (let [[tea] (async/alts!
                         [(good-tea-service :chan)
                          (bad-tea-service :chan)])]
              (async/>! result-channel tea))))

(defn -main [& args]
  (println "requesting tea...")
  (request-tea)
  (println (async/<!! result-channel)))
