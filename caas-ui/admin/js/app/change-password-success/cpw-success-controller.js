define(["app/change-password-success/cpw-success-view", 
	"app/change-password-success/cpw-success-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});