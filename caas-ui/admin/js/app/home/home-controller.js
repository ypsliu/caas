define(["app/home/home-view", "app/home/home-model"], function (View, Model) {
    var controller = function() {
        return new View({
            model : new Model()
        });
    };
    return controller;
});