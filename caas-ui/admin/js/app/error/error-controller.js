define(["app/error/error-view", 
	"app/error/error-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});