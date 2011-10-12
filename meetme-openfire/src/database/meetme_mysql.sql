INSERT INTO ofVersion (name, version) VALUES ('meetme', 0);

CREATE TABLE ofMeeting (
   id       BIGINT            NOT NULL,
   owner     VARCHAR(32)       NOT NULL,
   description     VARCHAR(255),
   position     VARCHAR(255),
   start_time     DATETIME,
   status    INTEGER     NOT NULL,
   PRIMARY KEY (id),
   FOREIGN KEY (owner) REFERENCES ofUser(username)
);

CREATE TABLE ofMeetingRequest (
   id       BIGINT            NOT NULL,
   meet_id       BIGINT            NOT NULL,
   requested_user     VARCHAR(32)       NOT NULL,
   status    INTEGER     NOT NULL,
   updated     DATETIME,
   PRIMARY KEY (id),
   FOREIGN KEY (meet_id) REFERENCES ofMeeting(id) ON DELETE CASCADE,
   FOREIGN KEY (requested_user) REFERENCES ofUser(username)
);