define(["app/admin-access-log/admin-access-log-view", 
	"app/admin-access-log/admin-access-log-model"], function (View, Model) {
    var controller = function(minTimeInMs, maxTimeInMs, 
    	paramsKeyword, resultKeyword, exceptionKeyword, fromTime, toTime, pageNo, pageSize) {
    	var condition = {};
    	if(minTimeInMs) {
    		condition.minTimeInMs = minTimeInMs;
    	}
    	if(maxTimeInMs) {
    		condition.maxTimeInMs = maxTimeInMs;
    	}
    	if(paramsKeyword) {
    		condition.paramsKeyword = paramsKeyword;
    	}
    	if(resultKeyword) {
    		condition.resultKeyword = resultKeyword;
    	}
    	if(exceptionKeyword) {
    		condition.exceptionKeyword = exceptionKeyword;
    	}
    	if(fromTime) {
    		condition.fromTime = fromTime;
    	}
    	if(toTime) {
    		condition.toTime = toTime;
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