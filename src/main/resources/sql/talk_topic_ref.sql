DROP TABLE IF EXISTS talk_topic_ref;

CREATE TABLE talk_topic_ref (
  talk_id         INT(11)      NOT NULL,
  topic_id        VARCHAR(30)  NOT NULL,
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, topic_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;