/**
 * Created by Administrator on 2016-9-7.
 */
define(['app/operation/operation-list-model','app/components/admin-user'], function(Model,adminUser) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/app/1/operation",
        initialize: function() {
            var self=this;
            adminUser.user(function(user) {
                self.url="/admin/app/"+user.appCode+"/operation";
            })
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});
