var exports = module.exports = {};
var crypto = require("crypto");
var Agent = require("caas-agent-nodejs");
var ws = require("./ws.js");
var util = require("./util.js");
var config = require("./config.js")();
var agent = new Agent(
    config.caas.host, 
    config.caas.port, 
    config.caas.urlPrefix, 
    config.caas.appKey, 
    config.caas.appSecret
);

exports.loginInit = function(req, res) {
    res.render("login.html");
}

exports.signupInit = function(req, res) {
    res.render("signup.html");
}

exports.changePasswordInit = function(req, res) {
    res.render("change-password.html");
}

exports.auth = function(req, res) {
    _doAuth(req.query.code, req, res);
}

exports.authByPost = function(req, res) {
    _doAuth(req.body.code, req, res);
}

function _doAuth(authCode, req, res) {
    agent.auth(authCode, function(err, result) {
        console.log("auth result:");
        console.log(result);
        if(!err) {
            req.session.user = {
                accessToken : result.access_token
            }
            res.redirect("/");
        } else {
            ws.forbidden(res);
        }
    });
}

exports.info = function(req, res) {
    res.render("info.html");
}

exports.signup = function(req, res) {
    var xAuthToken = req.session.xAuthToken;
    agent.signup(
        req.body.user_name,
        crypto.createHash("md5").update(req.body.password).digest("hex"), 
        req.body.email, 
        req.body.mobile, 
        req.body.vcode, 
        xAuthToken, 
        function(err, result) {
            if(!err && result.success) {
                req.session.xAuthToken = result.xAuthToken;
                res.redirect("/login");
            } else {
                ws.error(res);
            }
        }
    );
}

exports.login = function(req, res) {
    var xAuthToken = req.session.xAuthToken;
    agent.login(
        req.body.login_name, 
        crypto.createHash("md5").update(req.body.password).digest("hex"), 
        req.body.vcode, 
        xAuthToken, 
        function(err, result) {
            if(!err && result.success) {
                req.session.xAuthToken = result.xAuthToken;
                _doAuth(result.auth_code, req, res);
            } else {
                ws.error(res);
            }
        }
    );
}

exports.changePassword = function(req, res) {
    var xAuthToken = req.session.xAuthToken;
    agent.changePassword(
        req.session.user.accessToken, 
        crypto.createHash("md5").update(req.body.old_password).digest("hex"), 
        crypto.createHash("md5").update(req.body.password).digest("hex"),
        req.body.vcode, 
        xAuthToken, 
        function(err, result) {
            if(!err && result.success) {
                req.session.user = null;
                res.redirect("/login");
            } else {
                ws.error(res);
            }
        }
    );
}

exports.base64Vcode = function(req, res) {
    var xAuthToken = req.session.xAuthToken;
    agent.base64Vcode(xAuthToken, function(err, result) {
        if(!err) {
            req.session.xAuthToken = result.xAuthToken;
            ws.ok(res, result.result);
        } else {
            ws.error(res);
        }
    });
}

exports.validateUserName = function(req, res) {
    _doValidation("UserName", req.params.name, req, res);
}

exports.validateEmail = function(req, res) {
    _doValidation("Email", req.params.email, req, res);
}

exports.validateMobile = function(req, res) {
    _doValidation("Mobile", req.params.mobile, req, res);
}

exports.validateVcode = function(req, res) {
    _doValidation("Vcode", req.params.vcode, req, res);
}

function _doValidation(type, value, req, res) {
    var xAuthToken = req.session.xAuthToken;
    agent["validate" + type].call(agent, value, xAuthToken, function(err, result) {
        if(!err) {
            req.session.xAuthToken = result.xAuthToken;
            ws.ok(res, result);
        } else {
            ws.error(res);
        }
    });
}

exports.logout = function(req, res) {
    var accessToken = req.session.user.accessToken;
    agent.logout(accessToken, function(err, result) {
        if(!err) {
            if(result.success) {
                req.session.user = null;
                res.render("logout.html");
            }
        } else {
            ws.error(res);
        }
    });
}

exports.authorise = function(req, res, next) {
    if(req.session.user) {
        var accessToken = req.session.user.accessToken;
        agent.checkAuth(accessToken, "/test-resource-1", "查询", function(err, result) {
            if(!err && result.success) {
                next();
            } else {
                req.session.user = null;
                _toLogin();
            }
        });
    } else {
        _toLogin();
    }

    function _toLogin() {
        var url = "http://" + config.server.host + ":" + config.server.port + "/auth";
        res.redirect("http://caas-user/login.html?redirect_uri=" + encodeURIComponent(url) + "&client_id=" + config.caas.appKey);
    }
}