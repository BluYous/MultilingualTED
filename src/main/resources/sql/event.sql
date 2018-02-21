DROP TABLE IF EXISTS event;

CREATE TABLE event (
  id              VARCHAR(30) PRIMARY KEY,
  label           VARCHAR(40),
  year            YEAR,
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO event (id, label, year) VALUES (:id, :label, :year)
ON DUPLICATE KEY UPDATE label = values(label), year = values(year);

SELECT
  max(length(id)),
  min(length(id)),
  max(length(label)),
  min(length(label))
FROM event;