--1.3.0-DB-001 修改用户表 password 内容
update `caas_user` set `password` = md5(`password`);
update `caas_admin_user` set `password` = md5(`password`);
--1.3.0-DB-002 创建表caas_subject 存储角色主题
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
) ;
--1.3.0-DB-003 caas_operation 存储角色操作
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
);

--1.3.0-DB-004 创建新表 用户存储批量导入结果
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

--如需要兼容历史数据 请运行以下脚本
---start-----

---end-----

--1.3.0-DB-005修改角色表，添加主题编码字段
ALTER TABLE caas_role ADD subjectCode varchar(30) AFTER `name`;
--1.3.0-DB-006修改角色表，添加parent字段
ALTER TABLE caas_role ADD `parent` varchar(30) AFTER `name`;
--1.3.0-DB-007修改角色表，添加order字段
ALTER TABLE caas_role ADD `order` varchar(30) AFTER `parent`;
--1.3.0-DB-008修改角色资源关系表，添加operationCode字段
ALTER TABLE caas_role_resource ADD `operationCode` varchar(30) AFTER `resourceCode`;
--1.3.0-DB-009修改用户表,增加originCode字段，用户存储遗留系统内的code
ALTER TABLE caas_user ADD `originCode` varchar(150) AFTER `email`;
--1.3.0-DB-010修改caas_role_resource主键设置
ALTER TABLE caas_role_resource drop primary key;
ALTER TABLE caas_role_resource add primary key(`roleCode`,`resourceCode`,`operationCode`);



