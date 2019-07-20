
/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["text!app/user-app/user-add-tpl.html","app/constants", "app/components/admin-user"], function(tpl,contant,adminUser) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self=this;

            require(["app/user-app/user-add-ops"], function(ops) {
                ops.initialize();
            });
            adminUser.user(function(user) {
                self.$el.html(self.template({
                       user : {
                        appName : user.appName,
                        appCode : user.appCode
                    }
                }));
            })
            return self;
    }
})});
