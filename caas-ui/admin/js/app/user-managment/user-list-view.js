/**
 * Created by wangshuguang on 2016-9-7.
 */
define(["text!app/user-managment/user-list-tpl.html","app/constants", 
    "app/components/admin-user", "jquery","jquery.searchable.list","jquery.colorbox" , "jquery.prompt"], function(tpl,constants,adminUser, $) {
    return Backbone.View.extend({
        el: "#page",
        template: _.template(tpl),
        initialize: function (params) {
            this.model.off("sync");
            this.model.on("sync", this.render, this);
             
            this.model.fetch({
                data : JSON.stringify(this.model.get("condition")),
                type : "POST",
                contentType: "application/json"
            });
        },
        initForm : function(condition) {
            for(var key in condition) {
                if(key === "resource") {
                    condition[key] = decodeURIComponent(condition[key]);
                }
                $("form input[name='" + key + "']").val(condition[key]);
            }
        },
        render: function (event) {
            var self = this;
            adminUser.user(function(user) {
                self.$el.html(self.template({
                    users : self.model.toJSON(),
                    appCode : user.appCode
                }));
    //          $("#sp-table").searchable({
    //              pageSize:10
    //           });
                require(["app/user-managment/user-list-ops"], function(ops) {
                    ops.initialize();
                });
                self.initForm(self.model.get("condition"));
                self.onPage();
                self.onSearch();

                $("a[role='colorbox']").colorbox({
                    iframe : true, 
                    width : "70%", 
                    height : "90%",
                    opacity : 0.5,
                    overlayClose : false
                });

                AJS.InlineDialog(AJS.$("#add-user"), "add-user",
                    function(content, trigger, showPopup) {
                        content.css({"width" : "165px", "overflow" : "auto"}).html($("#add-user-buttons").html());
                        showPopup();
                        $("a[role='add-ipa-user']").colorbox({
                            iframe : true, 
                            width : "70%", 
                            height : "90%",
                            opacity : 0.5,
                            overlayClose : false
                        });
                        $("a[role='create-user']").on("click", function() {
                            $("#inline-dialog-add-user").remove();
                        });
                        return false;
                    }
                );

                $("a[role='remove-from-app']").on("click", function() {
                    var self = $(this);
                    $.confirm("确认从应用中移除该用户？<br/>点击确认将移除该用户在当前应用中的所有角色权限。", function() {
                        var appUser = {
                            appCode : user.appCode,
                            userCode : self.attr("data-user-code")
                        }
                        $.ajax({
                            url : "/admin/app/user",
                            type : "DELETE",
                            contentType : "application/json",
                            data : JSON.stringify(appUser),
                            success: function(result, textStatus, xhr) {
                                window.location.reload();
                            }, 
                            error: function (xhr, textStatus, errorThrown) {
                            }
                        });
                    }, function() {

                    });
                });
            });

            return self;
        },    
        onSearch : function() {
            var self = this;
            $("#search").on("click", function() {
                var data = $("form").serializeObject();
                window.location.href = self.buildPath(data);
            });
        },
        onPage : function() {
            var self = this;
            $("div.pagination ul li:not(.inactive)").on("click", function() {
                var condition = self.model.get("condition");
                condition.pageNo = $(this).attr("data");
               
                window.location.href = self.buildPath(self.model.get("condition"));
            });
        },
        buildPath : function(condition) {
            var path = "#admin/user-managment";
            for(var key in condition) {
                if(condition[key].length > 0) {
                    path = path + "/" + key + "/" + condition[key];
                }
            }
            return path;
        }
    });
});