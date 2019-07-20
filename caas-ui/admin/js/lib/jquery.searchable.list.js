(function (factory) {
    if (typeof define === "function" && define.amd) {
        // AMD
        define("jquery.searchable.list", ["jquery"], factory);
    } else if (typeof exports === "object") {
        // CommonJS
        factory(require("jquery"));
    } else {
        // Browser globals
        factory(jQuery);
    }
}(function ($) {

    $.fn.searchable = function(options) {
        var defaultOptions = {
            condition : {},
            pageSize : 20
        };
        $.each($(this), function(i, value) {
            AJS.tablessortable.setTableSortable(AJS.$(value).find("table"));

            if(options) {
                options = $.extend(true, {}, defaultOptions, options);
            } else {
                options = defaultOptions;
            }

            var searchBtn = $(value).find("[role='search']");
            var table = $(value).find("table");
            var info = $(value).find("div.pagination-info");
            var pagination = $(value).find("div.pagination ul");

            _pagination();

            searchBtn.on("click", function() {
                _search();
                _pagination();
            });

            function _search() {
                table.find("tbody tr").show();
                table.find("tbody tr").removeClass("hide");
                var conditions = $(value).find("form input[role='condition']");
                $.each(conditions, function(j, condition) {
                    var conditionVal = $(condition).val();
                    if(conditionVal.length > 0) {
                        var tr = table.find("tbody tr td[condition='" + $(condition).attr("name") + "']:not(:contains('" + conditionVal + "'))").parent();
                        tr.hide();
                        tr.addClass("hide");
                    }
                });
            }

            function _pagination() {
                var total = table.find("tbody tr:not(.hide)").size();
                pagination.html("");
                info.find("span:first-child").html("");
                info.find("span:last-child").html(total);
                if(total === 0) {
                    info.hide();
                    return;
                } else {
                    info.show();
                }

                var pageSize = options.pageSize;
                var count = Math.ceil(total / pageSize);
                
                var prev = $('<li class="prev"><a href="javascript:void(0)">上一页</a></li>');
                prev.hide();
                pagination.append(prev);
                pagination.data("current", 0);
                for(var i = 0;i < count;i ++) {
                    var num = $('<li><a href="javascript:void(0)"></a></li>');
                    num.find("a").text(i + 1);
                    pagination.append(num);
                }
                var next = $('<li class="next"><a href="javascript:void(0)">下一页</a></li>');
                pagination.append(next);
                

                pagination.find("li").click(function() {
                    if(!$(this).hasClass("inactive")) {
                        var current = pagination.data("current");
                        var number;
                        if($(this).hasClass("prev")) {
                            number = current - 1;
                        } else if($(this).hasClass("next")) {
                            number = current + 1;
                        } else {
                            number = Number($(this).find("a").text()) - 1;
                        }
                        pagination.find("li").removeClass("inactive");
                        pagination.find("li").eq(number + 1).addClass("inactive");
                        if(number === 0) {
                            pagination.find("li.prev").hide();
                        } else {
                            pagination.find("li.prev").show();
                        }
                        if(number === pagination.find("li").size() - 3) {
                            pagination.find("li.next").hide();
                        } else {
                            pagination.find("li.next").show();
                        }
                        var from = number * pageSize;
                        var to = number * pageSize + pageSize - 1;
                        if(to > total - 1) {
                            to = total - 1;
                        }
                        $.each(table.find("tbody tr:not(.hide)"), function(j, column) {
                            if(j >= from && j <= to) {
                                $(column).show();
                            } else {
                                $(column).hide();
                            }
                        });
                        pagination.data("current", number);
                        info.find("span:first-child").html((from + 1) + "-" + (to + 1));
                    }
                });
                pagination.find("li").eq(1).trigger("click");
                if(pagination.find("li").size() === 3) {
                    pagination.html("");
                }
                table.show();
            } 
        });
    };
}));