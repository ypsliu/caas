/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["app/user-managment/user-list-view", "app/user-managment/user-list-model"], function (View, Model) {
    var controller = function(name,email,mobile,pageNo, pageSize) {
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
        	  model : new Model({
              	condition : condition
              })
        });
    };
    return controller;
});