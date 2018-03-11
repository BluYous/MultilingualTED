DROP TABLE IF EXISTS talk_download;

CREATE TABLE talk_download (
  talk_id         INT(11)      NOT NULL,
  download_type   VARCHAR(20),
  uri             VARCHAR(255),
  file_size_bytes INT(11),
  mime_type       VARCHAR(30),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (talk_id, download_type)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO talk_download (talk_id, download_type, uri, file_size_bytes, mime_type)
VALUES (:talkId, :downloadType, :uri, :fileSizeBytes, :mimeType)
ON DUPLICATE KEY UPDATE
  talk_id         = values(talk_id),
  download_type   = values(download_type),
  uri             = values(uri),
  file_size_bytes = values(file_size_bytes),
  mime_type       = values(mime_type)

SELECT *
FROM talk_download
ORDER BY talk_id, file_size_bytes DESC

SELECT *
FROM talk_download
# WHERE talk_id = 5351
ORDER BY file_size_bytes DESC