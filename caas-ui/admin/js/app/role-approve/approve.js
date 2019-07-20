define([ "jquery", "jquery.utility", "jquery.searchable.list", "jquery.prompt"], function($) {
	function _init() {
		$("#sp-table").searchable({
            pageSize : 10
        });

        $("input[name='all']").on("change", function() {
        	if($(this).is(":checked")) {
        		$("input[name='roleCode']").prop("checked", true);
        	} else {
        		$("input[name='roleCode']").prop("checked", false);
        	}
        });

        $("input[name='roleCode']").on("change", function() {
        	if(!$(this).is(":checked")) {
        		$("input[name='all']").prop("checked", false);
        	}
        });

        $("#approve").on("click", function() {
        	if($("input[name='roleCode']:checked").size() > 0) {
        		var data = [];
                $.each($("input[name='roleCode']:checked"), function(i, value) {
                    data.push({
                        userCode : $(value).attr("data-userCode"),
                        roleCode : $(value).val()
                    });
                });
        		$.ajax({
					url : "/admin/app/role/approve",
					type : "POST",
					contentType : "application/json",
					data : JSON.stringify(data),
					success : function(result, textStatus, xhr) {
						$.info("审批成功", function() {
							Backbone.history.loadUrl(Backbone.history.fragment);
						});
					},
					error : function(xhr, textStatus, errorThrown) {
					}
				});
        	}
        });
	}

	return {
		initialize : _init
	}
});