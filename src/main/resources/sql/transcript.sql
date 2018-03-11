DROP TABLE IF EXISTS transcript;

CREATE TABLE transcript (
  talk_id         INT(11)      NOT NULL,
  language_code   VARCHAR(20)  NOT NULL,
  sid             INT(11)      NOT NULL,
  paragraph       INT(11),
  subtitle_time   INT(11),
  subtitle_text   VARCHAR(300),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, language_code, sid)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO transcript (talk_id, language_code, sid, paragraph, subtitle_time, subtitle_text)
VALUES (:talkId, :languageCode, :sid, :paragraph, :subtitleTime, :subtitleText)
ON DUPLICATE KEY UPDATE
  paragraph     = values(paragraph),
  subtitle_time = values(subtitle_time),
  subtitle_text = values(subtitle_text);

DELETE FROM transcript
WHERE talk_id = :talkId AND language_code = :languageCode
      AND sid > :sid;

SELECT
  talk_id,
  language_code,
  count(*)
FROM transcript
WHERE talk_id = 2854
GROUP BY language_code
ORDER BY talk_id, sid, language_code

SELECT *
FROM transcript
WHERE talk_id = 2854 and language_code in('en','zh-tw','ja') ORDER BY sid,language_code

SELECT max(length(subtitle_text)) from transcript