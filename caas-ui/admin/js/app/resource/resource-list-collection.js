/**
 * Created by Administrator on 2016-9-7.
 */
define(['app/resource/resource-list-model',"app/components/admin-user"], function(ResourceListModel,adminUser) {
    return Backbone.Collection.extend({
        model: ResourceListModel,
        url: "/admin/app/1/resource",
        initialize: function() {
            var self=this;
            adminUser.user(function(user) {
                self.url="/admin/app/"+user.appCode+"/resource";
            })
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});