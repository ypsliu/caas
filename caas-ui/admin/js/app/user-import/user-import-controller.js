/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/user-import/user-import-view", "app/user-import/user-import-model"], function (View, AddModel) {
    var controller = function() {
        var model;
        model = new AddModel({});
        return new View({
            model : model
        });
    };

    return controller;
});
