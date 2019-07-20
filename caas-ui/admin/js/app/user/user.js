define([ "jquery", "jquery.utility","jquery.prompt" ], function($) {
	function _init() {
		$("#userError").hide();
		$(":button[name='onOff']").on("click", function() {
		  	var code = $(this).attr("code");
			var method = getMethod($(this).attr("value"));
			$.ajax({
				url : "/admin/user/"+code,
				type : method,
				contentType : "application/json",
				data : {code:code},
				success : function(data, textStatus, xhr) {
					Backbone.history.loadUrl(Backbone.history.fragment);
				},
				error : function(xhr, textStatus, errorThrown) {
					$("#userError").show();
				}
			});

		});

		$(":button[name='kickOut']").on("click", function() {
			$.confirm("确认踢出?", function() {
				var code = $(this).attr("code");
				var method = getMethod($(this).attr("value"));
				$.ajax({
					url: "/admin/user/" + code + "/kickout",
					type: "DELETE",
					contentType: "application/json",
					success: function (data, textStatus, xhr) {
						Backbone.history.loadUrl(Backbone.history.fragment);
						$.info("踢出用户成功!", function () {
							console.log("kickOut user success");
						});
					},
					error: function (xhr, textStatus, errorThrown) {
						$("#userError").show();
					}
				});
			}, function() {
				console.log("cancel");
			});
		});

	}

	function getMethod(operation) {

		if(operation == "停用") {
			return "delete";
		} else {
			return "PUT";
		}
	}
	return {
		initialize : _init
	}
});