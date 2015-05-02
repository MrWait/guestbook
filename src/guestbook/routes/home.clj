(ns guestbook.routes.home
  (:require [guestbook.layout :as layout]
            [guestbook.wx :as wx]
            [guestbook.gz :as gz]
            [compojure.core :refer [defroutes GET POST]]
            [clojure.java.io :as io]
            [guestbook.db.core :as db]
            [bouncer.core :as b]
            [bouncer.validators :as v]
            [ring.util.response :refer [redirect]]))

;; (defn home-page []
;;   (layout/render
;;     "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))
(defn home-page [{:keys [flash]}]
  (layout/render
   "home.html"
   (merge {:messages (db/get-messages)}
          (select-keys flash [:name :message :errors]))))

(defn about-page []
  (layout/render "about.html"))

(defn validate-wx [params]
  {:echostr params})

(defn validate-message [params]
  (first
   (b/validate
    params
    :name v/required
    :message [v/required [v/min-count 10]])))

(defn save-message! [{:keys [params]}]
  (if-let [errors (validate-message params)]
    (-> (redirect "/")
        (assoc :flash (assoc params :errors errors)))
    (do
      (db/save-message! (assoc params :timestamp (java.util.Date.)))
      (redirect "/"))))


(defroutes home-routes
  (GET "/" request (home-page request))
  (GET "/wx" request {:echostr request})
  (POST "/wx" request (wx/wx-recieve-message request))
  (POST "/" request (save-message! request))
  (GET "/about" [] (about-page))
  (GET "/device" [] (gz/device-page)))
