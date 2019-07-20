
insert into `caas_admin_user`(`name`, `password`, `email`, `superUser`, `enabled`, `removed`, `creationTime`, `creationUser`, `version`)
 values('caas-super-admin', md5(md5('the default password')), 'admin@caas.rongcapital.cn', 1, 1, 0, now(), 'STSTEM', 1);
