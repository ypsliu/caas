/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility" ,"jquery.prompt"], function($) {
    function _init() {
        //  $(".roleBtn").on("click", function(code){
        //     var user_name=$(this).attr("user_name");
        //     var user_code=$(this).attr("user_code");
        //     var content= $("#role_table_"+user_code).html();
        //     showRoleDialog(user_code,user_name,content);
        //     handleRemoveRole();
        // });
}
    
    function handleRemoveRole(){
        $("a[id^='removeRoleBtn_']").on("click", function(){
            var $self =$(this);
            $.confirm("确认删除 ?", function() {
                var role_code=$self.attr("role_code");
                var user_code=$self.attr("user_code");
                var url="/admin/user/"+user_code+"/role/"+role_code;
                $.ajax({
                    url : url,
                    type : "DELETE",
                    contentType : "application/json",
                    success : function(data, textStatus, xhr) {
                        var row_class="row_"+user_code+"_"+role_code;
                        $("."+row_class).remove();
                    }
                });
            },function(){
                console.log("cancel to remove");
            });
        });
    } 

    function showRoleDialog(user_code,user_name,content){
        var message=content;
        var dialog = new AJS.Dialog({
            width: 600,
            height: 600,
            closeOnOutsideClick: false
        });

        dialog.addHeader("用户:"+user_name+" 的角色列表", "aui-header-logo aui-header-logo-aui");
        dialog.addPanel("", "<p><table class='aui aui-table-sortable'> " + message + "</table></p>", "");

        dialog.addButton(
            "知道了",
            function() {
                var mark="tr[class^='row_"+user_code+"_']";
                var noOfrow=$(mark).length;

                dialog.hide();
                dialog.remove();

                //no roles for this user, then remove the row
               // alert(noOfrow);
                if(noOfrow==0){
                    $("#row_"+user_code).remove();
                    //rebuildTable();
                }


            },
            "aui-button aui-button-primary"
        );
        dialog.show();
    }

function rebuildTable(){
    $("#sp-table").searchable({
        pageSize:10
    });
}


    return {
        initialize : _init
    }
});