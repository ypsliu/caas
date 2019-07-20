/**
 * Created by Wang Shu Guang on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable"], function($) {
    function _init() {
        $("#resCreateError").hide();
        addValidation();
        save();


    };
    /**
     * validate the input once blur
     * */
    function addValidation(){

        var appCode =$("input[name='appCode']").val();
        var name =$("#name").val();
        var url="/validation/app/"+appCode+"/subject/name/";

        $("input[name='name']").verifiable(url);

    }


    function save(){
        $("#saveBtn").bind("click",function(){
            var name=$("input[name='name']").val();
            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写角色名称",function(){
                    console.log("pls input res name");
                });
                return;
            }


            var appCode=$("input[name='appCode']").val();
            var param={};
            param.name=name;
            param.appCode=appCode;
            
            var data=JSON.stringify(param);
            $.ajax({
                url : "/admin/subject",
                type : "POST",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	if(data && data.errorCode && data.errorMessage){
                		$("#createErrorMsg").html(data.errorMessage);
                		$("#createError").show();
                	}else{
                		  window.location.href = "/#admin/subject/list";
                	}

                },
                error : function(xhr, textStatus, errorThrown) {
                    $("#createError").show();
                }
            });
            // }
        });
    };

    return {
        initialize : _init
    }
});
