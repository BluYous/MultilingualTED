DROP TABLE IF EXISTS speaker;

CREATE TABLE speaker (
  speaker_id          INT(11)      NOT NULL,
  slug                VARCHAR(60),
  first_name          VARCHAR(60),
  last_name           VARCHAR(60),
  description         VARCHAR(60),
  photo_url           VARCHAR(255),
  photo_slug          VARCHAR(60),
  photo_is_downloaded CHAR(1)               DEFAULT 'N',
  what_others_say     VARCHAR(1000),
  who_they_are        VARCHAR(255),
  why_listen          VARCHAR(2000),
  title               VARCHAR(255),
  middle_initial      VARCHAR(255),
  
  entry_datetime      TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime     TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (speaker_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;


INSERT INTO speaker (speaker_id, slug, first_name, last_name, description, photo_url, photo_slug, what_others_say,
                     who_they_are, why_listen, title, middle_initial)
VALUES (:speakerId, :slug, :firstName, :lastName, :description, :photoUrl, :photoSlug, :whatOthersSay, :whoTheyAre,
                    :whyListen, :title, :middleInitial)
ON DUPLICATE KEY UPDATE
  slug            = values(slug),
  first_name      = values(first_name),
  last_name       = values(last_name),
  description     = values(description),
  photo_url       = values(photo_url),
  photo_slug      = values(photo_slug),
  what_others_say = values(what_others_say),
  who_they_are    = values(who_they_are),
  why_listen      = values(why_listen),
  title           = values(title),
  middle_initial  = values(middle_initial);