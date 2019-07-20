-- new table caas_app_user
DROP TABLE IF EXISTS `caas_app_user`;
CREATE TABLE `caas_app_user` (
  `appCode` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '管理员编码',
  `userCode` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '管理员名称',
  `creationTime` datetime NOT NULL COMMENT '创建日期',
  `updateTime` datetime DEFAULT NULL COMMENT '更新日期',
  `comment` varchar(300) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '备注',
  `creationUser` varchar(100) COLLATE utf8_unicode_ci NOT NULL COMMENT '创建人',
  `updateUser` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL COMMENT '更新人',
  `version` bigint(20) NOT NULL DEFAULT '1' COMMENT '版本号',
  PRIMARY KEY (`appCode`,`userCode`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用用户关系表';


-- new table caas_app_admin
DROP TABLE IF EXISTS `caas_app_admin`;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci COMMENT='应用管理員关系表';


--add userType on admin user table
ALTER TABLE caas_admin_user ADD userType varchar(100) default 'SIGNUP' NOT NULL  AFTER `appCode`;
--add column checkVcode for app no_set|yes|no
ALTER TABLE caas_app ADD  `checkVcode` varchar(10)  default 'NO_SET' NOT NULL  COMMENT '是否校验验证码'   AFTER `checkResource`;
	 

--allow the column password to be null  on admin user table
ALTER TABLE caas_admin_user MODIFY `password` varchar(150) NULL;
--allow the column password to be null  on  user table
ALTER TABLE caas_user MODIFY `password` varchar(150) NULL;

--fix current data due to add app_user table 
INSERT INTO caas_app_user(
    appCode, 
    userCode, 
    creationTime, 
    creationUser
) SELECT DISTINCT cu.code userCode,
		   cr.appCode,
		   NOW(),
		   'system'
      FROM caas_user cu
INNER JOIN caas_user_role cur
        ON cu.code = cur.userCode
INNER JOIN caas_role cr
        ON cr.code = cur.roleCode;

--fix current data due to add app_admin table 
INSERT INTO caas_app_admin( appCode, adminCode, creationTime, creationUser) SELECT appCode, code adminCode, NOW(),'system' FROM caas_admin_user WHERE superUser = 0;

 

