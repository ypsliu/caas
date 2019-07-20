 
 CREATE TABLE `caas_user_upload` (
  `code` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '编码',
  `appCode` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '应用编码',
  `inserted` mediumblob COMMENT '插入数据',
  `updated` mediumblob COMMENT '更新数据',
  `invalid` mediumblob COMMENT '无效数据',
  `existed` mediumblob COMMENT '已有数据',
  `failed` mediumblob COMMENT '失败数据',
  `status` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '状态',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `creationTime` datetime NOT NULL COMMENT '创建日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

ALTER TABLE caas_user ADD `originCode` varchar(150) AFTER `email`;

ALTER TABLE caas_subject ADD `roleTree` MEDIUMBLOB AFTER `appCode`;

ALTER TABLE caas_subject modify column `roleTree`  mediumblob;

ALTER TABLE caas_role_resource drop primary key;
ALTER TABLE caas_role_resource add primary key(`roleCode`,`resourceCode`,`operationCode`);
