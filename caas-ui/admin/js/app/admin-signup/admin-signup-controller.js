define(["app/admin-signup/admin-signup-view", 
    "app/admin-signup/admin-signup-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});