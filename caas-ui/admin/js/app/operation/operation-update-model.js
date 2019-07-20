/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["jquery"], function() {
    return Backbone.Model.extend({
        url : "/admin/operation",
        initialize: function () {
            this.url = "/admin/operation/" + this.get("code")
        }
    });
});
