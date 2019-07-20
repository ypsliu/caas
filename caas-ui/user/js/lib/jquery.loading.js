(function (factory) {
    if (typeof define === "function" && define.amd) {
        // AMD
        define("jquery.loading", ["jquery"], factory);
    } else if (typeof exports === "object") {
        // CommonJS
        factory(require("jquery"));
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {
    $.loading = {
        start : function() {
            var overlay = $('<div class="overlay" role="loading-overlay"><div class="loading"><img src="/images/loading-big.gif" /></div></div>');
            $("body").append(overlay);
            var loading = overlay.find("div.loading");
            
            function _locate() {
                loading.css("left", $(window).width() / 2 - 57);
                loading.css("top", $(window).height() / 2 - 42);
            }

            _locate();
            $(window).on("resize.loading", function() {
                _locate();
            });
        },
        stop : function() {
            $(window).off("resize.loading");
            $("div[role='loading-overlay']").remove();
        }
    }
}));