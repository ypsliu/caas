define(["app/popup/user-roles/user-roles-view", 
    "app/popup/user-roles/user-roles-model"], function (View, Model) {
    var controller = function(appCode, userCode) {
        return new View({
            model : new Model({
            	appCode : appCode,
            	userCode : userCode
            })
        });
    };
    return controller;
});