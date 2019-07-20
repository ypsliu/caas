define(["app/unauthorized/unauthorized-view", 
	"app/unauthorized/unauthorized-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});