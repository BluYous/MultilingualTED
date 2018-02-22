DROP TABLE IF EXISTS speaker;

CREATE TABLE speaker (
  speaker_id          INT(11)      NOT NULL,
  slug                VARCHAR(60),
  firstname           VARCHAR(60),
  lastname            VARCHAR(60),
  description         VARCHAR(60),
  photo_url           VARCHAR(255),
  photo_slug          VARCHAR(60),
  photo_is_downloaded CHAR(1)               DEFAULT 'N',
  what_others_say     VARCHAR(255),
  who_they_are        VARCHAR(255),
  why_listen          VARCHAR(255),
  title               VARCHAR(255),
  middle_initial      VARCHAR(255),
  
  entry_datetime      TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime     TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (speaker_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

