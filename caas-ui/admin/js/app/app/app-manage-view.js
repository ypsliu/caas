define(["text!app/app/app-manage-tpl.html","app/app/app-manage-query","jquery","jquery.searchable.list"], function(tpl,appQuery,$) {
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
                apps : this.collection.toJSON()
            }));
            $("#sp-table").searchable({
                pageSize : 10
            });
            appQuery.initialize();
            return this;
        }
    });
});