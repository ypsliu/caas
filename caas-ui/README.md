#CAAS前端程序
##环境安装：
###node.js
下载地址：<a href="https://nodejs.org/en/download/">https://nodejs.org/en/download/</a><br/>
配置环境变量：export PATH=$PATH:NODE_HOME/bin
##初始化工程：
进入当前（README.md所在）目录；<br>
运行：npm install
##Nginx配置：

    upstream caas {
        ip_hash;
        server localhost:8080;
    }

    server {
        listen       80;
        server_name  caas-admin;

        location / {
            root   caas-ui/admin;
            index  index.html index.htm;
        }

        location /api {
            proxy_pass              http://caas;
            proxy_connect_timeout   10s;
            proxy_read_timeout  500s;
            proxy_send_timeout 500s;
        }
    }

    server {
        listen       80;
        server_name  caas-user;

        location / {
            root   caas-ui/user;
            index  index.html index.htm;
        }

        location /api {
            proxy_pass              http://caas;
            proxy_connect_timeout   10s;
            proxy_read_timeout  500s;
            proxy_send_timeout 500s;
        }
    }

###访问：
####管理控制台：
<a href="http://localhost">http://localhost</a>
####用户：
<a href="http://localhost/user/login.html">http://localhost/user/login.html</a>
####API：
http://localhost/api/v1/XXXX
##压缩／恢复
###安装grunt client
npm install grunt-cli -g
###压缩
grunt minifyAdminJs

grunt minifyUserJs
###恢复
grunt recoverAdminJs

grunt recoverUserJs
