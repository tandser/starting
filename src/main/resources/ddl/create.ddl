DROP TABLE IF EXISTS payloads;
DROP TABLE IF EXISTS headers;

DROP SEQUENCE IF EXISTS seq_payloads;
DROP SEQUENCE IF EXISTS seq_headers;

-------------------

CREATE SEQUENCE seq_headers START 1;

CREATE TABLE headers (
  id               INTEGER PRIMARY KEY DEFAULT nextval('seq_headers'),
  jmscorrelationid VARCHAR,
  jmsdeliverymode  INTEGER,
  jmsdestination   VARCHAR,
  jmsexpiration    BIGINT,
  jmsmessageid     VARCHAR,
  jmspriority      INTEGER,
  jmsredelivered   BOOLEAN,
  jmsreplyto       VARCHAR,
  jmstimestamp     BIGINT,
  jmstype          VARCHAR
);

-------------------

CREATE SEQUENCE seq_payloads START 1;

CREATE TABLE payloads (
  id               INTEGER PRIMARY KEY DEFAULT nextval('seq_payloads'),
  headers_id       INTEGER NOT NULL REFERENCES headers ON DELETE CASCADE,
  body             VARCHAR
);