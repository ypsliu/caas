--add isActive on  user table
ALTER TABLE caas_user ADD isActive tinyint(1) default '1' NOT NULL  AFTER `email`;

--add column checkVcode for app  yes|no
ALTER TABLE caas_app ADD  `emailNotify` tinyint(1)  default '0' NOT NULL  COMMENT '是否打开邮件通知功能'   AFTER `checkVcode`;



-- new table caas_user_token
DROP TABLE IF EXISTS `caas_user_token`;
CREATE TABLE `caas_user_token` (
  `userCode` bigint(20) NOT NULL  COMMENT '用户编码',
  `token` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'token',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`userCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户验证码表';
