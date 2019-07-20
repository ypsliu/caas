define(["app/common", "md5", 
	"jquery", "jquery.validator", "jquery.utility", "jquery.prompt", "jquery.loading"], function(common, md5, $) {

	var loginTryTimes = 0;
	var form = $("form");
	var responseType = common.getResponseType();
	var state = common.getState();
	var redirectUrl = common.getRedirectUrl();
	
	(function _initApp() {
		_initSignup();
		var appKey = common.getAppKey();
		if(appKey != undefined) {
			$("div.form1").removeClass("col-sm-offset-3 col-sm-6 col-md-offset-4 col-md-4")
				.addClass("col-sm-offset-2 col-sm-8 col-md-offset-3 col-md-6");
			$("#form").removeClass("col-sm-12")
				.addClass("col-sm-6")
				.css("border-right", "1px solid #eee");
			$("#login").text("登录并授权");
			$("#grantArea").show();

			$("#term").on("change", function() {
				if($(this).is(":checked")) {
					$("#login").removeAttr("disabled");
					$("#auth").removeAttr("disabled");
				} else {
					$("#login").attr("disabled", true);
					$("#auth").attr("disabled", true);
				}
			});

			$.ajax({
				url : "/common/info/app/" + appKey,
				success : function(result, textStatus, xhr) {
					$("#appName").text(result.name);
					if(!redirectUrl) {
						redirectUrl = result.backUrl;
					}
					_initAuth(result.name);
				},
				error : function(xhr, textStatus, errorThrown) {
				}
			});
		} else {
			_initAuth();
			$("#grantArea").remove();
		}
	})();

	function _initSignup() {
		var queryString = common.getQueryString();
		if(queryString) {
			$("a[role='signup']").attr("href", "/signup.html?" + queryString);
		} else {
			$("a[role='signup']").attr("href", "/signup.html");
		}
	}

	function _initAuth(appName) {
		if(responseType === "code") {
			common.getUser(function(user) {
				if(user) {
					if(appName) {
						$("#username").removeClass("col-sm-12")
							.addClass("col-sm-6")
							.css("border-right", "1px solid #eee");
					}
					$("#form").remove();
					$("#username").show();
					$("#username p").text(user.name);
					$("#auth").removeAttr("disabled");
					$("#auth").on("click", function() {
						var form = $("<form name='success' method='get'></form>");
						form.attr("action", "/api/v1/oauth2/authorize");

						var redirectUriInput = $("<input name='redirect_uri'></input>");
						redirectUriInput.val(redirectUrl);
						form.append(redirectUriInput);

						var clientId = $("<input name='client_id'></input>");
						clientId.val(common.getAppKey());
						form.append(clientId);

						var responseTypeInput = $("<input name='response_type'></input>");
						responseTypeInput.val(responseType);
						form.append(responseTypeInput);

						var stateInput = $("<input name='state'></input>");
						stateInput.val(state);
						form.append(stateInput);

						$("body").append(form);
						form.submit();
					});
				} else {
					_initLogin();
				}
			});
		} else {
			_initLogin();
		}
	}

	function _initLogin() {
		$("#form").show();
		$("#username").remove();
		$("#login").removeAttr("disabled");
		$("#login").on("click", function() {
			_clearError();
			var url = "/oauth2/login";
			if(form.validate()) {
				loginTryTimes ++;
				var data = form.serializeObject();
				data.password = calcMD5(data.password); 
				$.ajax({
					url : url,
					type : "POST",
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(result, textStatus, xhr) {
						if(result.errorCode) {
							_onError(result.errorCode);
						} else {
							_onSuccess(result);
						}
					},
					error : function(xhr, textStatus, errorThrown) {
						if(xhr.status === 401) {
						}
					}
				});
			}
		});
	}

	function _onSuccess(result) {
		if(redirectUrl != undefined) {
			var target = redirectUrl + "?code=" + result.auth_code;
			if(state != undefined) {
				target = target + "&state=" + state;
			}
			window.location.href = target;
		} else {
			if(!result.active) {
				$.confirm("你的邮箱尚未激活，请登录邮箱激活<br/>或者点击发送按钮再次发送激活邮件", "知道了", "发送", function() {
					window.location.href = "/";
				}, function() {
					_sendActivateEmail(result.email);
				});
			} else {
				window.location.href = "/";
			}
		}
	}

	function _sendActivateEmail(email) {
		$.loading.start();
		$.ajax({
			url : "/user/email/activation",
			type : "POST",
			contentType : "application/x-www-form-urlencoded",
			data : "email=" + email,
			success : function(result, textStatus, xhr) {
				$.loading.stop();
				$.info("激活邮件发送成功！<br/>请登录邮箱（" + email + "）点击激活按钮", "知道了", function() {
					window.location.href = "/";
				});
			},
			error : function(xhr, textStatus, errorThrown) {
				$.loading.stop();
				if(xhr.status === 400) {
					var response = JSON.parse(xhr.responseText);
					if(response.errorMessage.indexOf("输入数据已经存在了") === 0) {
						$.info("该邮箱已经激活", "知道了", function() {
							window.location.href = "/";
						});
					}
				} else if(xhr.status === 500) {
					_fail();
				}
			}
		});

		function _fail() {
			$.alert("发送邮件失败，请重试！", "发送", function() {
				_sendActivateEmail(email);
			});
		}
	}

	function _clearError() {
		$("div.alert-danger").hide();
	}

	function _onError(errorCode) {
		$("div.input-group[role='vcode']").removeClass("ok");
		form.find("input[name='vcode']").val("");
		if(errorCode === "E9015") {
			$("div.alert-danger span").html("验证码错误");
		} else if(errorCode === "E9021" || errorCode === "E9022") {
			$("div.alert-danger span").html("用户名或密码错误");
		} else if(errorCode === "E9023") {
			$("div.alert-danger span").html("用户已停用");
		}
		
		$("div.alert-danger").show();
		if(loginTryTimes >= 3 || errorCode === "E9015") {
			loginTryTimes = 3;
			$("div[role='vcode']").show();
			$("div[role='vcode'] img").attr("src", "/api/v1/common/vimg?timestamp=" + new Date().getTime());
			_refreshVCode();
			var vcode = form.find("input[name='vcode']");
			vcode.verifiable("/oauth2/vcode/");
		} else {
			$("div[role='vcode']").hide();
			$("div.alert-danger span").html("");
		}
	}

	function _refreshVCode() {
		$("#refresh").off("click");
		$("#refresh").on("click", function() {
			$(this).off("click");
			$("div[role='vcode'] img").attr("src", "/api/v1/common/vimg?timestamp=" + new Date().getTime());
			_refreshVCode();
		});
	}
});