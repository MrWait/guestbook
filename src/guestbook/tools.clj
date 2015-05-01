(ns guestbook.tools
  (:require
   [guestbook.layout :as layout]
   [guestbook.log :as log]
   [guestbook.gz :as gz]
   [guestbook.wx :as wx]
   [compojure.core :refer [defroutes GET POST]]
   [clojure.java.io :as io]
   [guestbook.db.core :as db]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect]]
   [clj-http.client :as client]
   ))

(defn save-config!
  [params]
  (do
    (db/save-config! (assoc params :createtime (java.util.Date.)))))

(defn get-config-byname
  [params]
  (do
    (db/get-config {:configname params})))
;; gz appid : 8c371f7f1ef745f6897eddf6f7e83ae3
;; gz pk : 43af4045eef345c2b3531123e8f4b93d
;; wx appid : wx35e527ea383d5275
;; wx appsecret : 65a71b6cf81e40edf650f42a4d350c77
;; wx token : weline
;; wx AESKey : 5vrZh1KaaP3xYcOTSf9IkvSJTHmMSW6u90FdU8FynsB
