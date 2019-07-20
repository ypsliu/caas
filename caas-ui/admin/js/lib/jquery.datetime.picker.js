(function (factory) {
    if (typeof define === "function" && define.amd) {
        // AMD
        define("jquery.datetime.picker", ["jquery"], factory);
    } else if (typeof exports === "object") {
        // CommonJS
        factory(require("jquery"));
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    $.fn.datetime = function() {
        var datetimeInput = $(this);
        var picker = $('<ul class="datetime-picker">' + 
            '<li>' + 
                '<input class="text" type="text" role="trigger-change" style="width:100px;" />' + 
            '</li>' + 
            '<li>' + 
                '<a href="javascript:void(0)" aria-owns="hour" aria-haspopup="true" class="aui-button aui-dropdown2-trigger aui-style-default">00</a></p>' + 
                '<div id="hour" class="aui-dropdown2 aui-style-default">' + 
                    '<ul class="aui-list-truncate" data-input="hour">' + 
                    '</ul>' + 
                '</div>' + 
                '<input type="hidden" name="hour" value="00" role="trigger-change" />' + 
            '</li>' + 
            '<li>' + 
                '<a href="javascript:void(0)" aria-owns="minute" aria-haspopup="true" class="aui-button aui-dropdown2-trigger aui-style-default">00</a></p>' + 
                '<div id="minute" class="aui-dropdown2 aui-style-default">' + 
                    '<ul class="aui-list-truncate" data-input="minute">' + 
                    '</ul>' + 
                '</div>' + 
                '<input type="hidden" name="minute" value="00" role="trigger-change" />' + 
            '</li>' + 
            '<li>' + 
                '<a href="javascript:void(0)" aria-owns="second" aria-haspopup="true" class="aui-button aui-dropdown2-trigger aui-style-default">00</a></p>' + 
                '<div id="second" class="aui-dropdown2 aui-style-default">' + 
                    '<ul class="aui-list-truncate" data-input="second">' + 
                    '</ul>' + 
                '</div>' + 
                '<input type="hidden" name="second" value="00" role="trigger-change" />' + 
            '</li>' + 
        '</ul>');
                
        var dateInput = picker.find("input[type='text']");
        var id = "date" + new Date().getTime();
        dateInput.attr("id", id);
        var dateTimeHidden = $("<input type='hidden'></input>");
        dateTimeHidden.attr("name", datetimeInput.attr("name"));
        dateInput.after(dateTimeHidden);

        _initDropdown(picker.children("li").eq(1), 24);
        _initDropdown(picker.children("li").eq(2), 60);
        _initDropdown(picker.children("li").eq(3), 60);

        function _initDropdown(context, len) {
            var ul = context.find("ul");
            var id = "picker" + new Date().getTime();
            var name = context.find("a[aria-owns]").attr("aria-owns") + id;
            context.find("a[aria-owns]").attr("aria-owns", name);
            context.find("input[name]").attr("name", name);
            context.find("div").attr("id", name);
            ul.attr("data-input", name);
            context.find("a").attr("id", id);
            ul.attr("target-id", id);
            ul.css("max-height", "150px").css("overflow-y", "scroll");
            for(var i = 0;i < len;i ++) {
                var value = String(i);
                if(value.length === 1) {
                    value = "0" + value;
                }
                var option = $('<li><a href="javascript:void(0)"></a></li>');
                option.find("a").text(value);
                option.attr("data-value", value);
                ul.append(option);
            }
            ul.find("li").on("click", function() {
                var parent = $(this).closest("ul");
                var name = parent.attr("data-input");
                var id = parent.attr("target-id");
                var value = $(this).attr("data-value");
                $("#" + id).siblings("input[name='" + name + "']").val(value).change();
                $("#" + id).text(value);
            });
        }

        picker.find("input[role='trigger-change']").on("change", function() {
            if(dateInput.val().length > 0) {
                dateTimeHidden.val(dateInput.val() + " " 
                    + picker.find("input[name*='hour']").val() + ":" 
                    + picker.find("input[name*='minute']").val() + ":"
                    + picker.find("input[name*='second']").val());
            } else {
                dateTimeHidden.val("");
            }
        });

        datetimeInput.after(picker);
        datetimeInput.remove();

        var defaultTime = datetimeInput.val();
        if(defaultTime.length > 0) {
            var dateTimeParts = defaultTime.split(" ");
            dateInput.val(dateTimeParts[0]);
            var timeParts = dateTimeParts[1].split(":");
            picker.find("input[name*='hour']").val(timeParts[0]);
            picker.find("input[name*='minute']").val(timeParts[1]);
            picker.find("input[name*='second']").val(timeParts[2]);
            picker.find("input[name*='hour']").siblings("a").text(timeParts[0]);
            picker.find("input[name*='minute']").siblings("a").text(timeParts[1]);
            picker.find("input[name*='second']").siblings("a").text(timeParts[2]);
        }

        AJS.$("#" + id).datePicker({
            overrideBrowserDefault: true,
            languageCode: "zh-CN",
            onSelect : function() {
                console.log(1);
            }
        });

        function _fill() {
            if(dateInput.val().length > 0) {
                dateTimeHidden.val(dateInput.val() + " " 
                    + picker.find("input[name*='hour']").val() + ":" 
                    + picker.find("input[name*='minute']").val() + ":"
                    + picker.find("input[name*='second']").val());
            } else {
                dateTimeHidden.val("");
            }
        }

        return {
            fill : _fill
        }
    }
}));