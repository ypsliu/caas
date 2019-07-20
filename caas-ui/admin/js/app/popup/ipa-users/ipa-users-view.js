define(["text!app/popup/ipa-users/ipa-users-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el : "#content",
        template : _.template(tpl),
        initialize : function (params) {
            this.render();
            this.refreshIpaUser();
        },
        render : function (event) {
            var self = this;
            self.$el.html(self.template({}));

            var appCode = self.model.get("appCode");
            self.onSearch(appCode);

            $("#close").on("click", function() {
                window.parent.$.colorbox.close();
            });

            self.onSave(appCode);

            return self;
        },
        refreshIpaUser : function() {
            $.ajax({
                url : "/admin/ipa/users",
                method : "PUT",
                success: function(result, textStatus, xhr) {
                }, 
                error: function (xhr, textStatus, errorThrown) {
                }
            });
        },
        onSearch : function(appCode) {
            var self = this;
            var searchBox = $("input[name='username']");
            var timer;
            var url;
            if(appCode) {
                url = "/admin/ipa/app/" + appCode + "/users/";
            } else {
                url = "/admin/ipa/admin/users/";
            }
            searchBox.on("keyup", function() {
                clearTimeout(timer);
                timer = setTimeout(function() {
                    if(searchBox.val().length > 0) {
                        $.ajax({
                            url : url + searchBox.val(),
                            success: function(users, textStatus, xhr) {
                                self.handler(users);
                            }, 
                            error: function (xhr, textStatus, errorThrown) {
                            }
                        });
                    } else {
                        self.handler();
                    }
                }, 300);
            });
        },
        handler : function(users) {
            $("input[type='checkbox'][name='all']").prop("checked", false);
            var container = $("#result");
            container.html("");
            if(users) {
                for(var i = 0;i < users.length;i ++) {
                    var user = users[i];
                    var row = $('<tr><td><input type="checkbox" name="uid" /></td><td></td><td></td><td></td></tr>');
                    if(user.status === "selected") {
                        row.find("input").prop("checked", true);
                        row.find("input").attr("disabled", true);
                        row.find("td").eq(3).text("已添加");
                    }
                    row.data("user", user);
                    row.find("td").eq(1).text(user.name);
                    row.find("td").eq(2).text(user.email);
                    container.append(row);
                }
                this.onCheck();
            }
        },
        onCheck : function() {
            var allCheck = $("input[type='checkbox'][name='all']");
            var checkbox = $("input[type='checkbox'][name='uid']");
            allCheck.on("change", function() {
                var enabledCheckbox = checkbox.filter(function(index) {
                    return !checkbox.eq(index).attr("disabled")
                });
                if($(this).is(":checked")) {
                    enabledCheckbox.prop("checked", true);
                } else {
                    enabledCheckbox.prop("checked", false);
                }
            });
            checkbox.on("change", function() {
                if(!$(this).is(":checked")) {
                    allCheck.prop("checked", false);
                }
            });
        },
        onSave : function(appCode) {
            var url;
            if(appCode) {
                url = "/admin/app/" + appCode + "/ipa/users";
            } else {
                url = "/admin/ipa/admin/users";
            }
            $("#save").on("click", function() {
                var checkbox = $("input[type='checkbox'][name='uid']:checked");
                var enabledCheckbox = checkbox.filter(function(index) {
                    return !checkbox.eq(index).attr("disabled")
                });
                if(enabledCheckbox.size() > 0) {
                    var users = [];
                    $.each(enabledCheckbox, function(i, value) {
                        users.push($(value).closest("tr").data("user"));
                    });
                    $.ajax({
                        url : url,
                        type : "POST",
                        contentType : "application/json",
                        data : JSON.stringify(users),
                        success: function(result, textStatus, xhr) {
                            window.parent.$.colorbox.close();
                            window.parent.location.reload();
                        }, 
                        error: function (xhr, textStatus, errorThrown) {
                        }
                    });
                }
            });
        }
    });
});