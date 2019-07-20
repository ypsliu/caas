(function (factory) {
    if (typeof define === "function" && define.amd) {
        // AMD
        define("jquery.prompt", ["jquery"], factory);
    } else if (typeof exports === "object") {
        // CommonJS
        factory(require("jquery"));
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    $.info = function(message, callback) {
        var id = "info" + new Date().getTime();
        var dialog = new AJS.Dialog({
            width: 400, 
            height: 200, 
            id: id, 
            closeOnOutsideClick: false
        });
        dialog.addHeader("信息", "aui-lozenge-current");
        dialog.addPanel("", "<p>" + message + "</p>", "");

        dialog.addButton(
            "知道了",
            function() {
                callback();
                dialog.remove();
            },
            "aui-button aui-button-primary"
        );
        dialog.show();
        $("#" + id).find("button").removeClass("button-panel-button");
    }

    $.alert = function(message, callback) {
        var id = "alert" + new Date().getTime();
        var dialog = new AJS.Dialog({
            width: 400, 
            height: 200, 
            id: id, 
            closeOnOutsideClick: false
        });
        dialog.addHeader("警告", "aui-lozenge-current");
        dialog.addPanel("", "<p>" + message + "</p>", "");

        dialog.addButton(
            "确定",
            function() {
                callback();
                dialog.remove();
            },
            "aui-button aui-button-primary"
        );
        dialog.show();
        $("#" + id).find("button").removeClass("button-panel-button");
    }

    $.confirm = function(message, confirm, cancel) {
        var id = "confirm" + new Date().getTime();
        var dialog = new AJS.Dialog({
            width: 400, 
            height: 200, 
            id: id, 
            closeOnOutsideClick: false
        });
        dialog.addHeader("提示", "aui-lozenge-current");
        dialog.addPanel("", "<p>" + message + "</p>", "");

        dialog.addButton(
            "确定",
            function() {
                confirm();
                dialog.remove();
            },
            "aui-button aui-button-primary"
        );
        dialog.addButton(
            "取消",
            function() {
                cancel();
                dialog.remove();
            },
            "aui-button"
        );
        dialog.show();
        $("#" + id).find("button").removeClass("button-panel-button");
    }
}));