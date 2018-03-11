DROP TABLE IF EXISTS talk_multi_lang;

CREATE TABLE talk_multi_lang (
  talk_id         INT(11)      NOT NULL,
  language_code   VARCHAR(20),
  title           VARCHAR(255),
  speaker         VARCHAR(255),
  description     VARCHAR(5000),
  
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, language_code)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO talk_multi_lang (talk_id, language_code, title, speaker, description)
VALUES (:talkId, :languageCode, :title, :speaker, :description)
ON DUPLICATE KEY UPDATE
  title       = values(title),
  speaker     = values(speaker),
  description = values(description);

SELECT *
FROM talk_multi_lang
WHERE talk_id = 9214;

SELECT
  talk_multi_lang.talk_id,
  language.language_code,
  language_name,
  endonym,
  title,
  speaker,
  is_rtl
FROM talk_multi_lang
  INNER JOIN language ON talk_multi_lang.language_code = language.language_code
  INNER JOIN talk ON talk.talk_id = talk_multi_lang.talk_id
WHERE talk_multi_lang.talk_id = 2854
ORDER BY endonym

SELECT max(length(description)) from talk_multi_lang