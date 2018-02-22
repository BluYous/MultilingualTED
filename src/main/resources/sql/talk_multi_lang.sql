DROP TABLE IF EXISTS talk_multi_lang;

CREATE TABLE talk_multi_lang (
  talk_id         INT(11)      NOT NULL,
  language_code   VARCHAR(20),
  title           VARCHAR(60),
  speaker         VARCHAR(60),
  description     VARCHAR(255),
  
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, language_code)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

