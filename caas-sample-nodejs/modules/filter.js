var exports = module.exports = {};

var funs = [];
var excludes = [];
exports.before = function(req, res, next) {
    //addFilter(basicAuth());
    addFilter(globalSetting());
    doFilter(req, res, next);
}

function doFilter(req, res, next) {
    for(var i = 0;i < funs.length;i ++) {
        if(!funs[i](req, res)) {
            return;
        }
    }
    next();
}

function addFilter(func) {
    funs.push(func);
}

function globalSetting() {
    return function(req, res) {
        res.locals.session = req.session;
        res.locals.url = req.originalUrl;
        return true;
    }
}

function basicAuth() {
    function _doAuth(auth) {
        var basic = auth.split(" ")[1];
        var buffer = new Buffer(basic, "base64");
        var pair = buffer.toString().split(":");
        if(pair[0] == "sample" && pair[1] == "123456") {
            return true;
        }
        return false;
    }

    function _401(res) {
        res.setHeader('WWW-Authenticate', 'Basic realm="Secure Area"');
        res.statusCode = 401;
        res.render("401.html");
        return false;
    }

    return function(req, res) {
        if(excludes.indexOf(req.originalUrl) < 0) {
            var auth = req.headers["authorization"];
            if(auth) {
                if(_doAuth(auth)) {
                    return true;
                } else {
                    return _401(res);
                }
            } else {
                return _401(res);
            }
        } else {
            return true;
        }
    }
}