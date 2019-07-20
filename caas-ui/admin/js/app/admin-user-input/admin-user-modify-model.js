define(["jquery"], function() {
    return Backbone.Model.extend({
    	url : "/admin",
        initialize: function () {
        	this.url = this.url + "/" + this.get("code")
        }
    });
});
