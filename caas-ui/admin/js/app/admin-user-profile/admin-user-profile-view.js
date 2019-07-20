define(["text!app/admin-user-profile/admin-user-profile-tpl.html",
    "app/components/admin-user", "app/components/nav/nav-view", 
    "app/admin-user-profile/profile"], function(tpl, adminUser, navView, profile) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self = this;
            adminUser.clear();
            navView.refresh(function() {
                adminUser.user(function(user) {
                    self.$el.html(self.template({
                        user : user,
                        app : user.app
                    }));
                    profile.initialize(user.app);
                });
            });
            return self;
        }
    });
});