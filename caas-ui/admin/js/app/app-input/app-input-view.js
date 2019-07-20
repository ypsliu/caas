define(["text!app/app-input/app-input-tpl.html","app/app-input/app-input"], function(tpl, appInput) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            if(this.model.get("code")) {
                this.model.off("sync");
                this.model.on("sync", this.render, this);
                this.model.fetch();
            } else {
                this.render();
            }
        },
        render: function (event) {

            if(this.model.get("code")) {
                this.$el.html(this.template({
                    app : this.model.toJSON()
                }));
            } else {
                this.$el.html(this.template({
                    app : {}
                }));
            }
            appInput.initialize();
            return this;
        }
    });
});