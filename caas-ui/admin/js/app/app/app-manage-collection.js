define(['app/app/app-manage-model'], function(Model) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/app",
        initialize: function() {
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});