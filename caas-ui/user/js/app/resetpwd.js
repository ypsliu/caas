define(["app/common", "md5", 
	"jquery", "jquery.validator", "jquery.utility", "jquery.prompt"], function(common, md5, $) {

	$("input[name='token']").val(common.getQueryValue("token"));
	$("input[name='email']").val(common.getQueryValue("email"));

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
			$("input[name='password']").val(calcMD5($("input[name='password']").val()));
			$.ajax({
				url : "/user/reset",
				type : "POST",
				contentType : "application/x-www-form-urlencoded",
				data : form.serialize(),
				success : function(result, textStatus, xhr) {
					if(result.success) {
						_onSuccess();
					} else {
						_onError(result.errorCode);
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					_onError();
				}
			});
		}
	});

	function _onSuccess(userCode) {
		$.info("密码重置成功，请重新登录。", "登录", function() {
			common.logout(function() {
				window.location.href = "/login.html";
			});
		})
	}

	function _clearError() {
		$("div.alert-danger").hide();
	}

	function _onError(errorCode) {
		$("div.alert-danger").show();
		$("div.alert-danger strong").text("重置密码失败！");
		$("div.alert-danger span").text("请重试。");
	}

	form.find("input[name='password']").on("change", function() {
		var p1Container = $(this).closest("div.input-group");
		p1Container.removeClass("invalid");
		p1Container.find("div.error-msg").hide();
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
