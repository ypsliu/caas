/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility" ,"jquery.prompt"], function($) {
    function _init() {

        $("#advance").on("click", function() {
            var width = $(window).width();
            var height = $(window).height();
            window.open(
                "/rtree/rtree.html", 
                "newwindow", 
                "height=" + height + ",width=" + width + ",top=0,left=0,toolbar=no,menubar=no,resizable=no,location=no,status=no"
            );
        });

        /**
         * delete button
         * */
        $("#delBtn").on("click", function(){
            var selectedNo= $("input[id^='role']:checked").length;
            if(selectedNo<=0){
                $.alert("请先选择删除内容",function(){
                    console.log("pls choose one row");
                });
                return false;
            }
            delRow();
        });

        $(".applyusers").on("click",function(){
            var username=$(this).attr("name");
            var rolecode=$(this).attr("code");
            getRoleApplyingUsers(rolecode,username);
        });
    /**
         *Approve the user's applying for one role
         */




    };
    function bindApproveBtnAction(){
        $(".approve").bind("click",function(){
            var user_code=$(this).closest("span").attr("usercode");
            var role_code=$(this).closest("span").attr("rolecode");
            //alert(user_code+"---"+role_code);
            if(user_code=='undefined' ||role_code=='undefined' ){
                console.log("can not get usercode and rolecode");
                return false;
            }
            $.confirm("确认批准 ?", function() {
                approveApplying(user_code,role_code);
            },function(){
                console.log("no approve")
            });

        });
    }
    function approveApplying(user_code,role_code){

      var url=  "/admin/user/"+user_code+"/role/"+role_code;
        $.ajax({
            url :url,
            type : "PUT",
            contentType : "application/json",
            success : function(data, textStatus, xhr) {
                $.info("操作成功",function(){
                    console.log("approve success");
                });
                $("#approve_"+user_code).remove();
            }
        });
    }
    function getRoleApplyingUsers(role_code,role_name){
        $.ajax({
            url: "/admin/role/"+role_code+"/applying",
            contentType: "application/json",
            success: function (data, textStatus, xhr) {
                popupUserList(role_code,role_name,data);
            }
        });
    }
    function popupUserList(role_code,role_name,users){

        var dialog = new AJS.Dialog({
            width: 600,
            height: 600,
            closeOnOutsideClick: false
        });
        var user_list="";
        for(var i=0;i<users.length;i++){
            user_list+="<tr id='approve_"+users[i].code+"'><td>"+users[i].name+"</td><td><span class='approve' usercode='"+users[i].code+"' rolecode='"+role_code+"'><a class='aui-button aui-button-primary' href='javascript:void(0)'>批准</a></span></td></tr>";
        }
        var user_table= "<table class='aui aui-table-sortable'> <thead> <tr> <th>用户名称</th> <th></th></tr> </thead> <tbody>"+user_list+" </tbody> </table>";
        dialog.addHeader("角色'"+role_name+"'用户申请列表", "aui-header-logo aui-header-logo-aui");
        dialog.addPanel("", "<p>" + user_table + "</p>", "");

        dialog.addButton(
            "知道了",
            function() {
                dialog.hide();
                dialog.remove();

            },
            "aui-button aui-button-primary"
        );
        bindApproveBtnAction();
        dialog.show();
    }

    function delRow(){
        $.confirm("确认删除 ?", function() {
            $("input[id^='role']:checked").each(function(){
                var id=$(this).attr("id");
                var code=id.split("_")[1];
                $.ajax({
                    url : "/admin/role/"+code,
                    type : "DELETE",
                    contentType : "application/json",
                    success : function(data, textStatus, xhr) {
                        $("#row_"+code).remove();
                    },
                    error : function(xhr, textStatus, errorThrown) {
                        alert("failed"+JSON.stringify(xhr));
                    }
                });
            })
        }, function() {
            console.log("cancel");
        });




        }

    return {
        initialize : _init
    }
});