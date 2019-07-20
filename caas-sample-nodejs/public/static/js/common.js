var vcodeInProgress;
var vcodeReq;
function getVcode(callback) {
	if(!vcodeInProgress) {
		vcodeInProgress = true;
		vcodeReq = $.ajax({
			url : "/vcode",
			success : function(result, textStatus, xhr) {
				callback("data:image/png;base64," + result);
				vcodeInProgress = false;
			},
			error : function(xhr, textStatus, errorThrown) {
				vcodeInProgress = false;
			}
		});
	} else {
		if(vcodeReq) {
			vcodeReq.abort();
		}
	}
}

function validate(input, url) {
	input.on("change", function() {
		var _self = $(this);
		$.ajax({
			url : url + _self.val(),
			success : function(result, textStatus, xhr) {
				if(result.success) {
					_self.addClass("valid-border");
					_self.removeClass("error-border");
				} else {
					_self.removeClass("valid-border");
					_self.addClass("error-border");
				}
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	});
}

$.each($("input[validate-url]"), function(i, value) {
	validate($(value), $(value).attr("validate-url"));
});