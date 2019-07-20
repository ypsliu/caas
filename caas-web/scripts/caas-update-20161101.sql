CREATE TABLE `caas_subject` (
  `code` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '主题名称',
  `appCode` bigint(20) NOT NULL COMMENT '应用编码',
  `roleTree` mediumblob,
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`)
) 

CREATE TABLE `caas_operation` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主题编码',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '主题名称',
  `appCode` bigint(20) NOT NULL COMMENT '应用编码',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`)
)


ALTER TABLE caas_role ADD subjectCode varchar(30) AFTER `name`;

ALTER TABLE caas_role ADD `parent` varchar(30) AFTER `name`;
ALTER TABLE caas_role ADD `order` varchar(30) AFTER `parent`;
ALTER TABLE caas_role_resource ADD `operationCode` varchar(30) AFTER `resourceCode`;

ALTER TABLE caas_user ADD `originCode` varchar(150) AFTER `email`;
ALTER TABLE caas_subject ADD `roleTree` MEDIUMBLOB AFTER `appCode`;
