/**
 * Created by Administrator on 2016-9-7.
 */
define(['app/user-managment/user-list-model','app/components/admin-user'], function(Model,adminUser) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/app/{appCode}/user",
        initialize: function() {
            var self=this;
            adminUser.user(function(user) {
                self.url="/admin/app/"+user.appCode+"/user";
            })
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});