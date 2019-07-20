/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["jquery"], function() {
    return Backbone.Model.extend({
        url : "/admin/resource",
        initialize: function () {
            this.url = "/admin/resource/" + this.get("code")
        }
    });
});