define(["app/constants", "jquery"], function(constants, $) {

    $.ajaxSetup({
        beforeSend: function(jqXHR, settings) {
            settings.url = constants.API_URL_PREFIX + settings.url;
        },
        error: function (xhr, textStatus, errorThrown) {
            
        }
    });

    var Router = Backbone.Router.extend({
        routes: {
            "admin/app/:appCode/user/:userCode/roles" : "/js/app/popup/user-roles/user-roles-controller.js",
            "admin/app/:appCode/ipa/users" : "/js/app/popup/ipa-users/ipa-users-controller.js",
            "superadmin/ipa/admin/users" : "/js/app/popup/ipa-users/ipa-users-controller.js",
            "superadmin/admin/:adminCode/app" : "/js/app/popup/app-select/app-select-controller.js"
        }
    });
    var router = new Router();
    var currentView;
    router.on("route", function(route, params) {
        require([route], function(controller) {
            if(router.currentController && router.currentController !== controller){
                router.currentController.onRouteChange && router.currentController.onRouteChange();
            }
            if (currentView) {
                if (currentView['dispose']) {
                    currentView.dispose();
                } else {
                    currentView.remove();
                }
            }

            $("body").append("<div id='content'></div>");

            if (controller) {
                currentView = controller.apply(null, params);
            }
        });
    });

    return router;
});
