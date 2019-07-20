ALTER TABLE caas_app ADD `key` varchar(150)  not null  AFTER `backUrl`;
update caas_app set `key`=`code`;

ALTER TABLE caas_app ADD UNIQUE INDEX index_key ( `key` );
