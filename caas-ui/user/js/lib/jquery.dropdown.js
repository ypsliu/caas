(function (factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define("jquery.dropdown", ["jquery"], factory);
	} else if (typeof exports === "object") {
		// CommonJS
		factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
    $.fn.dropdown = function() {
        var set = $(this);
        $.each(set, function(i, value) {
            var _obj = $(value);
            var _button = _obj.find("button");
            var _input = _obj.find("input");
            $("body").on("click.dropdown", function() {
                _obj.removeClass("open");
            });
            _button.off("click.dropdown");
            _button.on("click.dropdown", function(event) {
                event.stopPropagation();
                var open = _obj.hasClass("open");
                $("div.btn-group").removeClass("open");
                if(open) {
                    _obj.removeClass("open");
                } else {
                    _obj.addClass("open");
                }
            });
            $(this).find("ul li").on("click", function() {
                _input.val($(this).find("a").attr("data-value")).trigger("change");
                _button.find("span:first-child").text($(this).find("a").text());
                _obj.removeClass("open");
            });
        });
    }
}));