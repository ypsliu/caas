define(['app/admin-user/admin-user-model'], function(Model) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin",
        initialize: function() {
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});