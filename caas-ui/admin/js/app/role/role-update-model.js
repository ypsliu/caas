/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["jquery"], function() {
    return Backbone.Model.extend({
        url : "/admin/role",
        initialize: function () {
            this.url = "/admin/role/" + this.get("code")
        }
    });
});