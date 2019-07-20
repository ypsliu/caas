define(["text!app/change-password-success/cpw-success-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            this.$el.html(this.template({}));
            return this;
        }
    });
});