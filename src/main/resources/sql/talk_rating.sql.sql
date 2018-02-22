DROP TABLE IF EXISTS talk_rating;

CREATE TABLE talk_rating (
  talk_id         INT(11)      NOT NULL,
  rating_id       INT(11)      NOT NULL,
  rating_name     VARCHAR(30),
  rating_count    INT(11),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, rating_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;