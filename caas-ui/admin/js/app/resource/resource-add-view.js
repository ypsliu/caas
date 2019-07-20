/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["text!app/resource/resource-add-tpl.html","app/constants", "app/components/admin-user"], function(tpl,constants,adminUser) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
           var self=this;

            require(["app/resource/resource-add-ops"], function(ops) {
                ops.initialize();
            });
            adminUser.user(function(user) {
                self.$el.html(self.template({
                    resource : {
                        appName : user.appName,
                        appCode : user.appCode
                    }
                }));
            })
            return self;
    }
})});