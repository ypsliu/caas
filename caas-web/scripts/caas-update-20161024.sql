
--add status for caas_app
ALTER TABLE caas_app ADD `status` varchar(50) AFTER `key`;
update caas_app set `status`='CONFIRMED';

-- add type for caas_app
ALTER TABLE caas_app ADD `appType` varchar(50) AFTER `status`;
update caas_app set `appType`='PUBLIC';

-- add roleType for caas_role
ALTER TABLE caas_role ADD roleType varchar(50) NOT NULL  AFTER `appCode`;
update caas_role set roleType='PUBLIC';

-- remove autoAuth column on caas_role
ALTER TABLE caas_role DROP column autoAuth;  

--remove applyEnabled column on caas_role
ALTER TABLE caas_role DROP column applyEnabled;  