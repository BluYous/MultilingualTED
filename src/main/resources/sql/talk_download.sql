DROP TABLE IF EXISTS talk_download;

CREATE TABLE talk_download (
  talk_id         INT(11)      NOT NULL,
  download_type   VARCHAR(20),
  uri             VARCHAR(255),
  filesize_bytes  INT(11),
  mime_type       VARCHAR(30),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, download_type)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

