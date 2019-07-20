(function (factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define("jquery.verifiable", ["jquery"], factory);
	} else if (typeof exports === "object") {
		// CommonJS
		factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
    $.fn.verifiable = function(url) {
        var input = $(this);
        var container = input.closest("div.field-group");
        input.off("change");
        input.on("change", function() {
            if(container.validate() && input.val().length > 0) {
                container.find("span.aui-icon").hide();
                if(input.attr("ignore-value") && input.attr("ignore-value") == input.val()) {
                    container.find("div.invalid[verify='true']").hide();
                    container.find("span.aui-icon-wait").hide();
                    container.find("span.aui-iconfont-success").show();
                    container.find("span.aui-iconfont-warning").hide();
                } else {
                    container.find("span.aui-icon-wait").show();
                    $.ajax({
                        url : url + input.val(),
                        success : function(result, textStatus, xhr) {
                            container.find("span.aui-icon-wait").hide();
                            container.find("div.invalid").hide();
                            if(result.success) {
                                container.find("span.aui-iconfont-success").show();
                                container.find("span.aui-iconfont-warning").hide();
                            } else {
                                container.find("span.aui-iconfont-success").hide();
                                container.find("span.aui-iconfont-warning").show();
                                container.find("div.invalid[verify='true']").show();
                            }
                        },
                        error : function(xhr, textStatus, errorThrown) {
                            container.find("span.aui-icon-wait").hide();
                        }
                    });
                }
            } else if(input.val().length === 0) {
                container.find("div.invalid[verify='true']").hide();
            }
        });
    }
}));