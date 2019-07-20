define(["text!app/admin-signup/admin-signup-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self = this;
            this.$el.html(this.template({}));
            require(["app/admin-signup/signup"], function(signup) {
                signup.initialize();
            });
            return self;
        }
    });
});