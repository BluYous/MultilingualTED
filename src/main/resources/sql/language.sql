DROP TABLE IF EXISTS language;

CREATE TABLE language (
  language_code   VARCHAR(20) COMMENT 'id' NOT NULL,
  language_name   VARCHAR(30) COMMENT 'label',
  endonym         VARCHAR(30) COMMENT '当地语言称呼',
  iana_code       VARCHAR(30),
  is_rtl          CHAR(1),
  
  entry_datetime  TIMESTAMP(3)             NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3)             NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  PRIMARY KEY (language_code)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
# 插入基本信息
INSERT INTO language (language_code, language_name) VALUES (:languageCode, :languageName)
ON DUPLICATE KEY UPDATE language_name = values(language_name);

# 插入全部信息
INSERT INTO language (language_code, language_name, endonym, iana_code, is_rtl)
VALUES (:languageCode, :languageName, :endonym, :ianaCode, :isRtl)
ON DUPLICATE KEY UPDATE language_name = values(language_name), endonym = values(endonym),
  iana_code                           = values(iana_code), is_rtl = values(is_rtl);

SELECT
  max(length(language_code)),
  min(length(language_code)),
  max(length(language_name)),
  min(length(language_name))
FROM language;

SELECT *
FROM language
WHERE language_code = 'zh-cn'
