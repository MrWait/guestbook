(ns guestbook.log
  (:require
   [guestbook.layout :as layout]
   [compojure.core :refer [defroutes GET POST]]
   [clojure.java.io :as io]
   [guestbook.db.core :as db]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect]]
   [clj-http.client :as client]
   ))

(defn print-logger
  [writer]
  #(binding [*out* writer]
     (println %)))


(def *out*-logger (print-logger *out*))

(defn file-logger
  [file]
  #(with-open [f (clojure.java.io/writer file :append true)]
     ((print-logger f) %)))

(def log->file (file-logger "messages.log"))
