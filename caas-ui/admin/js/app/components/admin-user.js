define(["app/constants", "jquery"], function(constants, $) {

    function _me(callback) {
        if(!window.user) {
            $.ajax({
                url : "/admin/me",
                success: function(adminUser, textStatus, xhr) {
                    window.user = adminUser;
                    if(adminUser.superUser) {
                        window.user.role = constants.ROLE_SUPER_ADMIN;
                    } else {
                        window.user.role = constants.ROLE_ADMIN_USER;
                    }
                    if(window.user.appCode) {
                        _getApp(window.user.appCode, function(app) {
                            window.user.app = app;
                            callback();
                        });
                    } else {
                        callback();
                    }
                }, 
                error: function (xhr, textStatus, errorThrown) {
                    window.user = {
                        role : constants.ROLE_ANONYMOUS
                    };
                    callback();
                }
            });
        } else {
            callback();
        }
    }

    function _getApp(appCode, callback) {
        $.ajax({
            url : "/admin/app/" + appCode,
            success: function(app, textStatus, xhr) {
                callback(app);
            }, 
            error: function (xhr, textStatus, errorThrown) {
                callback();
            }
        });
    }

    function _user(callback) {
        _me(function() {
            callback(window.user);
        });
    }

    function _clear() {
        window.user = undefined;
    }

	return {
        user : _user,
        clear : _clear
	}
});