/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/subject/subject-update-view", "app/subject/subject-update-model"], function (View, ModifyModel) {
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
