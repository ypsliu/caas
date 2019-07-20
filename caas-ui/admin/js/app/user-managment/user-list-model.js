/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["jquery",'app/components/admin-user'], function($,adminUser) {
    return Backbone.Model.extend({
        url: "/admin/app/{appCode}/user",
        initialize: function() {
            var self=this;
            adminUser.user(function(user) {
                self.url="/admin/app/"+user.appCode+"/user";
            })
        }
    });
});