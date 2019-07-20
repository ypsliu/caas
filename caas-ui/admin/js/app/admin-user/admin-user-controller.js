define(["app/admin-user/admin-user-view", "app/admin-user/admin-user-collection"], function (View, Collection) {
    var controller = function() {
        return new View({
            collection : new Collection()
        });
    };
    return controller;
});