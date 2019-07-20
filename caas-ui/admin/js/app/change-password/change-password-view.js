define(["text!app/change-password/change-password-tpl.html",
    "app/components/admin-user",
    "app/change-password/change-password"], function(tpl, adminUser, changePassword) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self = this;
            adminUser.user(function(user) {
                self.$el.html(self.template({
                    user : user
                }));
                changePassword.initialize();
            });
            return self;
        }
    });
});