var Agent = require("caas-agent-nodejs");
var config = require("./config.js")();
var agent = new Agent(
    config.caas.host, 
    config.caas.port, 
    config.caas.urlPrefix, 
    config.caas.appKey, 
    config.caas.appSecret
);
var exports = module.exports = {};

exports.init = function(req, res) {
	var app = process.env.NODE_ENV || "app1";
	if(req.session.user) {
		var accessToken = req.session.user.accessToken;
	    agent.getAuthCode(accessToken, function(err, result) {
	        if(!err) {
	        	var targetApp;
	        	if(app === "app1") {
					targetApp = "app2";
				} else {
					targetApp = "app1";
				}
	        	var configTarget = require("./config.js")(targetApp);
	        	var targetUrl = "http://" + configTarget.server.host + ":" + configTarget.server.port + "/auth";
			    _renderHome({
			    	app : app,
			    	targetApp : targetApp,
			    	targetUrl : targetUrl,
			    	authCode : result.auth_code
			    });
	        } else {
	        	_renderHome({
	        		app : app
	        	});
	        }
	    });
	} else {
		_renderHome({
    		app : app
    	});
	}

	function _renderHome(obj) {
		res.render("home.html", obj);
	}
}