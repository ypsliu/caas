/**
 * Created by Administrator on 2016-9-7.
 */
define(['app/role/role-list-model','app/components/admin-user'], function(Model,adminUser) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/app/1/role",
        initialize: function() {
            var self=this;
            adminUser.user(function(user) {
                self.url="/admin/app/"+user.appCode+"/role";
            })
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});