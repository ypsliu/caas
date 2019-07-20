define(["app/router", "app/components/nav/nav-view"], function(router, navView) {
    navView.refresh(function() {
    	Backbone.history.start();
    });
});