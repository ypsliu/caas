define(["app/constants", "app/components/admin-user", "jquery", "jquery.browser"], function(constants, adminUser, $) {

    $.ajaxSetup({
        beforeSend: function(jqXHR, settings) {
            settings.url = constants.API_URL_PREFIX + settings.url;
            if($.browser.msie) {
                if(settings.url.indexOf("?") > 0) {
                    settings.url = settings.url + "&";
                } else {
                    settings.url = settings.url + "?";
                }
                settings.url = settings.url + "timestamp=" + new Date().getTime()
            }
        },
        error: function (xhr, textStatus, errorThrown) {
            _errorHandler(xhr);
        }
    });

    Backbone.Model.prototype.on("error", function(collection, response, options) {
        _errorHandler(response);
    });

    Backbone.Collection.prototype.on("error", function(collection, response, options) {
        _errorHandler(response);
    });

    function _errorHandler(response) {
        if(response.status === 401 || response.status === 403) {
            window.location.replace("#unauthorized");
        } else if(response.status === 500) {
            window.location.replace("#error");
        }
    }

    var Router = Backbone.Router.extend({
        routes: {
            "home" : "/js/app/home/home-controller.js",
            "login" : "/js/app/login/login-controller.js",
            "unauthorized" : "/js/app/unauthorized/unauthorized-controller.js",
            "error" : "/js/app/error/error-controller.js",
            "superadmin/admin-user" : "/js/app/admin-user/admin-user-controller.js",
            "superadmin/admin-user/input/:code" : "/js/app/admin-user-input/admin-user-input-controller.js",
            "superadmin/admin-user/add" : "/js/app/admin-user-input/admin-user-input-controller.js",
            "superadmin/app" : "/js/app/app/app-manage-controller.js",
            "superadmin/app/input" : "/js/app/app-input/app-input-controller.js",
            "superadmin/app/input/:code" : "/js/app/app-input/app-input-controller.js",
            "superadmin/user(/name/:name)(/email/:email)(/mobile/:mobile)(/status/:status)(/pageNo/:pageNo)(/pageSize/:pageSize)"
                : "/js/app/user/user-controller.js",
            "superadmin/profile" : "/js/app/super-admin-profile/super-admin-profile-controller.js",
            "superadmin/user-access-log(/userCode/:userCode)(/appCode/:appCode)(/resource/:resource)(/minTimeInMs/:minTimeInMs)(/maxTimeInMs/:maxTimeInMs)(/paramsKeyword/:paramsKeyword)(/resultKeyword/:resultKeyword)(/exceptionKeyword/:exceptionKeyword)(/fromTime/:fromTime)(/toTime/:toTime)(/pageNo/:pageNo)(/pageSize/:pageSize)"
                : "/js/app/user-access-log/user-access-log-controller.js",
            "superadmin/admin-access-log(/minTimeInMs/:minTimeInMs)(/maxTimeInMs/:maxTimeInMs)(/paramsKeyword/:paramsKeyword)(/resultKeyword/:resultKeyword)(/exceptionKeyword/:exceptionKeyword)(/fromTime/:fromTime)(/toTime/:toTime)(/pageNo/:pageNo)(/pageSize/:pageSize)"
                : "/js/app/admin-access-log/admin-access-log-controller.js",
            "superadmin/admin-access-log" : "/js/app/admin-access-log/admin-access-log-controller.js",
            "admin/resource/list":"/js/app/resource/resource-list-controller.js",
            "admin/resource/update/:code":"/js/app/resource/resource-update-controller.js",
            "admin/resource/add":"/js/app/resource/resource-add-controller.js",
            "admin/role/list":"/js/app/role/role-list-controller.js",
            "admin/role/update/:code":"/js/app/role/role-update-controller.js",
            "admin/role/add":"/js/app/role/role-add-controller.js",
            "admin/user-managment(/name/:name)(/email/:email)(/mobile/:mobile)(/pageNo/:pageNo)(/pageSize/:pageSize)":"/js/app/user-managment/user-list-controller.js",
            "admin/user-role/pending":"/js/app/role-approve/role-approve-controller.js",
            "admin/user/:code/role":"/js/app/user-role/user-role-list-controller.js",
            "admin/profile" : "/js/app/admin-user-profile/admin-user-profile-controller.js",
            "change-password" : "/js/app/change-password/change-password-controller.js",
            "change-password-success" : "/js/app/change-password-success/cpw-success-controller.js",
            "signup" : "/js/app/admin-signup/admin-signup-controller.js",
            "admin/app/apply/:userCode" : "/js/app/app-application/app-application-controller.js",
            "admin/subject/list":"/js/app/subject/subject-list-controller.js",
            "admin/subject/update/:code":"/js/app/subject/subject-update-controller.js",
            "admin/subject/add":"/js/app/subject/subject-add-controller.js",
            "admin/operation/list":"/js/app/operation/operation-list-controller.js",
            "admin/operation/update/:code":"/js/app/operation/operation-update-controller.js",
            "admin/operation/add":"/js/app/operation/operation-add-controller.js",
            "admin/user-import":"/js/app/user-import/user-import-controller.js",
            "admin/user/create":"/js/app/user-app/user-add-controller.js",
            "admin/user-access-log(/userCode/:userCode)(/appCode/:appCode)(/resource/:resource)(/minTimeInMs/:minTimeInMs)(/maxTimeInMs/:maxTimeInMs)(/paramsKeyword/:paramsKeyword)(/resultKeyword/:resultKeyword)(/exceptionKeyword/:exceptionKeyword)(/fromTime/:fromTime)(/toTime/:toTime)(/pageNo/:pageNo)(/pageSize/:pageSize)"
                : "/js/app/user-access-log-admin/user-access-log-controller.js",
              "admin/user-app":"/js/app/user-app/user-list-controller.js",
            "*actions": "/js/app/home/home-controller.js"
        }
    });
    var router = new Router();
    var currentView;
    router.on("route", function(route, params) {
         adminUser.user(function(user) {
            // filter
            var routeUrl = Backbone.history.getFragment();
            if(routeUrl.indexOf("login") === 0
                || routeUrl.indexOf("unauthorized") === 0
                || routeUrl.indexOf("error") === 0
                ||  routeUrl.indexOf("change-password-success") === 0
                ||  routeUrl.indexOf("signup") === 0) {
                $("div.aui-page-panel-inner").css("background-color", "#f5f5f5");
            } else {
                if((user.role !== constants.ROLE_SUPER_ADMIN && routeUrl.indexOf("superadmin") === 0)
                    || (user.role !== constants.ROLE_ADMIN_USER && routeUrl.indexOf("admin") === 0)
                    || (user.role === constants.ROLE_ANONYMOUS && routeUrl.indexOf("change-password") === 0)) {
                    route = "/js/app/unauthorized/unauthorized-controller.js";
                } else {
                    $("div.aui-page-panel-inner").css("background-color", "#ffffff");
                    if(routeUrl.indexOf("admin/app/apply") === 0) {
                        $("div.aui-page-panel-inner").css("background-color", "#f5f5f5");
                    }
                }
            }

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

                $("div.aui-page-panel-inner").append('<section class="aui-page-panel-content" id="page"></section>');

                if (controller) {
                    currentView = controller.apply(null, params);
                }
            });
        });
    });

    return router;
});
