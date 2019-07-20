(function (factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define("jquery.validator", ["jquery"], factory);
	} else if (typeof exports === "object") {
		// CommonJS
		factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
    $.fn.validate = function() {
        var valid = true;
        function _validateGroup(group) {
            var input = $(group).find("input");
            var ruleObjs = $(group).find(".error-msg[validation]");
            $.each(ruleObjs, function(j, ruleObj) {
                var rule = $(ruleObj).attr("validation");
                //rule = rule.replace(new RegExp("\\s", "gm"), "");
                var parts = rule.split(", ");
                var method = parts[0];
                var params = [];
                if(parts.length > 1) {
                    params = parts.slice(1);
                }
                var targetInput = input;
                if($(ruleObj).attr("for")) {
                    targetInput = $(group).find("input[name='" + $(ruleObj).attr("for") + "']");
                }
                if(!$(ruleObj).attr("ignore-blank") || targetInput.val().length > 0) {
                    if(!targetInput[method].apply(targetInput, params)) {
                        valid = false;
                        $(group).addClass("invalid");
                        $(group).find(".error-msg").hide();
                        $(ruleObj).show();
                        return false;
                    }
                }
            });
        }

        if($(this)[0].tagName.toLowerCase() === "form") {
            var form  = $(this);
            form.find("div.input-group").removeClass("invalid");
            form.find("div.input-group .error-msg").hide();
            $.each(form.find("div.input-group:visible"), function(i, group) {
                _validateGroup(group);
            });
        } else if($(this)[0].tagName.toLowerCase() === "div" && $(this).hasClass("input-group")) {
            var group = $(this);
            group.removeClass("invalid");
            group.removeClass("ok");
            group.removeClass("error");
            group.find(".error-msg").hide();
            _validateGroup(group);
        }
        return valid;
    }
}));