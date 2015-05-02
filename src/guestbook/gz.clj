(ns guestbook.gz
  (:require
   [guestbook.layout :as layout]
   [guestbook.log :as log]
   [guestbook.tools :as tools]
   [compojure.core :refer [defroutes GET POST]]
   [clojure.java.io :as io]
   [guestbook.db.core :as db]
   [bouncer.core :as b]
   [bouncer.validators :as v]
   [ring.util.response :refer [redirect]]
   [clj-http.client :as client]
   ))

;; ;; 保存给用户注册到机智云的信息
;; (defn gz-save-user
;;   [params]
;;   (db/save-user! params))

;; ;; 使用用户的微信ID向机智云注册
;; ;; input : wxopenid
;; (defn gz-regist
;;   [params]
;;   (let [ret  (client/post "http://api.gizwits.com/app/users"
;;                           {:X-Gizwits-Application-Id "8c371f7f1ef745f6897eddf6f7e83ae3"
;;                            :phone_id (:wxopenid params)})]
;;     (println ret)
;;     (if (:uid ret)
;;       (gz-save-user (assoc ret (:wxopenid params))))))

;; ;; 设备绑定界面。需要pk和MAC地址
(defn device-page
  []
  (layout/render "device.html"))

;; ;; 根据用户的的微信id获取对应的机智云id以及附加信息
;; (defn gz-get-userid
;;   [wxopenid]
;;   (let [ret (db/get-gzuserid {:wxopenid "xgizwitsapplication"})]
;;     (if (:gzuserid ret)
;;       {:gzuserid (:gzuserid ret)}
;;       ((gz-regist {:wxopenid wxopenid})
;;        (gz-get-userid wxopenid)))))

;; ;; 用户绑定设备
;; (defn device-regist
;;   [{:keys [params]}]
;;   (println params
;;            ))

;; ;;(defn device-user-login)
