/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable"], function($) {
    function _init() {
        handleSave();



    }


    function handleSave(){
        $("#saveBtn").bind("click",function(){
            $("#resCreateError").hide();
            var form = $("#resNewForm");

            var name= $("#name").val();

            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写资源名称",function(){
                    console.log("pls input res name");
                });
                return;
            }
            var identifier = $("#identifier").val();

            if($.trim(identifier) == ''||identifier=='undefined'){
                $.alert("请填写资源标识",function(){
                    console.log("pls input res identifier");
                });
                return ;
            }

            // if(form.validate()) {
            saveResource();
            // }
        });
    }
    function saveResource(){
        var form = $("#resNewForm");
        var data=JSON.stringify(form.serializeObject());
        $.ajax({
            url : form.attr("action"),
            type : form.attr("method"),
            contentType : "application/json",
            data :data ,
            success : function(data, textStatus, xhr) {
                 if(data.errorCode && data.errorMessage){
            		$("#resCreateErrorMsg").html(data.errorMessage);
            		$("#resCreateError").show();
            		}else{
            			 window.location.href = "#admin/resource/list";
            		}
            },
            error : function(xhr, textStatus, errorThrown) {
                $("#resCreateError").show();
                if(xhr.status === 401) {
                    $("#resCreateError").show();
                }
            }
        });
    }
    return {
        initialize : _init
    }
});