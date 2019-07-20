/**
 * Created by Wang Shu Guang on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable"], function($) {
    function _init() {
        $("#resCreateError").hide();
        loadResList();
        addValidation();
        saveRole();
        initResourceSelection();

    };
    /**
     * validate the input once blur
     * */
    function addValidation(){

        var appCode =$("input[name='appCode']").val();
        var roleName =$("#name").val();
        var url="/validation/app/"+appCode+"/role/name/";

        $("input[name='name']").verifiable(url);

    }
    /**
     * get resources already selected.
     * and remove the selected list from resource list.
     *
     * */
    function initResourceSelection(){
        var leftSel = $("#selectL");
        var rightSel = $("#selectR");
        $("#toright").bind("click",function(){
            leftSel.find("option:selected").each(function(){
                $(this).remove().appendTo(rightSel);
            });
        });
        $("#toleft").bind("click",function(){
            rightSel.find("option:selected").each(function(){
                $(this).remove().appendTo(leftSel);
            });
        });
        leftSel.dblclick(function(){
            $(this).find("option:selected").each(function(){
                $(this).remove().appendTo(rightSel);
            });
        });
        rightSel.dblclick(function(){
            $(this).find("option:selected").each(function(){
                $(this).remove().appendTo(leftSel);
            });
        });
    }
    function saveRole(){
        $("#saveBtn").bind("click",function(){
            var name=$("input[name='name']").val();
            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写角色名称",function(){
                    console.log("pls input res name");
                });
                return;
            }
            // if(form.validate()) {
            var roleType=$('input[name=roleType]:checked').val();

         /*   var autoAuth= "";
            if ($("#autoAuth").is(':checked')) {
                autoAuth="true";
            }else{
                autoAuth="false";
            }
            var applyEnabled= "";
            if ($("#applyEnabled").is(':checked')) {
                applyEnabled="true";
            }else{
                applyEnabled="false";
            }*/

            var appCode=$("input[name='appCode']").val();
            var role={};
            role.name=name;
//            role.autoAuth=autoAuth;
//            role.applyEnabled=applyEnabled;
            role.roleType=roleType;
            role.appCode=appCode;
            var resources=[];
            var rightSel = $("#selectR");
            rightSel.find("option").each(function(){
                var resource={};
                resource.code=$(this).val();
                resources.push(resource);
            });


            role.resources=resources;
            var data=JSON.stringify(role);
            $.ajax({
                url : "/admin/role",
                type : "POST",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	if(data && data.errorCode && data.errorMessage){
                		$("#createErrorMsg").html(data.errorMessage);
                		$("#createError").show();
                	}else{
                		  window.location.href = "/#admin/role/list";
                	}

                },
                error : function(xhr, textStatus, errorThrown) {
                    $("#createError").show();
                }
            });
            // }
        });
    };
    /**
     * load all resources
     * */
    function loadResList(){
        var appCode=$("input[name='appCode']").val();
        var url="/admin/app/"+appCode+"/resource";
        $.ajax({
            url : url,
            type : "GET",
            contentType : "application/json",
            success : function(data, textStatus, xhr) {
                var length=data.length;
                var optionlist="";
                for(var i=0;i<length;i++){
                    optionlist+="<option value='"+data[i].code+"'>"+data[i].name+"</option>";
                }
                $("#selectL").find("option").remove();
                $("#selectL").html(optionlist);
            },
            error : function(xhr, textStatus, errorThrown) {
                alert("加载资源列表错误");
            }
        });
    };
    return {
        initialize : _init
    }
});
