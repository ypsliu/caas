define(["app/admin-user-profile/admin-user-profile-view", 
    "app/admin-user-profile/admin-user-profile-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});