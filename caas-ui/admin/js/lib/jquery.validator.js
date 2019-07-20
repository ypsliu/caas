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
            var ruleObjs = $(group).find(".invalid[validation]");
            $.each(ruleObjs, function(j, ruleObj) {
                var rule = $(ruleObj).attr("validation");
                var parts = rule.split(", ");
                var method = parts[0];
                var params = [];
                if(parts.length > 1) {
                    params = parts.slice(1);
                }
                var targetInput = input;
                if($(ruleObj).attr("validation-for")) {
                    targetInput = $(group).find("input[name='" + $(ruleObj).attr("for") + "']");
                }
                if(!$(ruleObj).attr("validation-ignore-blank") || targetInput.val().length > 0) {
                    if(!targetInput[method].apply(targetInput, params)) {
                        valid = false;
                        $(ruleObj).show();
                        return false;
                    }
                }
            });
        }

        if($(this)[0].tagName.toLowerCase() === "form") {
            var form  = $(this);
            form.find("div.field-group div.invalid:not([verify])").hide();
            $.each(form.find("div.field-group:visible"), function(i, group) {
                _validateGroup(group);
            });
        } else if($(this)[0].tagName.toLowerCase() === "div" && $(this).hasClass("field-group")) {
            $(this).find("div.invalid:not([verify])").hide();
            _validateGroup($(this));
        }
        return valid;
    }
}));