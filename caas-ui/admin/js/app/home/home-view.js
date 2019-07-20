define(["text!app/home/home-tpl.html", "app/constants", 
    "app/components/admin-user", 
    "app/components/nav/nav-view"], function(tpl, constants, adminUser, navView) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            this.$el.html(this.template({}));
            navView.refresh();
            adminUser.user(function(user) {
                if(user.role === constants.ROLE_ANONYMOUS) {
                    $("#loginArea").show(); 
                    require(["app/login/login", "text!app/login/login-tpl.html"], function(login, loginTpl) {
                        var loginForm = $(loginTpl);
                        loginForm.find("h2").remove();
                        $("#loginArea div.body").html(loginForm.html());
                        login.initialize();
                    });
                } else {
                    $("#loginArea").remove();
                }

                if(user.appCode) {
                    $("#applyApp").remove();
                } else {
                    $("#applyApp a").attr("href", $("#applyApp a").attr("href") + user.code);
                    $("#applyApp").show();
                }
            });

            return this;
        }
    });
});