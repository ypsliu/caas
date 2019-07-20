define(["text!app/admin-user-input/admin-user-input-tpl.html","app/admin-user-input/admin-user-input"], function(tpl,adminUserInput) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            if(this.model.get("code")) {
                this.model.off("sync");
                this.model.on("sync", this.render, this);
                this.model.fetch();
            } else {
                this.render();
            }
        },
        initAppDropdown : function() {
            var self = this;
            $.ajax({
                url : "/admin/app",
                type: "get",
                success : function(apps, textStatus, xhr) {
                    self.renderDropdown(apps, $("#appCode"), $("a[aria-owns='appCode']"), $("input[name='appCode']"));
                },
                error : function(xhr, textStatus, errorThrown) {
                }
            });
        },

          renderDropdown : function(data, context, label, input) {

            context.find("ul").html("");
            var option = $();
            context.find("ul").append(option);
            for(var i = 0;i < data.length;i ++) {

                var item = data[i];

                if(item.appcode == $("input[name='appCode']").val()) {
                    label.text(item.find("a").text());
                }

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
            if(this.model.get("code")) {
                this.$el.html(this.template({
                    adminUser : this.model.toJSON()
                }));
            } else {
                this.$el.html(this.template({
                    adminUser : {}
                }));
            }
            this.initAppDropdown();
            adminUserInput.initialize();
            return this;
        }
    });
});