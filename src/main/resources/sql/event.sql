DROP TABLE IF EXISTS event;

CREATE TABLE event (
  event_id        VARCHAR(30)  NOT NULL,
  label           VARCHAR(40),
  year            YEAR,
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (event_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO event (event_id, label, year) VALUES (:eventId, :label, :year)
ON DUPLICATE KEY UPDATE label = values(label), year = values(year);

SELECT
  max(length(event_id)),
  min(length(event_id)),
  max(length(label)),
  min(length(label))
FROM event;

SELECT * from event WHERE label='TED2017'