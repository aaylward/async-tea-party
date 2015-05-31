(ns async-tea-party.core
  (:gen-class)
  (:require [clojure.core.async :as async]))

(def good-tea-service {:chan (async/chan 10) :name "good service"})
(def bad-tea-service {:chan (async/chan 10) :name "bad service"})
(def result-channel (async/chan 10))

(defn random-add []
  (reduce + (conj [] (repeat 1 (rand-int 10000)))))

(defn request-tea-service [tea-provider]
  (async/go
    (random-add)
    (async/>! (tea-provider :chan)
              (str "tea compliments of " (tea-provider :name)))))

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
