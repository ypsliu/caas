define(["app/app-application/app-application-view", 
    "app/app-application/app-application-model"], function (View, Model) {
    var controller = function(userCode) {
        return new View({
            model : new Model({
            	userCode : userCode
            })
        });
    };
    return controller;
});