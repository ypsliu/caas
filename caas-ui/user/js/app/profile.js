define(["app/common", "md5",
	"jquery", "jquery.validator", "jquery.utility", "jquery.prompt"], function(common, md5, $) {

	var form = $("form");
	_initForm();
	_refreshVCode();
	$("#refresh").trigger("click");
	$.each($("div[verify='true']").closest("div.input-group").find("input"), function(i, value) {
		$(value).verifiable($(value).attr("verify-uri"));
	});

	$("#save").on("click", function() {
		_clearError();
		var valid = form.validate();
		var hasError = form.find("div.input-group.error").size() > 0;
		if(hasError) {
			form.find("div.input-group.error").find("div.error-msg[verify='true']").show();
		}
		if(valid && !hasError) {
			var data = form.serializeObject();
			$.ajax({
				url : form.attr("action"),
				type : form.attr("method"),
				contentType : "application/json",
				data : JSON.stringify(data),
				success : function(result, textStatus, xhr) {
					if(result.success) {
						common.logout(function() {
							_onSuccess();
						});
					} else {
						_onError(result.errorCode);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					if(xhr.status === 400) {
						_onError("400");
					}
				}
			});
		}
	});

	function _onSuccess() {
		if(form.find("input[name='redirectUrl']").val().length > 0) {
			window.location.href = decodeURIComponent(form.find("input[name='redirectUrl']").val());
		} else {
			$.info("修改成功，点击登录按钮重新登录。", "登录", function() {
				window.location.href = "/login.html";
			});
		}
	}

	function _clearError() {	
		$("div.alert-danger").hide();
		$("div.alert-danger span").html("");
	}

	function _onError(errorCode) {
		$("div.input-group[role='vcode']").removeClass("ok");
		form.find("input[name='vcode']").val("");
		_refreshVCode();
		if(errorCode === "E9041") {
			$("div.alert-danger span").html("认证code不合法");
		} else if(errorCode === "400") {
			$("div.alert-danger span").html("用户，邮箱或者电话已经存在。");
		}
		$("div.alert-danger").show();
	}

	function _initForm() {
		common.getUser(function(user) {
			if(user) {
				$("input[name='user_name']").val(user.name);
				$("input[name='email']").val(user.email);
				$("input[name='mobile']").val(user.mobile);
				$("input[name='mobile']").attr("ignore-value", user.mobile);
				$("input[name='email']").attr("ignore-value", user.email);
			}
		});
	}

	function _getData() {
		var url = window.location.href;
		var paramPairs = url.substring(url.indexOf("?") + 1).split("&");
		var data = {};
		for(var i = 0;i < paramPairs.length;i ++) {
			var parts = paramPairs[i].split("=");
			data[parts[0]] = parts[1];
		}
		return data;
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