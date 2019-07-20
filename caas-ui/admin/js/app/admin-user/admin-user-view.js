define(["text!app/admin-user/admin-user-tpl.html","app/admin-user/admin-user","jquery", "jquery.searchable.list"], function(tpl,adminuser, $) {
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
                adminUsers : this.collection.toJSON()
            }));
            $("#sp-table").searchable({
                pageSize : 10
            });
            adminuser.initialize();
            return this;
        }
    });
});