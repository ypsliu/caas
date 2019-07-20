define(["text!app/user-access-log/user-access-log-tpl.html", 
    "jquery", "jquery.utility", "jquery.datetime.picker", "jquery.jsonview"], function(tpl, $) {
    return Backbone.View.extend({
        el: "#page",
        template:        _.template(tpl),
        initialize: function (params) {
            _.formatDateTime = $.dateUtil.formatDateTime;
            this.model.off("sync");
            this.model.on("sync", this.render, this);
            this.model.fetch({
                data : JSON.stringify(this.model.get("condition")),
                type : "POST",
                contentType: "application/json"
            });
        },
        initForm : function(condition) {
            for(var key in condition) {
                if(key === "resource") {
                    condition[key] = decodeURIComponent(condition[key]);
                }
                $("form input[name='" + key + "']").val(condition[key]);
            }
        },
        initDatePicker : function() {
            this.fromTime = $("input[name='fromTime']").datetime();
            this.toTime = $("input[name='toTime']").datetime();
        },
        initAppDropdown : function() {
            var self = this;
            $.ajax({
                url : "/admin/app",
                success : function(apps, textStatus, xhr) {
                    self.renderDropdown(apps, $("#appCode"), $("a[aria-owns='appCode']"), $("input[name='appCode']"));
                },
                error : function(xhr, textStatus, errorThrown) {
                }
            });
        },
        renderDropdown : function(data, context, label, input) {
            context.find("ul").html("");
            var option = $('<li data-value=""><a href="javascript:void(0)">查询所有</a></li>');
            context.find("ul").append(option);
            for(var i = 0;i < data.length;i ++) {
                var item = data[i];
                var option = $('<li><a href="javascript:void(0)"></a></li>');
                option.find("a").text(item.name);
                option.attr("data-value", item.code);
                context.find("ul").append(option);
            }
            context.find("ul li").on("click", function() {
                label.text($(this).find("a").text());
                input.val($(this).attr("data-value"));
            });
            context.find("ul li[data-value='" + input.val() + "']").trigger("click");
        },
        render: function (event) {
            this.$el.html(this.template({
                result : this.model.toJSON()
            }));

            this.initForm(this.model.get("condition"));
            this.initDatePicker();
            this.initAppDropdown();

            this.bindDetail();
            this.onSearch();
            this.onPage();

            return this;
        },
        bindDetail : function() {
            $.each($("a[data-detail]"), function(i, value) {
                AJS.InlineDialog(AJS.$(value), 1,
                    function(content, trigger, showPopup) {
                        content.css({"padding":"20px"});
                        var data = $(value).attr("data-detail");
                        try {
                            data = JSON.parse(data);
                        } catch(e) {
                        }
                        if(typeof data === "object") {
                            $(content).JSONView($(value).attr("data-detail"));
                        } else {
                            $(content).html($(value).attr("data-detail"));
                        }
                        showPopup();
                        return false;
                    }
                );
            });
        },
        onSearch : function() {
            var self = this;
            $("#search").on("click", function() {
                self.fromTime.fill();
                self.toTime.fill();
                var data = $("form").serializeObject();
                for(var key in data) {
                    if(key === "resource") {
                        data[key] = encodeURIComponent(data[key]);
                    }
                    if($("form input[name='" + key + "']").attr("role") === "trigger-change") {
                        delete data[key];
                    }
                }
                window.location.href = self.buildPath(data);
            });
        },
        onPage : function() {
            var self = this;
            $("div.pagination ul li:not(.inactive)").on("click", function() {
                var condition = self.model.get("condition");
                condition.pageNo = $(this).attr("data");
                for(var key in condition) {
                    if(key === "resource") {
                        condition[key] = encodeURIComponent(condition[key]);
                    }
                    if($("form input[name='" + key + "']").attr("role") === "trigger-change") {
                        delete condition[key];
                    }
                }
                window.location.href = self.buildPath(condition);
            });
        },
        buildPath : function(condition) {
            var path = "#superadmin/user-access-log";
            for(var key in condition) {
                if(condition[key].length > 0) {
                    path = path + "/" + key + "/" + condition[key];
                }
            }
            return path;
        }
    });
});