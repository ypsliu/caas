var cluster = require("cluster");
var cpus = require("os").cpus();
var config = require("./modules/config.js")();
var serverConfig = config.server;
var ws = require("./modules/ws.js");
var express = require("express");
var sessionstore = require("sessionstore");
var fs = require("fs");

if (cluster.isMaster) {
    for (var i = 0; i < 1; i++) {
        cluster.fork();
    }
    cluster.on("disconnect", function(worker) {
        console.error("process " + worker.process.pid + " disconnect!");
        cluster.fork();
    });
} else {
    GLOBAL.domain = require("domain").create();
    var emitter = {};
    domain.on("error", function(err) {
        onError(err);
    });
    domain.run(function() {
        route();
    });
}

function routerHandler(func) {
    return function(req, res, next) {
        try {
            return func(req, res, next);
        } catch(err) {
            err.domain = GLOBAL.domain;
            domain.emit("error", err);
        }
    }
}

function route() {
    var router = start();

    var methods = [router.all, router.get, router.delete, router.post, router.put];
    for(var i = 0;i < methods.length;i ++) {
        methods[i] = function(route, callback) {
            methods[i].call(router, route, routerHandler(callback));
        }
    }

    GLOBAL.router = router;

    var filter = require("./modules/filter.js");
    router.all("/", filter.before);
    router.all("/*", filter.before);

    var user = require("./modules/user.js");
    router.all("/user/*", user.authorise);

    fs.readdir("./routes", function(err, files) {
        for(var i = 0;i < files.length; i++) {
            require("./routes/" + files[i]);
        }
    });
}

function start() {
    var path = require("path");
    var app = express();
    var server = require("http").createServer(app);
    var router = express.Router();

    router.use(function(req, res, next) {
        domain.remove(emitter);
        emitter = {
            request : req,
            response : res,
            server : server
        }
        domain.add(emitter);
        next();
    });

    app.use(express.static(path.join(__dirname, "public")));
    app.set("views", __dirname + "/views")
    app.engine(".html", require("ejs").renderFile);
    app.set("view engine", "ejs");
    var bodyParser = require("body-parser");
    app.use(bodyParser.json({ "limit":"10000kb"}));
    app.use(bodyParser.urlencoded({
        extended : true
    }));
    var cookieParser = require("cookie-parser");
    app.use(cookieParser());

    // session
    var session = require("express-session");
    var MongoStore = require("connect-mongo")(session);
    var sessionStore = session({
        secret : "sample",
        cookie : {
            //secure : true,
            httpOnly: true,
            maxAge : 60000 * 200
        },
        store: sessionstore.createSessionStore(),
        rolling : true,
        resave : true,
        saveUninitialized : false
    });
    app.use(sessionStore);

    app.use("/", router);
    server.listen(serverConfig.port);

    return router;
}

function onError(err) {
    console.error("error", err.stack);
    var killtimer = setTimeout(function() {
        process.exit(1);
    }, 3000);
    killtimer.unref();
    cluster.worker.disconnect();
    if(err.domain.members.length > 0) {
        var member = err.domain.members[0];
        var req = member.request;
        var res = member.response;
        var server = member.server;
        server.close();
        if(res) {
            ws.error(res);
        }
    }
}
