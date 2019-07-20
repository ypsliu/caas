/**
 * Created by Wang Shu Guang on 2016-9-7.
 */
define([ "jquery", "jquery.utility","jquery.prompt","jquery.verifiable","md5"], function($,md5) {
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
        var mobile =$("#mobile").val();
        var email =$("#email").val();
        var url_name="/validation/user/name/";
        var url_email="/validation/user/email/";
        var url_mobile="/validation/user/mobile/";

        $("input[name='name']").verifiable(url_name);
        $("input[name='mobile']").verifiable(url_mobile);
        $("input[name='email']").verifiable(url_email);
    }


    function save(){
        $("#saveBtn").bind("click",function(){
            var name=$("input[name='name']").val();
              var password=$("input[name='password']").val();
                var email=$("input[name='email']").val();
                  var mobile=$("input[name='mobile']").val();

            if($.trim(name) == ''||name=='undefined'){
                $.alert("请填写用户姓名",function(){
                    console.log("pls input res name");
                });
                return false;
            }
            if($.trim(password) == ''||password=='undefined'){
                $.alert("请填写用户密码",function(){
                    console.log("pls input res pwd");
                });
                return false;
            }
            if($.trim(email) == ''||email=='undefined'){
                $.alert("请填写邮箱地址",function(){
                    console.log("pls input res name");
                });
                return false;
            }
               if($.trim(mobile) == ''||mobile=='undefined'){
                $.alert("请填写电话号码",function(){
                    console.log("pls input res name");
                });
                return false;
              }

                if(!(/^1[34578]\d{9}$/.test(mobile))){
                    $.alert("电话号码有误，请重填",function(){
                        console.log("mobile format error");
                    });
                    return false;
                }

                var myreg = /^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/;
                if(!myreg.test(email))
                {
                     $.alert("邮箱地址有误，请重填",function(){
                         console.log("mobile format error");
                     });
                     return false;
                }


            var appCode=$("input[name='appCode']").val();
            var param={};
            param.name=name;

            param.password=hex_md5(password);
            param.status="ENABLED";
            param.email=email;
            param.mobile=mobile;
            param.appCode=appCode;





            var data=JSON.stringify(param);
            $.ajax({
                url : "/admin/app/user",
                type : "POST",
                contentType : "application/json",
                data :data ,
                success : function(data, textStatus, xhr) {
                	if(data && data.errorCode && data.errorMessage){
                		$("#createErrorMsg").html(data.errorMessage);
                		$("#createError").show();
                	}else{
                		  window.location.href = "/#admin/user-managment";
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
