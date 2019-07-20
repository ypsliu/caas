define([ "jquery", "jquery.utility" , "jquery.validator", "jquery.verifiable","md5"], function($,md5) {
	function _init() {
        $("#appEditError").hide();
				initCheckVcode();
        saveApp();
        generateSign();
        generateAppKey();
        $("input[name='name']").verifiable("/validation/app/name/");
    };

function initCheckVcode(){
	$(".checkVcodeItem").on('click',function(){
		var text=$(this).text();
			$("#checkVcodeVal").text(text);
		if(text=="校验"){
 	$("#checkVcode").attr("value","YES");
		}
		if(text=="不校验"){
	$("#checkVcode").attr("value","NO");
		}
		if(text=="无设置"){
	$("#checkVcode").attr("value","NO_SET");
		}
	});
}
    function saveApp(){
            $("#appSaveBtn").on("click", function() {
                checkResource();
                checkSign();
                checkAppType();
								checkVcode();
								checkEmailNotify();
                if($("form").validate()) {
                var form = $("form[name='app-input']");
                $.ajax({
                   url : getUrl(form.attr("action"),$("#appcode").val()),
                    type : getType($("#appcode").val()),
                    contentType : "application/json",
                    data : JSON.stringify(form.serializeObject()),
                    success : function(data, textStatus, xhr) {
                        window.location.href = "#superadmin/app";
                    },
                    error : function(xhr, textStatus, errorThrown) {
                        //alert(JSON.stringify(xhr));
                        //window.location.href = "#admin/resource/add";
                        $("#appEditError").show();
                    }
                });
             }
        });
    };

    function getUrl(baseUrl,appcode) {
        if(appcode) {
            return baseUrl+"/"+appcode;
        } else {
            return baseUrl;
        }
    }

    function getType(appcode) {
        if(appcode) {
            return "put";
        } else {
            return "post";
        }
    }

    function generateAppKey(){
        $("#generateKey").on("click", function() {
            $("#key").val($.uuid().replace(new RegExp("\\-", "gm"), ""));
        });
    }

    function generateSign(){
        $("#generateSecret").on("click", function() {
            $("#secret").val($.uuid().replace(new RegExp("\\-", "gm"), ""));
        });
    }

    function checkResource(){
        if($("#checkResource").is(':checked')) {
            $("input[name='checkResource']").val("true");
        } else {
            $("input[name='checkResource']").val("false");
        }
    }

    function checkAppType(){
        if($("#appType").is(':checked')) {
            $("input[name='appType']").val("PUBLIC");
        } else {
            $("input[name='appType']").val("PROTECTED");
        }
    }

    function checkSign(){
        if($("#checkSign").is(':checked')) {
            $("input[name='checkSign']").val("true");
        } else {
            $("input[name='checkSign']").val("false");
        }
    }
   function checkVcode(){
		 var text=$(this).text();
		 $("#checkVcodeVal").text(text);
	 if(text=="校验"){
 $("#checkVcode").attr("value","YES");
	 }
	 if(text=="不校验"){
 $("#checkVcode").attr("value","NO");
	 }
	 if(text=="无设置"){
 $("#checkVcode").attr("value","NO_SET");
	 }

	 }

	 function checkEmailNotify(){
		 if($("#emailNotify").is(':checked')) {
				 $("input[name='emailNotify']").val("true");
		 } else {
				 $("input[name='emailNotify']").val("false");
		 }
	 }
	return {
		initialize : _init
	}
});
