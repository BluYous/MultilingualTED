DROP TABLE IF EXISTS talk_speaker_ref;

CREATE TABLE talk_speaker_ref (
  talk_id         INT(11)      NOT NULL,
  speaker_id      INT(11)      NOT NULL,
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, speaker_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO talk_speaker_ref (talk_id, speaker_id) VALUES (:talkId, :speakerId)
ON DUPLICATE KEY UPDATE talk_id = talk_id, speaker_id = speaker_id

SELECT
  talk.talk_id,
  speaker.speaker_id,
  speaker.first_name,
  speaker.last_name,
  speaker.description,
  speaker.slug
FROM talk
  INNER JOIN talk_speaker_ref ON talk.talk_id = talk_speaker_ref.talk_id
  INNER JOIN speaker ON talk_speaker_ref.speaker_id = speaker.speaker_id
