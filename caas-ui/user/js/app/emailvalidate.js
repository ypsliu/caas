define(["app/common", "jquery", "jquery.utility", "jquery.prompt", "jquery.validator", "jquery.loading"], function(common, $) {
 	$("#sendbtn").on("click",function(){
 		_send();
	});

	function _send() {
		if($("#mailform").validate()) {
 			$.loading.start();
 			$.ajax({
				url : "/user/email/reset",
				type : "POST",
				contentType : "application/x-www-form-urlencoded",
				data : $("#mailform").serialize(),
				success : function(result, textStatus, xhr) {
					$.loading.stop();
					if(result.success) {
						$.info("邮件发送成功！<br/>请登录邮箱（" + $("input[name='email']").val() + "）点击重置密码按钮", "知道了", function() {
							window.location.href = "/";
						});
					} else {
						$.alert("邮件发送失败！请确认邮箱后重试", "知道了", function() {
						});
					}
				},
				error : function(xhr, textStatus, errorThrown) {
					$.loading.stop();
					$.alert("邮件发送失败！请确认邮箱后重试", "知道了", function() {
					});
				}
			});
 		}
	}
});
