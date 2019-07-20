define(["md5", "jquery", "jquery.validation", "jquery.storageapi"], function(md5, $) {

	$.ajaxSetup({
        beforeSend: function(jqXHR, settings) {
        	settings.url = "/api/v1" + settings.url;
            var authToken = $.sessionStorage.get("x-auth-token");
            if(authToken) {
            	jqXHR.setRequestHeader("x-auth-token", authToken);
            }
        },
        complete : function(jqXHR, textStatus) {
        	var authToken = jqXHR.getResponseHeader("x-auth-token");
        	if(authToken) {
        		$.sessionStorage.set("x-auth-token", authToken);
        	}
        },
        error: function (jqXHR, textStatus, errorThrown) {
        }
    });

    (function() {
    	_getUser(function(user) {
    		if(user) {
    			$("#beforeLogin").remove();
    			$("#afterLogin").show();
    			$("#beforeLoginLink").show();
    			$("#afterLoginLink").remove();
    			$("#apply").attr("href", $("#apply").attr("href") + "?userCode=" + user.code);
    			$("#logout").on("click", function() {
    				_logout(function() {
    					window.location.href = "/";
    				});
    			});
    		} else {
    			$("#beforeLogin").show();
    			$("#afterLogin").remove();
    			$("#beforeLoginLink").remove();
    			$("#afterLoginLink").show();
    		}
    	}, true);
    })();

    function _logout(callback) {
    	$.ajax({
			url : "/oauth2/logout",
			success : function(result, textStatus, xhr) {
				$.sessionStorage.set("user", null);
				callback();
			},
			error : function(xhr, textStatus, errorThrown) {
				
			}
		});
    }

    function _getUser(callback, refresh) {
    	if($.sessionStorage.get("user")) {
    		callback($.sessionStorage.get("user"));
    		if(refresh) {
    			_refresh();
    		}
    	} else {
	        _refresh(callback);
    	}
    }

    function _refresh(callback) {
    	$.ajax({
            url : "/oauth2/me",
            success: function(user, textStatus, xhr) {
            	if(user) {
            		$.sessionStorage.set("user", user);
            		setTimeout(function() {
            			$.sessionStorage.set("user", null);
            		}, 1200 * 1000);
            	} else {
            		$.sessionStorage.set("user", null);
            	}
            	if(callback) {
            		callback(user);
            	}
            }, 
            error: function (xhr, textStatus, errorThrown) {
            }
        });
    }

    function _isLogin() {
    	return $.sessionStorage.get("user");
    }

	$.fn.verifiable = function(url) {
		var input = $(this);
		var container = input.closest("div.input-group");
		input.off("change");
		input.on("change", function() {
			if(container.validate() && input.val().length > 0) {
				if(input.attr("ignore-value") && input.attr("ignore-value") == input.val()) {
					container.addClass("ok");
					container.removeClass("error");
					container.removeClass("invalid");
					container.find(".error-msg").hide();
				} else {
					container.find("span.checking").show();
					$.ajax({
						url : url + input.val(),
						success : function(result, textStatus, xhr) {
							container.find("span.checking").hide();
							if(result.success) {
								container.addClass("ok");
								container.removeClass("error");
								container.removeClass("invalid");
								container.find(".error-msg").hide();
							} else {
								container.addClass("error");
								container.removeClass("ok");
								container.removeClass("invalid");
								container.find("div.error-msg[verify='true']").show();
							}
						},
						error : function(xhr, textStatus, errorThrown) {
							container.find("span.checking").hide();
						}
					});
				}
			}
		});
	}

	function _getQueryValue(keys) {
		if(!$.isArray(keys)) {
			keys = [keys];
		}
		var url = window.location.href;
		var paramPairs = url.substring(url.indexOf("?") + 1).split("&");
		var value;
		for(var i = 0;i < paramPairs.length;i ++) {
			var parts = paramPairs[i].split("=");
			for(var j = 0;j < keys.length;j ++) {
				if(parts[0] === keys[j]) {
					value = parts[1];
					break;
				}
			}
			if(value != undefined) {
				break;
			}
		}
		return value;
	}

	function _getRedirectUrl() {
		var redirectUrl = _getQueryValue([
			"backUrl", 
			"redirectUrl", 
			"redirectUri",
			"back_url",
			"redirect_url",
			"redirect_uri"
		]);
		if(redirectUrl) {
			return decodeURIComponent(redirectUrl);
		}
	}

	function _getAppKey() {
		return _getQueryValue(["app_key", "appKey", "client_id", "clientId"]);
	}

	function _getResponseType() {
		return _getQueryValue(["response_type", "responseType"]);
	}

	function _getState() {
		return _getQueryValue("state");
	}

	function _serializeData(form) {
		var obj = form.serializeObject();
		var data = "";
		for(var key in obj) {
			if(key === "password" || key === "old_password") {
				obj[key] = calcMD5(obj[key]);
			}
			if(obj[key].length > 0) {
				if(data.length > 0) {
					data = data + "&";
				}
				data = data + key + "=" + obj[key];
			}
		}

		return data;
	}

	var vcodeInProgress;
	var vcodeReq;
	function _getVcode(callback) {
		if(!vcodeInProgress) {
			vcodeInProgress = true;
			vcodeReq = $.ajax({
				url : "/common/base64vimg",
				xhrFields : {
				    withCredentials : true
				},
				success : function(result, textStatus, xhr) {
					callback("data:image/png;base64," + result);
					vcodeInProgress = false;
				},
				error : function(xhr, textStatus, errorThrown) {
					vcodeInProgress = false;
				}
			});
		} else {
			if(vcodeReq) {
				vcodeReq.abort();
			}
		}
	}

	function _getQueryString() {
		var url = window.location.href;
		if(url.indexOf("?") > 0) {
			return url.substring(url.indexOf("?") + 1);
		}
	}

	return {
		getRedirectUrl : _getRedirectUrl,
		getAppKey : _getAppKey,
		getState : _getState,
		getResponseType : _getResponseType,
		getQueryValue : _getQueryValue,
		serializeData : _serializeData,
		getVcode : _getVcode,
		getUser : _getUser,
		logout : _logout,
		isLogin : _isLogin,
		getQueryString : _getQueryString
	}
});