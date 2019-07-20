-- MySQL dump 10.13  Distrib 5.6.24, for Win64 (x86_64)
--
-- Host: localhost    Database: caas
-- ------------------------------------------------------
-- Server version	5.6.24-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `caas_admin_user`
--

DROP TABLE IF EXISTS `caas_admin_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_admin_user` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员编码',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员名称',
  `password` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '邮箱',
  `superUser` tinyint(1) NOT NULL COMMENT '是否是超级管理员',
  `appCode` bigint(20) DEFAULT NULL COMMENT '应用编码',
  `userType` varchar(100) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'SIGNUP',
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='管理员表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_app`
--

DROP TABLE IF EXISTS `caas_app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_app` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'app_code',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '应用名称',
  `secret` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT 'secret',
  `checkSign` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否校验token',
  `tokenTimeoutSec` bigint(20) NOT NULL COMMENT '授权超时秒数',
  `checkResource` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否校验资源权限',
  `checkVcode` varchar(10) COLLATE utf8_unicode_ci NOT NULL DEFAULT 'NO_SET' COMMENT '是否校验验证码',
  `emailNotify` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否打开邮件通知功能',
  `backUrl` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `key` varchar(150) COLLATE utf8_unicode_ci NOT NULL,
  `status` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `appType` varchar(50) COLLATE utf8_unicode_ci DEFAULT NULL,
  `removed` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除',
  `creationTime` datetime DEFAULT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`code`),
  UNIQUE KEY `index_name` (`name`),
  UNIQUE KEY `index_key` (`key`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用程序表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_app_admin`
--

DROP TABLE IF EXISTS `caas_app_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_app_admin` (
  `appCode` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '應用编码',
  `adminCode` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员名称',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`appCode`,`adminCode`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用管理員关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_app_setting`
--

DROP TABLE IF EXISTS `caas_app_setting`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_app_user`
--

DROP TABLE IF EXISTS `caas_app_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_app_user` (
  `appCode` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员编码',
  `userCode` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员名称',
  `checkVerificationCode` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否校验验证码功能',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`appCode`,`userCode`)
) ENGINE=InnoDB AUTO_INCREMENT=93 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用用户关系表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_operation`
--

DROP TABLE IF EXISTS `caas_operation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_operation` (
  `code` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_resource`
--

DROP TABLE IF EXISTS `caas_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_role`
--

DROP TABLE IF EXISTS `caas_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_role` (
  `code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色编码',
  `name` varchar(150) COLLATE utf8_unicode_ci NOT NULL COMMENT '角色名称',
  `parent` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `order` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `subjectCode` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL,
  `appCode` bigint(20) NOT NULL COMMENT '应用程序编码',
  `roleType` varchar(50) COLLATE utf8_unicode_ci NOT NULL,
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) DEFAULT '1' COMMENT '版本号',
  `removed` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`code`),
  KEY `appcode_name` (`appCode`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_role_resource`
--

DROP TABLE IF EXISTS `caas_role_resource`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_role_resource` (
  `roleCode` bigint(20) NOT NULL COMMENT '角色编码',
  `resourceCode` bigint(20) NOT NULL COMMENT '资源编码',
  `operationCode` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  PRIMARY KEY (`roleCode`,`resourceCode`,`operationCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='角色资源表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_subject`
--

DROP TABLE IF EXISTS `caas_subject`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_user`
--

DROP TABLE IF EXISTS `caas_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_user` (
  `code` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
  `name` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户名',
  `userType` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '用户类型',
  `status` varchar(50) COLLATE utf8_unicode_ci NOT NULL COMMENT '用户状态',
  `mobile` varchar(30) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '手机号码',
  `password` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
  `email` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '邮箱',
  `isActive` tinyint(1) NOT NULL DEFAULT '1',
  `originCode` varchar(150) COLLATE utf8_unicode_ci DEFAULT NULL,
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_user_role`
--

DROP TABLE IF EXISTS `caas_user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_user_role` (
  `userCode` varchar(30) COLLATE utf8_unicode_ci NOT NULL DEFAULT '',
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_user_token`
--

DROP TABLE IF EXISTS `caas_user_token`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `caas_user_token` (
  `userCode` bigint(20) NOT NULL COMMENT '用户编码',
  `token` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT 'token',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`userCode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='用户验证码表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `caas_user_upload`
--

DROP TABLE IF EXISTS `caas_user_upload`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-02-04 11:07:36
