define([ "jquery", "jquery.utility" ,"jquery.prompt"], function($) {
	function _init() {
        $("#appQueryError").hide();
        revomeApp();
        secretShow();
        dropdown($("div[role='dropdown']"));
        approval();
    };

    function dropdown(context) {
        context.find("ul li").on("click", function() {
            context.find("a[aria-owns]").text($(this).find("a").text());
            context.find("input").val($(this).attr("data-value"));
        });
    }

    function approval() {
        $("a[role='approve']").on("click", function() {
            var self = $(this);
            $.ajax({
                url : "/admin/app/" + self.attr("data-appCode") + "/approve",
                type : "POST",
                contentType : "application/json",
                success : function(result, textStatus, xhr) {
                    $.info("审批成功。", function() {
                        Backbone.history.loadUrl(Backbone.history.fragment);
                    });
                },
                error : function(xhr, textStatus, errorThrown) {
                }
            });
        });
    }

    function queryApp(){
          $("#query-app").on("click", function() {
			var form = $("form[name='app-query']");
			$.ajax({
				url : '/admin/app',
				type : 'get',
				contentType : "application/json",
				data : '',
				success : function(data, textStatus, xhr) {
					window.userRole = undefined;
					require([ "app/components/nav/nav-view" ], function(navView) {
						navView.refresh(function() {
							var currentUrl = window.location.href;
							if(currentUrl.match(new RegExp("#home$")) || currentUrl.match(new RegExp("#$"))) {
								Backbone.history.loadUrl(Backbone.history.fragment);
							} else {
								window.location.href = "#";
							}
						});
					});
				},
				error : function(xhr, textStatus, errorThrown) {
                    $("#appQueryError").show();
				}
			});
		});
}

	/**
     * delete button
     * */
	function revomeApp() {
	
        $("#revomeApp").on("click", function() {
           var selectedNo= $("input[id^='app']:checked").length;
            if(selectedNo<=0){
                $.alert("请先选择删除内容",function(){
                    console.log("pls choose one line first.");
                });
                return false;
            }
            $.confirm("确认删除?", function() {
                $("input[id^='app']:checked").each(function(){
                    var id=$(this).attr("id");
                    var code=id.split("_")[1];
                    $.ajax({
                        url : "/admin/app/"+code,
                        type : "delete",
                        contentType : "application/json",
                        success : function(data, textStatus, xhr) {
                            $("#row_"+code).remove();

                        },
                        error : function(xhr, textStatus, errorThrown) {
                            $("#appQueryError").show();
                        }
                    });

                })
            }, function() {
                console.log("cancel");
            });

    	})
    }

    function secretShow(){
        $("span[name='secretHide']").on('click',function(){
            $(this).toggle();
            $(this).next().toggle();

        });
        $("span[name='secretShow']").on('click',function(){
            $(this).toggle();
            $(this).prev().toggle();
        });
    }

	return {
		initialize : _init
	}
});







