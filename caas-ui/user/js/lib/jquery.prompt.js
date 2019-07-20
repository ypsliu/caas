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
    $.info = function(content, label, callback) {
        var box = $('<div class="popup-box">' + 
                        '<span class="glyphicon glyphicon-info-sign"></span>' +
                        '<p></p>' +
                        '<div class="btn-area top20">' +
                            '<a class="btn btn-primary center" href="javascript:void(0)"></a>' +
                        '</div>' +
                     '</div>');
        create(box, content, "infoBox");
        var btn = box.find("div.btn-area a.btn");
        setButton(box, btn, label, callback);
    }

    $.alert = function(content, label, callback) {
        var box = $('<div class="popup-box">' + 
                        '<span class="glyphicon glyphicon-exclamation-sign"></span>' +
                        '<p></p>' +
                        '<div class="btn-area top20">' +
                            '<a class="btn btn-primary center" href="javascript:void(0)"></a>' +
                        '</div>' +
                     '</div>');
        create(box, content, "alertBox");
        var btn = box.find("div.btn-area a.btn");
        setButton(box, btn, label, callback);
    }

    $.confirm = function(content, cancelLabel, confirmLabel, cancel, confirm) {
        var box = $('<div class="popup-box" id="confirmBox">' +
                        '<span class="glyphicon glyphicon-question-sign"></span>' +
                        '<p></p>' +
                        '<div class="btn-area top20">' +
                            '<div><a class="btn btn-primary" href="javascript:void(0)"></a></div>' +
                            '<div><a class="btn btn-primary" href="javascript:void(0)"></a></div>' +
                        '</div>' +
                     '</div>');
        create(box, content, "confirmBox");
        var cancelBtn = box.find("div.btn-area a.btn").eq(0);
        var confirmBtn = box.find("div.btn-area a.btn").eq(1);
        setButton(box, cancelBtn, cancelLabel, cancel);
        setButton(box, confirmBtn, confirmLabel, confirm);
    }

    function setButton(box, button, label, callback) {
        button.html(label);
        button.on("click", function() {
            $.colorbox.close();
            button.closest("div.popup-box").hide();
            callback();
        });
    }

    function create(box, content, id) {
        $("#" + id).remove();
        box.attr("id", id);
        box.find("p").html(content);
        $("body").append(box);
        $.colorbox({
            inline : true,
            href : "#" + id,
            opacity : 0.35,
            overlayClose : false,
            closeButton : false,
            width: 440,
            height: 260,
            onClosed : function() {
                $("#" + id).remove();
                $.colorbox.remove();
            }
        });
    }
}));