(function (factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define("jquery.validation", ["jquery"], factory);
	} else if (typeof exports === "object") {
		// CommonJS
		factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
    $.fn.isFalse = function() {
        if ($(this).val() != 0) {
            return false;
        }
        return true;
    }

    $.fn.isTrue = function() {
        if ($(this).val() == 0) {
            return false;
        }
        return true;
    }

    $.fn.isLessThanOrEqualTo = function(compareValue) {
        var fieldValue = $(this).val();
        if (fieldValue == "") {
            return true;
        }
        if (isNaN(fieldValue)) {
            return false;
        }
        if (fieldValue > compareValue) {
            return false;
        }
        return true;
    }

    $.fn.isGreaterThanOrEqualTo = function(compareValue) {
        var fieldValue = $(this).val();
        if (fieldValue == "") {
            return true;
        }
        if (isNaN(fieldValue)) {
            return false;
        }
        if (fieldValue < compareValue) {
            return false;
        }
        return true;
    }

    $.fn.isNumericValueInBounds = function(maxIntegerLength, maxFractionLength) {
        var fieldValue = $(this).val();
        fieldValue = fieldValue.replace(new RegExp("^\\s*|\\s*$", "gm"), "");
        if (fieldValue == "") {
            return true;
        }
        if (isNaN(fieldValue)) {
            return false;
        }

        integerLength = fieldValue.substring(0, fieldValue.indexOf(".")).length;
        fractionLength = fieldValue.substring(fieldValue.indexOf(".") + 1).length;
        if (integerLength > maxIntegerLength || fractionLength > maxFractionLength) {
            return false;
        }
        return true;
    }

    $.fn.isMatchPattern = function(regexp) {
        if (!$(this).val().match(new RegExp(regexp))) {
            return false;
        }
        return true;
    }

    $.fn.isObjectSizeBetween = function(minSize, maxSize) {
        var selAttr = "checked";
        var fields = $(this);
        if ($(this).prop("tagName").toLowerCase() === "select") {
            fields = $(this).find("option");
            selAttr = "selected";
        }
        if (fields.size() > 1) {
            var size = 0;
            for(var i = 0;i < fields.size();i ++) {
                if (selAttr === "checked" && fields.eq(i).is(":checked")) {
                    size++;
                } else if(selAttr === "selected" && fields.eq(i).is(":selected")) {
                    size++;
                }
            }
            if (size < minSize || size > maxSize) {
                return false;
            }
        } else {
            var fieldValue = fields.val();
            if (fieldValue.length < minSize || fieldValue.length > maxSize) {
                return false;
            }
        }
        return true;
    }

    $.fn.isURL = function() {
        if (!$(this).val().match("^[a-zA-Z]+://[^\\s]*$")) {
            return false;
        }
        return true;
    }

    $.fn.isLengthBetween = function(minLength, maxLength) {
        var fieldValue = $(this).val();
        if (fieldValue.length < minLength || fieldValue.length > maxLength) {
            return false;
        }
        return true;
    }

    $.fn.isNotBlank = function() {
        var fieldValue = $(this).val();
        fieldValue = fieldValue.replace(new RegExp("^\\s*|\\s*$", "gm"), "");
        if (fieldValue == "") {
            return false;
        }
        return true;
    }

	$.fn.isEmail = function() {
        var isEmail = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@([a-z0-9!#$%&'*+/=?^_`{|}~-]+(\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])$/i.test($(this).val());
        if (!isEmail) {
            return false;
        }
        return true;
    }
}));