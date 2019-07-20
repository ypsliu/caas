/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["text!app/resource/resource-update-tpl.html","app/constants", "app/components/admin-user"], function(tpl,constants,adminUser) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.model.off("sync");
            this.model.on("sync", this.render, this);
            this.model.fetch();
        },
        render: function (event) {
           var self=this;
            if(this.model.get("code")) {
                this.$el.html(this.template({
                    resource : this.model.toJSON()
                }));
            }
            require(["app/resource/resource-update-ops"], function(ops) {
                ops.initialize();
            });
          /*  adminUser.user(function(user) {
                self.$el.html(self.template({
                    resource : {
                        appName : user.appName,
                        appCode : user.appCode
                    }
                }));
            })*/
            return this;
    }
})});