/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable"], function($) {
    function _init() {
        save();
        addValidation();


    };
    /**
     * validate the input once blur
     * */
    function addValidation(){
        var appCode =$("input[name='appCode']").val();
        var roleName =$("#name").val();
        var url="/validation/app/"+appCode+"/operation/name/";
        $("input[name='name']").verifiable(url);
    }

    function save(){
        $("#saveBtn").bind("click",function(){
            var name=$("#name").val();
            var appCode=$("#appCode").val();
            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写主题名称",function(){
                    console.log("pls input name");
                });
                return false;
            }
            var code=$("input[name='code']").val();
            var param={};
            param.code=code;
            param.name=name;
            param.appCode=appCode;

            var data=JSON.stringify(param);
            $.ajax({
                url : "/admin/operation/"+code,
                type : "PUT",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	if(data && data.errorCode && data.errorMessage){
                		$("#resCreateErrorMsg").html(data.errorMessage);
                		$("#resCreateError").show();
                		}else{
                			 window.location.href = "#admin/operation/list";
                		}
                },
                error : function(xhr, textStatus, errorThrown) {
                    $("#resCreateError").show();
                }
            });
        });
        return false;
    };

    return {
        initialize : _init
    }
});
