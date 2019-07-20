define(["app/popup/app-select/app-select-view", 
    "app/popup/app-select/app-select-model"], function (View, Model) {
    var controller = function(adminCode) {
        return new View({
            model : new Model({
                adminCode : adminCode
            })
        });
    };
    return controller;
});