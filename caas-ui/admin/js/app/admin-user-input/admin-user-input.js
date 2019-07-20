define([ "jquery", "jquery.utility","jquery.validator","jquery.verifiable","md5"], function($,md5) {
	function _init() {
        $("#adminUserError").hide();
        saveAdminUser();
        $("input[name='name']").verifiable("/validation/admin/name/");
        $("input[name='email']").verifiable("/validation/admin/email/");
    };


    function saveAdminUser(){
            $("#saveAdminUser").on("click", function() {
                if($("form").validate()) {
                    var form = $("form[name='adminUser']");
                    var data = form.serializeObject();
                    data.password = hex_md5(data.password);
                    $.ajax({
                       url : getUrl(form.attr("action"),$("#adminUserCode").val()),
                        type : getType($("#adminUserCode").val()),
                        contentType : "application/json",
                        data : JSON.stringify(data),
                        success : function(data, textStatus, xhr) {
                            window.location.href = "#superadmin/admin-user";
                        },
                        error : function(xhr, textStatus, errorThrown) {
                            //alert(JSON.stringify(xhr));
                            //window.location.href = "#admin/resource/add";
                            $("#adminUserError").show();

                        }
                });
             }
        });
    };

    function getUrl(baseUrl,adminUserCode) {
        if(adminUserCode) {
            return baseUrl+"/"+adminUserCode;
        } else {
            return baseUrl;
        }
    }

    function getType(adminUserCode) {
        if(adminUserCode) {
            return "put";
        } else {
            return "post";
        }
    }
    
	return {
		initialize : _init
	}
});








