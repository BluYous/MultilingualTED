DROP TABLE IF EXISTS talk_multi_lang;

CREATE TABLE talk_multi_lang (
  talk_id         INT(11)      NOT NULL,
  language_code   VARCHAR(20),
  title           VARCHAR(255),
  speaker         VARCHAR(60),
  description     VARCHAR(2000),
  
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, language_code)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO talk_multi_lang (talk_id, language_code, title, speaker, description)
VALUES (:talkId, :languageCode, :title, :speaker, :description)
ON DUPLICATE KEY UPDATE
  title         = values(title),
  speaker       = values(speaker),
  description   = values(description);

SELECT * from talk_multi_lang where talk_id=9214;