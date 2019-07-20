	define(["app/app-input/app-input-view",
        "app/app-input/app-input-modify-model",
	"app/app-input/app-input-add-model"], function (View, ModifyModel,AddModel) {
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