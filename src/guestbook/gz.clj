(ns guestbook.gz
  (:require
   [guestbook.layout :as layout]
   [guestbook.log :as log]
   [compojure.core :refer [defroutes GET POST]]
   [clojure.java.io :as io]
   [guestbook.db.core :as db]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect]]
   [clj-http.client :as client]
   ))

(defn gz-save-user
  [params]
  (db/save-user! params)
  )

;; input : wxopenid
(defn gz-regist
  [params]
  (-> params
      (client/post "http://api.gizwits.com/app/users"
                   {:X-Gizwits-Application-Id "8c371f7f1ef745f6897eddf6f7e83ae3"
                    :phone_id (:wxopenid %1)})
      (if (:uid %1)
        (gz-save-user (assoc %1 (:wxopenid params)))
        nil)
      ))

(defn device-page
  []
  (layout/render "device.html"))


(defn gz-get-userid
  [wxopenid]
  (let [ret (db/get-gzuserid {:wxopenid "xgizwitsapplication"})]
    (if (:gzuserid ret)
      {:gzuserid (:gzuserid ret)}
      ((gz-regist {:wxopenid wxopenid})
       (gz-get-userid wxopenid)))))

(defn device-regist
  [{:keys [params]}]
  (do
    (if (gz-regist))))

;;(defn device-user-login)
