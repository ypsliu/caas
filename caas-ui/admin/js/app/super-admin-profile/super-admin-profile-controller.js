define(["app/super-admin-profile/super-admin-profile-view", 
    "app/super-admin-profile/super-admin-profile-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});