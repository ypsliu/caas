define(["jquery", "jquery.utility", "jquery.validator", "jquery.prompt", "jquery.verifiable"], function($) {

	function _init(app) {
		if(app && !app.pending) {
			$("input[name='name']").verifiable("/validation/app/name/");

			$("#generate").on("click", function() {
				$(this).prev().val($.uuid().replace(new RegExp("\\-", "gm"), ""));
			});

			$("#save").on("click", function() {
				if($("form").validate() && $("span.aui-iconfont-warning").is(":hidden")) {
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
						url : "/admin/app/" + data.code,
						type : "PUT",
						contentType : "application/json",
						data : JSON.stringify(data),
						success : function(result, textStatus, xhr) {
							$.info("修改应用成功", function() {});
						}
					});
				}
			});
		}
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
