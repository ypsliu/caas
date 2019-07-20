define(["text!app/role-approve/role-approve-tpl.html",
    "app/role-approve/approve",
    "jquery"], function(tpl, approve, $) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.collection.off("sync");
            this.collection.on("sync", this.render, this);
            this.collection.fetch();
        },
        render: function (event) {
            this.$el.html(this.template({
                userRoles : this.collection.toJSON()
            }));

            approve.initialize();

            return this;
        }
    });
});