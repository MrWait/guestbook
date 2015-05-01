--name: save-message!
-- creates a new user record
INSERT INTO guestbook
(name, message, timestamp)
VALUES (:name, :message, :timestamp)

-- name: get-messages
-- retrieve a used given the id.
SELECT * FROM guestbook

--name: save-config!
-- sava a config data
INSERT INTO globalconfig
    (configname, data, createtime)
    VALUES (:configname, :data, :createtime)

--name: get-config
-- retrieve a config data given by configname
SELECT * FROM globalconfig
WHERE configname=:configname

--name: save-user!
-- after create one user between wx and gz, save the map relationship
INSERT INTO wxuser
    (wxopenid, gzuserid, token, expired, username, userpwd)
    VALUES (:wxopenid, :gzuserid, :token, :expired, :username, :userpwd)

--name: get-gzuserid
-- find gzuserid by given wxopenid
SELECT * FROM wxuser
WHERE wxopenid=:wxopenid
