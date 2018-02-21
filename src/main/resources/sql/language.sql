DROP TABLE IF EXISTS language;

CREATE TABLE language (
  id              VARCHAR(20) PRIMARY KEY,
  label           VARCHAR(30),
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO language (id, label) VALUES (:id, :label)
ON DUPLICATE KEY UPDATE label = values(label);

SELECT
  max(length(id)),
  min(length(id)),
  max(length(label)),
  min(length(label))
FROM language;
