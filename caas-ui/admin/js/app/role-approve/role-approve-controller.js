define(["app/role-approve/role-approve-view", 
	"app/role-approve/role-approve-collection"], function (View, Collection) {
	var controller = function() {
		return new View({
			collection : new Collection()
		});
	};
	return controller;
});

