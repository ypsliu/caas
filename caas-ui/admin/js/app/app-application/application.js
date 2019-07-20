define(["app/components/logout", "app/components/nav/nav-view",
	"jquery", "jquery.utility", "jquery.validator", "jquery.prompt", "jquery.verifiable"], function(logout, navView, $) {

	function _init(userCode) {

		$.each($("input[validation-uri]"), function(i, value) {
			$(value).verifiable($(this).attr("validation-uri"));
		});

		$("#generateKey, #generateSecret").on("click", function() {
			$(this).prev().val($.uuid().replace(new RegExp("\\-", "gm"), ""));
		});


		function _onClick() {
			if($("form").validate()
				&& $("span.aui-iconfont-warning").is(":hidden")) {

				$("#apply").off("click");
				var data = $("form").serializeObject();
				if($("form input[name='checkSign']").prop("checked")) {
					data.checkSign = true;
				} else {
					data.checkSign = false;
				}
				if($("form input[name='checkResource']").prop("checked")) {
					data.checkResource = true;
				} else {
					data.checkResource = false;
				}
				if($("form input[name='appType']").prop("checked")) {
					data.appType = "PUBLIC";
				} else {
					data.appType = "PROTECTED";
				}
				if($("form input[name='emailNotify']").prop("checked")) {
					data.emailNotify = true;
				} else {
					data.emailNotify = false;
				}
				
				$.ajax({
					url : "/admin/apply/" + userCode + "/app",
					type : "POST",
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(result, textStatus, xhr) {
						$.info("申请已经发送，等待审批。", function() {
							logout.logout(function() {
								navView.reset();
								window.location.href = "#login";
							});
						});
					},
					error : function(xhr, textStatus, errorThrown) {
						$("#apply").on("click", function() {
							_onClick();
						});
					}
				});
			}
		}

		$("#apply").on("click", function() {
			_onClick();
		});
		initCheckVcode();
	}
	function initCheckVcode(){

		$(".checkVcodeItem").on('click',function(){
			var text=$(this).text();
				$("#checkVcodeVal").text(text);
			if(text=="校验"){
		$("#checkVcode").attr("value","YES");
			}
			if(text=="不校验"){
		$("#checkVcode").attr("value","NO");
			}
			if(text=="无设置"){
		$("#checkVcode").attr("value","NO_SET");
			}
		});
	}
	return {
		initialize : _init
	}
});
