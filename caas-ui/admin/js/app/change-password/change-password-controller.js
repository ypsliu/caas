define(["app/change-password/change-password-view", 
	"app/change-password/change-password-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});