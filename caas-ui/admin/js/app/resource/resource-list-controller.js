/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/resource/resource-list-view", "app/resource/resource-list-collection"], function (View, Collection) {
    var controller = function() {
        return new View({
            collection : new Collection()
        });
    };
    return controller;
});