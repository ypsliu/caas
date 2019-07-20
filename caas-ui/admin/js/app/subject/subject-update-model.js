/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["jquery"], function() {
    return Backbone.Model.extend({
        url : "/admin/subject",
        initialize: function () {
            this.url = "/admin/subject/" + this.get("code")
        }
    });
});
