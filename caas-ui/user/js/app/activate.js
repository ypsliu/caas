define(["app/common", "jquery", "jquery.validator", "jquery.utility", "jquery.prompt", "jquery.loading"], function(common, $) {
	var email = common.getQueryValue("email");
	var token = common.getQueryValue("token");

	$("div.jumbotron span").text(email);
	$("input[name='email']").val(email);
	$("input[name='token']").val(token);

	$.ajax({
		url : "/user/activate",
		type : "POST",
		contentType : "application/x-www-form-urlencoded",
		data : common.serializeData($("form")),
		success : function(result, textStatus, xhr) {
			if(!result.errorCode) {
				$("div.jumbotron").hide();
				$("#activate-success").show();
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			$("div.jumbotron").hide();
			if(xhr.status === 400) {
				var response = JSON.parse(xhr.responseText);
				if(response.errorMessage.indexOf("输入数据已经存在了") === 0) {
					$("#duplicate-activate").show();
				} else if(response.errorMessage.indexOf("输入参数错误") === 0) {
					$("#activate-failure").show();

					$("a[role='send']").on("click", function() {
						$.loading.start();
						$.ajax({
							url : "/user/email/activation",
							type : "POST",
							contentType : "application/x-www-form-urlencoded",
							data : "email=" + email,
							success : function(result, textStatus, xhr) {
								$.loading.stop();
								$("div.jumbotron").hide();
								$("#send-success").show();
							},
							error : function(xhr, textStatus, errorThrown) {
								$.loading.stop();
								$("div.jumbotron").hide();
								$("#send-failure").show();
							}
						});
					});
				}
			} else if(xhr.status === 404) {
				$("#email-not-found").show();
			} else if(xhr.status === 500) {
				$("#send-failure").show();
			}
		}
	});
});