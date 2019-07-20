-- 此文件用于 1.3.0升级后， 历史数据的修复以兼容程序运行
--针对不同的app 以appCode =1为例
--1)创建默认的subject, 
--2)创建默认的operation
--3)为已有角色添加subject
--4)为已有的role-resource关系添加操作
insert into `caas_subject`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values('1','default_subject', '1', '0', '1', now(), '1');
insert into `caas_operation`(`code`,`name`,`appCode`, `removed`,`creationUser`, `creationTime`, `version`) values('1','default_ops', '1', '0', '1', now(), '1');
update caas_role set `subjectCode`=1 ,`parent`=0, `order`=1 where `appCode`=1;
update caas_role_resource set operationCode=1 where roleCode in (select `code` from caas_role where appCode=1);