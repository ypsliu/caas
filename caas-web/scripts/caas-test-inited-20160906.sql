/*
Navicat MySQL Data Transfer

Source Server         : dl-crd-dev-1 172.20.4.41
Source Server Version : 50547
Source Host           : 172.20.4.41:3306
Source Database       : caas

Target Server Type    : MYSQL
Target Server Version : 50547
File Encoding         : 65001

Date: 2016-09-06 16:48:13
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for caas_admin_user
-- ----------------------------
DROP TABLE IF EXISTS `caas_admin_user`;
CREATE TABLE `caas_admin_user` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员编码',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员名称',
  `password` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT '密码',
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '邮箱',
  `superUser` tinyint(1) NOT NULL COMMENT '是否是超级管理员',
  `appCode` bigint(20) DEFAULT NULL COMMENT '应用编码',
  `enabled` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否有效',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`),
  UNIQUE KEY `index_name` (`name`),
  UNIQUE KEY `index_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='管理员表';

-- ----------------------------
-- Records of caas_admin_user
-- ----------------------------

-- ----------------------------
-- Table structure for caas_app
-- ----------------------------
DROP TABLE IF EXISTS `caas_app`;
CREATE TABLE `caas_app` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'app_code',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '应用名称',
  `secret` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT 'secret',
  `checkSign` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否校验token',
  `tokenTimeoutSec` bigint(20) NOT NULL COMMENT '授权超时秒数',
  `checkResource` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否校验资源权限',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creationTime` datetime DEFAULT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`),
  UNIQUE KEY `index_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用程序表';

-- ----------------------------
-- Records of caas_app
-- ----------------------------
INSERT INTO `caas_app` VALUES ('1', 'test-app-name-1', 'test-app-secret-1', '1', '3600', '0', '0', '2016-09-06 16:45:00', null, null, 'test', null, '1');

-- ----------------------------
-- Table structure for caas_app_setting
-- ----------------------------
DROP TABLE IF EXISTS `caas_app_setting`;
CREATE TABLE `caas_app_setting` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置项编码',
  `appCode` bigint(20) NOT NULL COMMENT '应用程序编码',
  `key` varchar(30) COLLATE utf8_unicode_ci NOT NULL COMMENT '配置项名称',
  `value` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '配置项内容',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用程序配置表';

-- ----------------------------
-- Records of caas_app_setting
-- ----------------------------

-- ----------------------------
-- Table structure for caas_resource
-- ----------------------------
DROP TABLE IF EXISTS `caas_resource`;
CREATE TABLE `caas_resource` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '资源编码',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '资源名称',
  `parentCode` bigint(20) DEFAULT '0' COMMENT 'parentCode',
  `identifier` varchar(300) COLLATE utf8_unicode_ci NOT NULL COMMENT '资源标识,url 或者其他',
  `appCode` bigint(20) NOT NULL COMMENT '应用程序编码',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`code`),
  KEY `appCode_name` (`appCode`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='资源表';

-- ----------------------------
-- Records of caas_resource
-- ----------------------------
INSERT INTO `caas_resource` VALUES ('1', 'test-resource-1', '0', 'test-resource-1', '1', '2016-09-06 16:45:00', null, null, 'test', null, '1', '0');

-- ----------------------------
-- Table structure for caas_role
-- ----------------------------
DROP TABLE IF EXISTS `caas_role`;
CREATE TABLE `caas_role` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色编码',
  `name` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT '角色名称',
  `autoAuth` tinyint(1) NOT NULL COMMENT '自动审核',
  `appCode` bigint(20) NOT NULL COMMENT '应用程序编码',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) DEFAULT '1' COMMENT '版本号',
  `removed` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`code`),
  KEY `appcode_name` (`appCode`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='角色表';

-- ----------------------------
-- Records of caas_role
-- ----------------------------
INSERT INTO `caas_role` VALUES ('1', 'test-role-1', '1', '1', '2016-09-06 16:45:00', null, null, 'test', null, '1', '0');

-- ----------------------------
-- Table structure for caas_role_resource
-- ----------------------------
DROP TABLE IF EXISTS `caas_role_resource`;
CREATE TABLE `caas_role_resource` (
  `roleCode` bigint(20) NOT NULL COMMENT '角色编码',
  `resourceCode` bigint(20) NOT NULL COMMENT '资源编码',
  PRIMARY KEY (`resourceCode`,`roleCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='角色资源表';

-- ----------------------------
-- Records of caas_role_resource
-- ----------------------------
INSERT INTO `caas_role_resource` VALUES ('1', '1');

-- ----------------------------
-- Table structure for caas_user
-- ----------------------------
DROP TABLE IF EXISTS `caas_user`;
CREATE TABLE `caas_user` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户编码',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `userType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户类型',
  `status` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户状态',
  `mobile` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `password` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT '登录密码',
  `email` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`code`),
  UNIQUE KEY `index_name` (`name`),
  KEY `user_email` (`email`),
  KEY `user_mobile` (`mobile`),
  KEY `user_role_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户表';

-- ----------------------------
-- Records of caas_user
-- ----------------------------

-- ----------------------------
-- Table structure for caas_user_role
-- ----------------------------
DROP TABLE IF EXISTS `caas_user_role`;
CREATE TABLE `caas_user_role` (
  `userCode` bigint(20) NOT NULL COMMENT '用户编码',
  `roleCode` bigint(20) NOT NULL COMMENT '角色编码',
  `status` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '状态',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`userCode`,`roleCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户角色表';

-- ----------------------------
-- Records of caas_user_role
-- ----------------------------
