define(["jquery"], function($) {
	var url = window.location.href;
	var paramPairs = url.substring(url.indexOf("?") + 1).split("&");
	var result;
	for(var i = 0;i < paramPairs.length;i ++) {
		var parts = paramPairs[i].split("=");
		if(parts[0] === "result") {
			result = parts[1];
		}
	}
	if(result) {
		$("div.alert-success strong").text(decodeURIComponent(result));
	}

	$("[role='close']").on("click", function() {
		window.close();
	});
});