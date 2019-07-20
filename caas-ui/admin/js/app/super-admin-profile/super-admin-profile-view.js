define(["text!app/super-admin-profile/super-admin-profile-tpl.html",
    "app/components/admin-user"], function(tpl, adminUser) {
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
            });
            return self;
        }
    });
});