CREATE TABLE guestbook
    (
        id VARCHAR(20) PRIMARY KEY AUTO_INCREMENT,
        name VARCHAR(30),
        message varchar(200),
        timestamp TIMESTAMP
        );

CREATE TABLE wxuser
    (
        id VARCHAR(20) PRIMARY KEY AUTO_INCREMENT,
        wxopenid VARCHAR(200),
        gzuserid varchar(200),
        token varchar(30),
        expired TIMESTAMP,
        username varchar(50),
        userpwd(50),
        );

CREATE TABLE globalconfig
    (
        id VARCHAR(20) PRIMARY KEY AUTO_INCREMENT,
        configname VARCHAR(30),
        data VARCHAR(1024),
        createtime TIMESTAMP,
        );
