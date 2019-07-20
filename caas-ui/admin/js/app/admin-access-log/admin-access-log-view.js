define(["text!app/admin-access-log/admin-access-log-tpl.html", 
    "jquery", "jquery.utility", "jquery.datetime.picker", "jquery.jsonview"], function(tpl, $) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
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
        render: function (event) {
            this.$el.html(this.template({
                result : this.model.toJSON()
            }));

            this.initForm(this.model.get("condition"));
            this.initDatePicker();

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
                window.location.href = self.buildPath(self.model.get("condition"));
            });
        },
        buildPath : function(condition) {
            var path = "#superadmin/admin-access-log";
            for(var key in condition) {
                if(condition[key].length > 0) {
                    path = path + "/" + key + "/" + condition[key];
                }
            }
            return path;
        }
    });
});