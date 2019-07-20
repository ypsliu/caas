define(["text!app/login/login-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            this.$el.html(this.template({}));
            require(["app/login/login"], function(login) {
                login.initialize();
            });
            return this;
        }
    });
});
