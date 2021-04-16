SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_device`
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE wechat.t_device (
	id BIGINT(20) auto_increment NOT NULL,
	openId varchar(32) NOT NULL,
	deviceId varchar(32) NOT NULL,
	CONSTRAINT t_device_UN UNIQUE KEY (id),
	CONSTRAINT t_device_PK PRIMARY KEY (openId,deviceId)
)
ENGINE=InnoDB
DEFAULT CHARSET=utf8
COLLATE=utf8_general_ci;

