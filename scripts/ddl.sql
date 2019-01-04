CREATE TABLE flows (
   flow_id              INT PRIMARY KEY NOT NULL,
   start_active         TIMESTAMP    NOT NULL,
   last_active          TIMESTAMP     NOT NULL,
   service_port         INT,
   protocol             INT
);


