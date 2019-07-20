/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/resource/resource-update-view", "app/resource/resource-update-model"], function (View, ModifyModel) {
    var controller = function(code) {
        var model;
        if(code) {
            model = new ModifyModel({
                code : code
            });
        }
        return new View({
            model :  model
        });
    };
    return controller;
});