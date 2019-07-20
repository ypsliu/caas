define(["app/admin-user-input/admin-user-input-view", 
	"app/admin-user-input/admin-user-add-model", 
	"app/admin-user-input/admin-user-modify-model"], function (View, AddModel, ModifyModel) {
    var controller = function(code) {
    	var model;
    	if(code) {
    		model = new ModifyModel({
            	code : code
            });
    	} else {
    		model = new AddModel({});
    	}
        return new View({
            model : model
        });
    };
    return controller;
});