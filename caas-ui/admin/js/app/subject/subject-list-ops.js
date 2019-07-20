/**
 * Created by Administrator on 2016-9-7.
 */
define([ "jquery", "jquery.utility" ,"jquery.prompt"], function($) {
    function _init() {

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

    };
 
    function delRow(){
        $.confirm("确认删除 ?", function() {
            $("input[id^='role']:checked").each(function(){
                var id=$(this).attr("id");
                var code=id.split("_")[1];
                $.ajax({
                    url : "/admin/subject/"+code,
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
