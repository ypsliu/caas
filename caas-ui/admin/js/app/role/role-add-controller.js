/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/role/role-add-view", "app/role/role-add-model"], function (View, AddModel) {
    var controller = function() {
        var model;
        model = new AddModel({});
        return new View({
            model : model
        });
    };

    return controller;
});