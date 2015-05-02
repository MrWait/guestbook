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
   [clj-json.core :as json]
   [clojure.string :as str]
   [clojure.xml :refer [parse]]
   [pandect.algo [sha1 :refer [sha1]]]
   )
  (:import (java.util UUID Properties)))

(defonce cached (atom {:token nil
                       :appid nil
                       :appsecret nil
                       :access-token nil,
                       :access-token-expire-time 0
                       :jsapi-ticket nil,
                       :jsapi-ticket-time 0}))

;; ;; 保存微信的accesstoken
;; (defn wx-save-access-token
;;   [params expired]
;;   (tools/save-config! (conj {}
;;                             {:configname "wxaccesstoken",
;;                              :data params,
;;                              :createtime (+ (java.util.Date.)
;;                                             (:expired expired))})))

;; ;; 重新获取微信accesstoken。需要用到appid和secret
;; (defn wx-refresh-token
;;   [params]
;;   (let [ret ((client/get "https://api.weixin.qq.com/cgi-bin/token"
;;                          {:grant_type "client_credential"
;;                           :appid (:appid params)
;;                           :secret (:appsecret params)}))]
;;       (if (:access_token ret)
;;         (wx-save-access-token (:access_token ret) (:expires_in ret))
;;         (:access_token ret))))

;; ;; 获取保存的微信accesstoken
;; (defn get-wx-access-token
;;   []
;;   (let [ret  (tools/get-config-byname "wxaccesstoken")]
;;     (if (or (:access_token ret) (< (java.util.Date.) (:expires_in ret)))
;;       ret
;;       (wx-refresh-token {:appid "wx35e527ea383d5275", :appsecret "65a71b6cf81e40edf650f42a4d350c77"}))
;;     ))

;; ;; 接收微信服务器的消息
(defn wx-recieve-message
  [params]
  (let [ret1 (slurp (:body params))
        ret (parse ret1 )]
    (println ret1)
    (println ret))
  (str "next"))

;; ;; 和微信服务器交互的主要接口。
(defn wx
  [request]
  (println (:params request))
  (:echostr (:params request)))
(defonce cached (atom {:token nil
                       :appid nil
                       :appsecret nil
                       :access-token nil,
                       :access-token-expire-time 0
                       :jsapi-ticket nil,
                       :jsapi-ticket-time 0}))

(defn update-config []
  "更新配置文件(位于io/resource所指定的目录下的wechat.properties文件)"
  (let [config (with-open [is (io/input-stream (io/resource "wechat.properties"))]
                 (doto (Properties.)
                   (.load is)))]
    (swap! cached assoc :token (get config "token") :appid (get config "appid") :appsecret (get config "appsecret"))))



(defn check-signature? [signature timestamp nonce]
  "返回true如果公众号签名验证正确"
  (if (= signature (sha1 (apply str (sort [(:token @cached) timestamp nonce])))) true false))

(defn access-token []
  "获取access-token"
  (let [now (System/currentTimeMillis)
        access-token (:access-token @cached)]
    (if (or (< (:access-token-expire-time @cached) now)
            (nil? access-token))
      (let [access-token (get (-> (str "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" (:appid @cached) "&secret=" (:appsecret @cached)) slurp json/parse-string) "access_token")]
        (swap! cached assoc :access-token access-token :access-token-expire-time (+ now 7000000))
        access-token)
      access-token)))

(defn jsapi-ticket []
  "获取jsapi-ticket"
  (let [now (System/currentTimeMillis)
        jsapi-ticket (:jsapi-ticket @cached)]
    (if (or (< (:jsapi-ticket-time @cached) now)
            (nil? jsapi-ticket))
      (let [jsapi-ticket (get (-> (str "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=" (access-token)) slurp json/parse-string) "ticket")]
        (swap! cached assoc :jsapi-ticket jsapi-ticket :jsapi-ticket-time (+ now 7000000))
        jsapi-ticket)
      jsapi-ticket)))

(defn jsapi-signature [jsapi-ticket noncestr timestamp url]
  "根据jsapi-ticket、noncestr、timestamp、url获取jsapi签名"
  (sha1 (str "jsapi_ticket=" jsapi-ticket "&noncestr=" noncestr "&timestamp=" timestamp "&url=" (get (str/split url #"#") 0))))

(defn sign-package [x]
  "根据url生成签名包,x可以是str或者request"
  (let [url (if (map? x) (str (name (:scheme x)) "://" (name (:server-name x)) (if (not= 80 (:server-port x)) (str ":" (:server-port x))) (:uri x) (if (not= nil (:query-string x)) (str "?" (:query-string x)))) x)
        noncestr (.toString (UUID/randomUUID))
        timestamp (Long/toString (/ (System/currentTimeMillis) 1000))
        signature (jsapi-signature (jsapi-ticket) noncestr timestamp url)]
    {:appid (:appid @cached)
     :noncestr noncestr
     :timestamp timestamp
     :url url
     :signature signature
     }))
