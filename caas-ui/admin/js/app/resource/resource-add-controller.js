/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/resource/resource-add-view", "app/resource/resource-add-model"], function (View, AddModel) {
    var controller = function() {
        var model;
        model = new AddModel({});
        return new View({
            model : model
        });
    };

    return controller;
});