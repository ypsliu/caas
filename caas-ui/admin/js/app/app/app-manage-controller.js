define(["app/app/app-manage-view", "app/app/app-manage-collection"], function (View, Collection) {
	var controller = function() {
		return new View({
			collection : new Collection()
		});
	};
	return controller;
});

