DROP TABLE IF EXISTS talk;

CREATE TABLE talk (
  talk_id                    INT(11)      NOT NULL,
  talk_url                   VARCHAR(255),
  talk_slug                  VARCHAR(255),
  talk_default_language_code VARCHAR(20),
  viewed_count               INT(11),
  filmed_datetime            DATETIME,
  published_datetime         DATETIME,
  duration                   INT(11),
  intro_duration             DECIMAL(10, 4),
  ad_duration                DECIMAL(10, 4),
  post_ad_duration           DECIMAL(10, 4),
  native_language            VARCHAR(30),
  event_blurb                VARCHAR(2500),
  event_label                VARCHAR(40),
  thumb_img_url              VARCHAR(255),
  thumb_img_slug             VARCHAR(60),
  thumb_img_is_downloaded    CHAR(1)               DEFAULT 'N',
  last_update_datetime       DATETIME,
  
  entry_datetime             TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime            TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

# 仅插入基本信息
INSERT INTO talk (talk_id, talk_url, talk_slug, talk_default_language_code)
VALUES (:talkId, :talkUrl, :talkSlug, :talkDefaultLanguageCode)
ON DUPLICATE KEY UPDATE talk_url = values(talk_url), talk_slug = values(talk_slug),
  talk_default_language_code     = values(talk_default_language_code);

# 插入额外信息
INSERT INTO talk (talk_id, viewed_count, filmed_datetime, published_datetime,
                  duration, intro_duration, ad_duration, post_ad_duration,
                  native_language, event_blurb, event_label, thumb_img_url,
                  thumb_img_slug, last_update_datetime)
VALUES
  (:talkId, :viewedCount, :filmedDatetime, :publishedDatetime, :duration, :introDuration, :adDuration, :postAdDuration,
            :nativeLanguage, :eventBlurb, :eventLabel, :thumbImgUrl, :thumbImgSlug, :lastUpdateDatetime)
ON DUPLICATE KEY UPDATE
  viewed_count         = values(viewed_count),
  filmed_datetime      = values(filmed_datetime),
  published_datetime   = values(published_datetime),
  duration             = values(duration),
  intro_duration       = values(intro_duration),
  ad_duration          = values(ad_duration),
  post_ad_duration     = values(post_ad_duration),
  native_language      = values(native_language),
  event_blurb          = values(event_blurb),
  event_label          = values(event_label),
  thumb_img_url        = values(thumb_img_url),
  thumb_img_slug       = values(thumb_img_slug),
  last_update_datetime = values(last_update_datetime);

SELECT
  talk_id,
  talk_url,
  talk_default_language_code
FROM talk
WHERE last_update_datetime IS NULL
      OR TIMESTAMPDIFF(DAY, last_update_datetime, sysdate()) >= 1;


