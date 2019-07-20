define(["text!app/user/user-tpl.html","app/user/user"], function(tpl,user) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.model.off("sync");
            this.model.on("sync", this.render, this);
            this.model.fetch({
                data : JSON.stringify(this.model.get("condition")),
                type : "POST",
                contentType: "application/json"
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
            
                window.location.href = self.buildPath(condition);
            });
        },
        buildPath : function(condition) {
            var path = "#superadmin/user";
            for(var key in condition) {
                if(condition[key].length > 0) {
                    path = path + "/" + key + "/" + condition[key];
                }
            }
            return path;
        },
        render: function (event) {
            this.$el.html(this.template({
                result : this.model.toJSON()
            }));
            user.initialize();
            this.onSearch();
            this.onPage();
            return this;
        }
    });
});