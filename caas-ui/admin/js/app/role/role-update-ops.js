/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable"], function($) {
    function _init() {
        refreshUIByData();
        initResourceSelection();
        loadResList();
        saveRole();
        addValidation();


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
    function refreshUIByData(){
        $("#resCreateError").hide();
        var isautoAuth=  $("input[name='autoAuthVal']").val();
        if(isautoAuth=="true"){
            $("#autoAuth").attr("checked","checked");
        }else{
            $("#autoAuth").removeAttr("checked");
        }

        var applyEnabled=  $("input[name='applyEnabledVal']").val();
        if(applyEnabled=="true"){
            $("#applyEnabled").attr("checked","checked");
        }else{
            $("#applyEnabled").removeAttr("checked");
        }

    }
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
            // if(form.validate()) {
            var name=$("#name").val();
            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写角色名称",function(){
                    console.log("pls input res name");
                });
                return false;
            }

            var code=$("input[name='code']").val();
//            var autoAuth="";
//            if ($("#autoAuth").is(':checked')) {
//                autoAuth="true";
//            }else{
//                autoAuth="false";
//            }
//
//            var applyEnabled= "";
//            if ($("#applyEnabled").is(':checked')) {
//                applyEnabled="true";
//            }else{
//                applyEnabled="false";
//            }

            var roleType=$('input[name=roleType]:checked').val();
            var appCode=$("input[name='appCode']").val();
            var role={};
            role.code=code;
            role.name=name;
//            role.autoAuth=autoAuth;
//            role.applyEnabled=applyEnabled;
            role.appCode=appCode;
            role.roleType=roleType;
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
                url : "/admin/role/"+code,
                type : "PUT",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	if(data && data.errorCode && data.errorMessage){
                		$("#resCreateErrorMsg").html(data.errorMessage);
                		$("#resCreateError").show();
                		}else{
                			 window.location.href = "#admin/role/list";
                		}
                   
                },
                error : function(xhr, textStatus, errorThrown) {
                    $("#resCreateError").show();
                    
                }
            });
            // }
        });
        return false;
    };
    function loadResList(){
        var appCode=$("input[name='appCode']").val();
        var url="/admin/app/"+appCode+"/resource";
        var selectedRes=[];
          $("#selectR").find("option").each(function(){
              var res={};
              res.code=$(this).val();
              res.name=$(this).text();
              selectedRes.push(res);
          });
        $.ajax({
            url : url,
            type : "GET",
            contentType : "application/json",
            success : function(data, textStatus, xhr) {
                var length=data.length;
                var optionlist="";
                for(var i=0;i<length;i++){
                    var involve=0;
                    for(j=0;j<selectedRes.length;j++){
                        if(selectedRes[j].code==data[i].code){
                            involve=1;
                        }
                    }
                    if(involve==0){
                        optionlist+="<option value='"+data[i].code+"'>"+data[i].name+"</option>";
                    }
                }
                $("#selectL").html(optionlist);
            },
            error : function(xhr, textStatus, errorThrown) {
                alert("error:"+JSON.stringify(xhr));
            }
        });
    };
    return {
        initialize : _init
    }
});