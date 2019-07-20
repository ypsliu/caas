define(["app/popup/ipa-users/ipa-users-view", 
    "app/popup/ipa-users/ipa-users-model"], function (View, Model) {
    var controller = function(appCode) {
        return new View({
            model : new Model({
                appCode : appCode
            })
        });
    };
    return controller;
});