/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt"], function($) {
    function _init() {
        $("#saveBtn").bind("click",function(){

            $("#resCreateError").hide();
            var form = $("#resEditForm");
            var res_name=$("#name").val();
            var res_identifier=$("#identifier").val();
            var res_old_name=$("#old_name").val();
            var res_old_identifier=$("#old_identifier").val();

            if($.trim(res_name) == ''||res_name=='undefined'){
                $.alert("请填写资源名称",function(){
                    console.log("pls input res name");
                });
                return;
            }

            if($.trim(res_identifier) == ''||res_identifier=='undefined'){
                $.alert("请填写资源标识",function(){
                    console.log("pls input res identifier");
                });
                return ;
            }

            if((res_name==res_old_name)&&(res_identifier==res_old_identifier)){
                $.alert("信息没有变化",function(){
                   console.log("no need to change.");
                });
                return false ;
            }

            // if(form.validate()) {
            var data=JSON.stringify(form.serializeObject());
            var resCode=form.attr("code");

            $.ajax({
                url : form.attr("action"),
                type : "PUT",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	 if(data && data.errorCode && data.errorMessage){
                 		$("#resCreateErrorMsg").html(data.errorMessage);
                 		$("#resCreateError").show();
                 		}else{
                 			 window.location.href = "#admin/resource/list";
                 		}
                  
                },
                error : function(xhr, textStatus, errorThrown) {
                   
                   // window.location.href = "#admin/resource/update/"+resCode;
                    $("#resCreateError").show();
                    if(xhr.status === 401) {
                        $("#resCreateError").show();
                    }
                }
            });
            // }
        });

    }
    return {
        initialize : _init
    }
});