define(['app/user/user-model'], function(Model) {
    return Backbone.Collection.extend({
        model: Model,
        url: "/admin/user/search",
        initialize: function() {
        },
        parse: function(collection) {
            return this.add(collection);
        }
    });
});