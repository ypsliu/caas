define(["app/user/user-view", "app/user/user-model"], function (View, model) {
    var controller = function(name, email, mobile,
                              status, pageNo, pageSize) {
        var condition = {};
        if(name) {
            condition.name = name;
        }
        if(email) {
            condition.email = email;
        }
        if(mobile) {
            condition.mobile = mobile;
        }
        if(status) {
            condition.status = status;
        }
        if(pageNo) {
            condition.pageNo = pageNo;
        } else {
            condition.pageNo = 1;
        }
        if(pageSize) {
            condition.pageSize = pageSize;
        } else {
            condition.pageSize = 10;
        }

        return new View({
            model : new model({
                condition : condition
            })
        });
    };
    return controller;
});

