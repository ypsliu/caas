/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/subject/subject-list-view", "app/subject/subject-list-collection"], function (View, Collection) {
    var controller = function() {
        return new View({
            collection : new Collection()
        });
    };
    return controller;
});
