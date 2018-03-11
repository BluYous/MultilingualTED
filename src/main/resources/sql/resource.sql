DROP TABLE IF EXISTS resource;

CREATE TABLE resource (
  url             VARCHAR(255) NOT NULL,
  file_path       VARCHAR(60),
  file_name       VARCHAR(60),
  content_length  INT(11),
  content_type    VARCHAR(30),
  tag             VARCHAR(40),
  last_modified   VARCHAR(40),
  
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (url)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

SELECT
  url,
  tag,
  last_modified
FROM resource
WHERE url IN (:urls);

INSERT INTO resource (url, file_path, file_name, content_length, content_type, tag, last_modified)
VALUES (:url, :filePath, :fileName, :contentLength, :contentType, :tag, :lastModified)
ON DUPLICATE KEY UPDATE
  file_path      = VALUES(file_path),
  file_name      = VALUES(file_name),
  content_length = VALUES(content_length),
  content_type   = VALUES(content_type),
  tag            = VALUES(tag),
  last_modified  = VALUES(last_modified)