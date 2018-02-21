DROP TABLE syn_system;

CREATE TABLE syn_system (
  key_name        VARCHAR(30) PRIMARY KEY,
  value_i         INT(11),
  value_d         DECIMAL(10, 4),
  value_v         VARCHAR(30),
  value_t         DATETIME(3),
  entry_datetime  TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  update_datetime TIMESTAMP(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

