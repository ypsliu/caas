/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/operation/operation-list-view", "app/operation/operation-list-collection"], function (View, Collection) {
    var controller = function() {
        return new View({
            collection : new Collection()
        });
    };
    return controller;
});
