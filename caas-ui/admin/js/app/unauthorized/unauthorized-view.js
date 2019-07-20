define(["text!app/unauthorized/unauthorized-tpl.html", 
    "app/components/nav/nav-view"], function(tpl, navView) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            navView.reset();
            this.$el.html(this.template({}));
            return this;
        }
    });
});