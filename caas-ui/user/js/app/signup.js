define(["app/common", "md5", "app/app-role",
	"jquery", "jquery.validator", "jquery.utility", "jquery.prompt", "jquery.loading"], function(common, md5, appRole, $) {
	var form = $("form");
	$("#signup").on("click", function() {
		_clearError();
		var valid = form.validate();
		var same = _comparePassword();
		var hasError = form.find("div.input-group.error").size() > 0;
		if(hasError) {
			form.find("div.input-group.error").find("div.error-msg[verify='true']").show();
		}
		if(valid && same && !hasError) {
			var data = form.serializeObject();
			data.password = calcMD5(data.password);
			data.password2 = calcMD5(data.password2);
      		if(!$.isArray(data.app_apply)){
				var temp=[];
				temp.push(data.app_apply);
				data.app_apply=temp;
			}
			//data.roles = appRole.buildData();
			$.ajax({
				url : form.attr("action"),
				type : form.attr("method"),
				contentType : "application/json",
				data : JSON.stringify(data),
				success : function(result, textStatus, xhr) {
					if(result.success) {
						_onSuccess();
					} else {
						_onError();
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					_onError();
				}
			});
		}
	});

	_refreshVCode();
	$("#refresh").trigger("click");

	$.each($("div[verify='true']").closest("div.input-group").find("input"), function(i, value) {
		$(value).verifiable($(value).attr("verify-uri"));
	});

	$("#next").on("click", function() {
		var container = $(this).parent().next();
		if(container.is(":hidden")) {
			$(this).find("span").removeClass("glyphicon-menu-down").addClass("glyphicon-menu-up");
			container.slideDown(200);
		} else {
			$(this).find("span").removeClass("glyphicon-menu-up").addClass("glyphicon-menu-down");
			container.slideUp(200);
		}
	});

	function _onSuccess() {
		var url = "/login.html";
		var queryString = common.getQueryString();
		if(queryString) {
			url = url + "?" + queryString;
		}
		// $.info("恭喜你，注册成功！点击登陆按钮去登录", "登录", function() {
		// 	window.location.href = url;
		// });
		_sendActivateEmail($("input[name='email']").val());
	}

	function _clearError() {
		$("div.alert-danger").hide();
	}

	function _onError(errorCode) {
		$("div.input-group[role='vcode']").removeClass("ok");
		form.find("input[name='vcode']").val("");
		$("div.alert-danger").show();
	}

	function _refreshVCode() {
		$("#refresh").off("click");
		$("#refresh").on("click", function() {
			$(this).off("click");
			$("div[role='vcode'] img").attr("src", "/api/v1/common/vimg?timestamp=" + new Date().getTime());
			_refreshVCode();
		});
	}

	form.find("input[name='password']").on("change", function() {
		_comparePassword();
	});
	form.find("input[name='password2']").on("change", function() {
		_comparePassword();
	});

	function _comparePassword() {
		var password = form.find("input[name='password']");
		var password2 = form.find("input[name='password2']");
		var p2Container = password2.closest("div.input-group");
		if(password.val().length > 0 && password2.val().length > 0) {
			p2Container.find("div.error-msg").hide();
			if(password2.val() != password.val()) {
				p2Container.addClass("invalid");
				p2Container.removeClass("ok");
				p2Container.find("div[comparation='true']").show();
			} else {
				p2Container.removeClass("invalid");
				p2Container.addClass("ok");
				p2Container.find("div[comparation='true']").hide();
				return true;
			}
		} else {
			if(password2.val().length > 0) {
				p2Container.removeClass("invalid");
				p2Container.removeClass("ok");
				p2Container.find("div[comparation='true']").hide();
			}
		}
		return false;
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
					window.location.href = "/login.html";
				});
			},
			error : function(xhr, textStatus, errorThrown) {
				$.loading.stop();
				if(xhr.status === 400) {
					var response = JSON.parse(xhr.responseText);
					if(response.errorMessage.indexOf("输入数据已经存在了") === 0) {
						$.info("该邮箱已经激活", "知道了", function() {});
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
});
