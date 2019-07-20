define(["jquery"], function() {
    return Backbone.Model.extend({
    	url : "/admin/app",
        initialize: function () {
        	this.url = this.url + "/" + this.get("code")
        }
    });
});
