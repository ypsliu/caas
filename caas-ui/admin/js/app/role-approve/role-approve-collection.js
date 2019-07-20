define(["app/role-approve/role-approve-model", "app/components/admin-user"], function(Model, adminUser) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/app/{appCode}/user/pending",
        initialize: function() {
            var self = this;
        	adminUser.user(function(user) {
        		self.url = self.url.replace(new RegExp("\\{appCode\\}"), user.appCode)
        	});
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});