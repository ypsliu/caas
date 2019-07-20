define(["jquery"], function($) {
	function _logout(success) {
		$.ajax({
            url: "/admin/logout",
            type: "DELETE",
            success: function(data, textStatus, xhr) {
            	if(success) {
            		success();
            	}
            }
        });
	}

    return {
        logout : _logout
    }
});