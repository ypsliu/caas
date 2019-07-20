/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/role/role-list-view", "app/role/role-list-collection"], function (View, Collection) {
    var controller = function() {
        return new View({
            collection : new Collection()
        });
    };
    return controller;
});