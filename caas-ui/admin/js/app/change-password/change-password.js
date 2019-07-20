define(["app/components/logout", "app/components/nav/nav-view", "md5",
	"jquery", "jquery.utility", "jquery.validator"], function(logout, navView, md5, $) {
	function _init() {
		$("#save").on("click", function() {
			$("#error").hide();
			$("#notMatchPassword").hide();
			if($("form").validate() && _samePassword()) {
				var form = $("form[name='changePassword']");
				var data = form.serializeObject();
				data.oldPassword = hex_md5(data.oldPassword);
				data.newPassword = hex_md5(data.newPassword);
				data.newPasswordConfirm = hex_md5(data.newPasswordConfirm);
				$.ajax({
					url : form.attr("action"),
					type : form.attr("method"),
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(data, textStatus, xhr) {
						logout.logout(function() {
							navView.reset();
							window.location.href = "#change-password-success";
						});
					},
					error : function(xhr, textStatus, errorThrown) {
						if(xhr.status === 401) {
							$("#error").show();
						}
					}
				});
			}
		});

		function _samePassword() {
			if($("input[name='newPassword']").val() === $("input[name='newPasswordConfirm']").val()) {
				return true;
			}
			$("#notMatchPassword").show();
			return false;
		}
	}
	return {
		initialize : _init
	}
});