DROP TABLE IF EXISTS topic;

CREATE TABLE topic (
  topic_id        VARCHAR(30)  NOT NULL,
  label           VARCHAR(30),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (topic_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO topic (topic_id, label) VALUES (:topicId, :label)
ON DUPLICATE KEY UPDATE label = values(label);

SELECT
  max(length(topic_id)),
  min(length(topic_id)),
  max(length(label)),
  min(length(label))
FROM topic;

