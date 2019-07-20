define(["md5", "app/components/admin-user", 
	"jquery", "jquery.utility", "jquery.validator", "jquery.prompt", "jquery.verifiable"], function(md5, adminUser, $) {

	function _init() {
	
		$.each($("input[validation-uri]"), function(i, value) {
			$(value).verifiable($(this).attr("validation-uri"));
		});

		$("input[name='password']").on("change", function() {
			_comparePassword();
		});
		$("input[name='password2']").on("change", function() {
			_comparePassword();
		});

		function _comparePassword() {
			var password = $("input[name='password']");
			var password2 = $("input[name='password2']");
			var p2Container = password2.closest("div.field-group");
			if(password.val().length > 0 && password2.val().length > 0) {
				p2Container.find("div.error").hide();
				if(password2.val() != password.val()) {
					p2Container.find("span.aui-iconfont-warning").show();
					p2Container.find("span.aui-iconfont-success").hide();
					p2Container.find("div[comparation='true']").show();
				} else {
					p2Container.find("span.aui-iconfont-warning").hide();
					p2Container.find("span.aui-iconfont-success").show();
					p2Container.find("div[comparation='true']").hide();
					return true;
				}
			} else {
				if(password2.val().length > 0) {
					p2Container.find("span.aui-iconfont-warning").hide();
					p2Container.find("span.aui-iconfont-success").hide();
					p2Container.find("div[comparation='true']").hide();
				}
			}
			return false;
		}

		function _onClick() {
			if($("form").validate() 
				&& _comparePassword() 
				&& $("span.aui-iconfont-warning").is(":hidden")) {

				$("#signup").off("click");
				var data = $("form").serializeObject();
				data.password = hex_md5(data.password);
				$.ajax({
					url : "/admin/register",
					type : "POST",
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(user, textStatus, xhr) {
						$.ajax({
							url : "/admin/login",
							type : "POST",
							contentType : "application/json",
							data : JSON.stringify({
								email : data.email,
								password : data.password
							}),
							success : function(result, textStatus, xhr) {
								adminUser.clear();
								window.location.href = "#admin/app/apply/" + user.code;
							},
							error : function(xhr, textStatus, errorThrown) {
							}
						});
					},
					error : function(xhr, textStatus, errorThrown) {
						$("#signup").on("click", function() {
							_onClick();
						});
					}
				});
			}
		}

		$("#signup").on("click", function() {
			_onClick();
		});
	}

	return {
		initialize : _init
	}
});