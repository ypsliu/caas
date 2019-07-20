(function (factory) {
    if (typeof define === "function" && define.amd) {
        // AMD
        define("jquery.utility", ["jquery"], factory);
    } else if (typeof exports === "object") {
        // CommonJS
        factory(require("jquery"));
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    $.fn.serializeObject = function() {
        var o = {};
        var a = this.serializeArray();
        $.each(a, function() {
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [o[this.name]];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
    };

    $.uuid = function() {
        return "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx".replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    }

    $.dateUtil = {
        toDate : function(input) {
            var parts = input.split("-");
            if(parts.length === 3) {
                return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
            }
        },
        formatDate : function(date) {
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate();
        },
        formatDateTime : function(date) {
            function _fill(field) {
                if(String(field).length == 1) {
                    return "0" + field;
                } else {
                    return field;
                }
            }
            return date.getFullYear() + "-" + (date.getMonth() + 1) + "-" + date.getDate() 
                + " " + _fill(date.getHours()) + ":" + _fill(date.getMinutes()) + ":" + _fill(date.getSeconds());
        }
    }
}));