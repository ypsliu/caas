define(["app/common", "md5", 
	"jquery", "jquery.validator", "jquery.utility", "jquery.prompt"], function(common, md5, $) {
	var form = $("form");
	$("#save").on("click", function() {
		_clearError();
		var valid = form.validate();
		var same = _comparePassword();
		var hasError = form.find("div.input-group.error").size() > 0;
		if(hasError) {
			form.find("div.input-group.error").find("div.error-msg[verify='true']").show();
		}
		if(valid && same && !hasError) {
			var data = form.serializeObject();
			data.old_password = calcMD5(data.old_password);
			data.password = calcMD5(data.password); 
			$.ajax({
				url : form.attr("action"),
				type : form.attr("method"),
				contentType : "application/json",
				data : JSON.stringify(data),
				success : function(result, textStatus, xhr) {
					if(result.success) {
						_onSuccess();
					} else {
						_onError(result.errorCode);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
				}
			});
		}
	});

	common.getUser(function(user) {
		form.find("input[name='name']").val(user.name);
	});

	_refreshVCode();
	$("#refresh").trigger("click");

	$.each($("div[verify='true']").closest("div.input-group").find("input"), function(i, value) {
		$(value).verifiable($(value).attr("verify-uri"));
	});

	function _onSuccess(userCode) {
		$.info("密码修改成功，请重新登录。", "登录", function() {
			common.logout(function() {
				window.location.href = "/login.html";
			});
		})
	}

	function _clearError() {
		$("div.alert-danger").hide();
	}

	function _onError(errorCode) {
		$("div.input-group[role='vcode']").removeClass("ok");
		form.find("input[name='vcode']").val("");
		$("div.alert-danger").show();
		if(errorCode === "E9021") {
			$("div.alert-danger strong").text("用户名或密码错误！");
			$("div.alert-danger span").text("");
		} else {
			$("div.alert-danger strong").text("修改密码失败！");
			$("div.alert-danger span").text("请重试。");
		}
		$("#refresh").trigger("click");
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
});