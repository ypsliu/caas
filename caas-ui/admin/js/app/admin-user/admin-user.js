define([ "jquery", "jquery.utility", "jquery.colorbox"], function($) {
	function _init() {
		$(":button[name='onOff']").on("click", function() {
			var method = getMethod($(this).attr("value"));

			$.ajax({
				url : "/admin/"+$(this).attr("code")+"/enabled",
				type : method,
				contentType : "application/json",
				success : function(data, textStatus, xhr) {
					Backbone.history.loadUrl(Backbone.history.fragment);
				},
				error : function(xhr, textStatus, errorThrown) {
					if(xhr.status === 401) {
					}
				}
			});

		});

		$("a[role='colorbox']").colorbox({
            iframe : true, 
            width : "70%", 
            height : "90%",
            opacity : 0.5,
            overlayClose : false
        });

		AJS.InlineDialog(AJS.$("#add-user"), "add-user",
            function(content, trigger, showPopup) {
                content.css({"width" : "165px", "overflow" : "auto"}).html($("#add-user-buttons").html());
                showPopup();
                $("a[role='add-ipa-user']").colorbox({
                    iframe : true, 
                    width : "70%", 
                    height : "90%",
                    opacity : 0.5,
                    overlayClose : false
                });
                $("a[role='create-user']").on("click", function() {
                    $("#inline-dialog-add-user").remove();
                });
                return false;
            }
        );
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