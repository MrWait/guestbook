(ns guestbook.wx
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

(defn wx-save-access-token
  [params expired]
  (tools/save-config! (conj {}
                            {:configname "wxaccesstoken",
                             :data params,
                             :createtime (+ (java.util.Date.)
                                            (:expired expired))})))

(defn wx-refresh-token
  [params]
  (let [ret ((client/get "https://api.weixin.qq.com/cgi-bin/token"
                         {:grant_type "client_credential"
                          :appid (:appid params)
                          :secret (:appsecret params)}))]
      (if (:access_token ret)
        (wx-save-access-token (:access_token ret) (:expires_in ret))
        (:access_token ret))))

(defn get-wx-access-token
  []
  (-> (tools/get-config-byname "wxaccesstoken")
      #(if (:access_token %1)
         %1
         (wx-refresh-token {:appid "wx35e527ea383d5275", :appsecret "65a71b6cf81e40edf650f42a4d350c77"}))
      ))



(defn wx
  [request]
  (println (:params request))
  (:echostr (:params request)))
