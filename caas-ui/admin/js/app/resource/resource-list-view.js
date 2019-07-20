/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["text!app/resource/resource-list-tpl.html","jquery", "jquery.searchable.list"], function(tpl,$) {
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
                resources : this.collection.toJSON()
            }));
            $("#sp-table").searchable({
                pageSize:5
            });
            require(["app/resource/resource-list-ops"], function(ops) {
                ops.initialize();
            });

            return this;
        }
    });
});