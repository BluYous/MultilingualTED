DROP TABLE IF EXISTS topic;

CREATE TABLE topic (
  id              VARCHAR(30) PRIMARY KEY,
  label           VARCHAR(30),
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO topic (id, label) VALUES (:id, :label)
ON DUPLICATE KEY UPDATE label = values(label);

SELECT
  max(length(id)),
  min(length(id)),
  max(length(label)),
  min(length(label))
FROM topic;