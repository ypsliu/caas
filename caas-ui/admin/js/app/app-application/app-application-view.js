define(["text!app/app-application/app-application-tpl.html",
    "app/app-application/application"], function(tpl, application) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self = this;
            this.$el.html(this.template({
                userCode : self.model.get("userCode")
            }));
            application.initialize(self.model.get("userCode"));
            return self;
        }
    });
});