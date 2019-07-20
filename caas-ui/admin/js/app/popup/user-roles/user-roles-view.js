define(["text!app/popup/user-roles/user-roles-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el: "#content",
        template: _.template(tpl),
        initialize: function (params) {
            this.render();
        },
        render: function (event) {
            var self = this;
            self.$el.html(self.template({}));

            var appCode = self.model.get("appCode");
            var userCode = self.model.get("userCode");

            $("#close-btn").one("click", function() {
                window.parent.$.colorbox.close();
            });

            self.getUserRoles(userCode, function(userRoles) {
                self.userRoles = userRoles;
                self.subjectRoles = {};
                self.getSubject(appCode, function(subjects) {
                    self.renderDropdwon(subjects, function(subjectCode) {
                        self.saveTemporary();
                        self.currentSubjectCode = subjectCode;
                        var subjectRoles = self.subjectRoles[subjectCode];
                        $("#fold-btn").show();
                        $("#fold-btn").removeClass("unfold").addClass("fold");
                        if(subjectRoles) {
                            self.refreshTree(subjectRoles);
                        } else {
                            self.getSubjectRoles(appCode, subjectCode, function(subjectRoles) {
                                for(var i = 0;i < subjectRoles.length;i ++) {
                                    for(var j = 0;j < self.userRoles.length;j ++) {
                                        if(subjectRoles[i].code == self.userRoles[j].code) {
                                            subjectRoles[i].checked = true;
                                        }
                                    }
                                }
                                self.subjectRoles[subjectCode] = subjectRoles;
                                self.refreshTree(subjectRoles);
                            });
                        }
                    });
                });
            });

            $("#fold-btn").on("click", function() {
                if($(this).hasClass("fold")) {
                    $(this).removeClass("fold").addClass("unfold");
                    $("ul.roles-tree ul").slideDown("fast");
                    $("ul.roles-tree a.fold").removeClass("fold").addClass("unfold");
                } else {
                    $(this).removeClass("unfold").addClass("fold");
                    $("ul.roles-tree ul").slideUp("fast");
                    $("ul.roles-tree a.unfold").removeClass("unfold").addClass("fold");
                }
            });

            $("#save-btn").one("click", function() {
                self.onSave(userCode, function() {
                    window.parent.$.colorbox.close();
                });
            });

            return self;
        },
        getCurrentSubjectRoles : function() {
            if(this.currentSubjectCode) {
                return this.subjectRoles[this.currentSubjectCode];
            }
        },
        getSubject : function(appCode, callback) {
            $.ajax({
                url : "/admin/app/" + appCode + "/subject",
                success: function(subjects, textStatus, xhr) {
                    callback(subjects);
                }, 
                error: function (xhr, textStatus, errorThrown) {
                }
            });
        },
        renderDropdwon : function(subjects, onSelect) {
            var context = $("#subject");
            var label = context.find("a");
            var input = context.find("input");

            context.find("ul").html("");
            for(var i = 0;i < subjects.length;i ++) {
                var subject = subjects[i];
                var option = $('<li><a href="javascript:void(0)"></a></li>');
                option.find("a").text(subject.name);
                option.attr("data-value", subject.code);
                context.find("ul").append(option);
            }
            context.find("ul li").on("click", function() {
                label.text($(this).find("a").text());
                var subjectCode = $(this).attr("data-value");
                input.val(subjectCode);
                onSelect(subjectCode);
            });
        },
        getSubjectRoles : function(appCode, subjectCode, callback) {
            $.ajax({
                url : "/admin/app/" + appCode + "/subject/" + subjectCode + "/role",
                success: function(roles, textStatus, xhr) {
                    callback(roles);
                }, 
                error: function (xhr, textStatus, errorThrown) {
                }
            });
        },
        getUserRoles : function(userCode, callback) {
            $.ajax({
                url : "/admin/user/" + userCode + "/app/role",
                success: function(roles, textStatus, xhr) {
                    callback(roles);
                }, 
                error: function (xhr, textStatus, errorThrown) {
                }
            });
        },
        refreshTree : function(roles) {
            $("#tree > ul.roles-tree").html("");
            var roots = roles.filter(function(role, index, array) {
                return role.parent == "0";
            }).sort(function(role1, role2) {
                return role1.order - role2.order;
            });
            for(var i = 0;i < roots.length;i ++) {
                this.createNode("0", roots[i]);
                this.buildTree(roots[i].code, roles);
            }
            this.bindEventForTree();
        },
        bindEventForTree : function() {
            $("a[role='branch']").on("click", function() {
                if($(this).hasClass("fold")) {
                    $(this).parent().next().slideDown("fast");
                    $(this).removeClass("fold").addClass("unfold");
                } else {
                    $(this).parent().next().slideUp("fast");
                    $(this).removeClass("unfold").addClass("fold");
                }
            });
            $("input[type='checkbox'][name='code']").on("change", function() {
                var descendants = $(this).closest("li").find("input[type='checkbox']").not($(this));
                if($(this).is(":checked")) {
                    descendants.prop("checked", true);
                    descendants.attr("disabled", "disabled");
                } else {
                    descendants.prop("checked", false);
                    descendants.removeAttr("disabled");
                }
            });
        },
        saveTemporary : function() {
            var roles = this.getCurrentSubjectRoles();
            if(roles) {
                for(var i = 0;i < roles.length;i ++) {
                    var role = roles[i];
                    var checkbox = $("input[type='checkbox'][name='code'][value='" + role.code + "']");
                    if(!checkbox.attr("disabled") && checkbox.is(":checked")) {
                        role.checked = true;
                    } else {
                        role.checked = false;
                    }
                }
            }
        },
        onSave : function(userCode, callback) {
            var self = this;
            self.saveTemporary();
            var roles = [];
            for(var subjectCode in self.subjectRoles) {
                for(var i = 0;i < self.subjectRoles[subjectCode].length;i ++) {
                    var subjectRole = self.subjectRoles[subjectCode][i];
                    if(subjectRole.checked) {
                        roles.push({
                            userCode : userCode,
                            roleCode : subjectRole.code,
                            status : "CONFIRMED"
                        });
                    }
                }
            }

            var unchangedRoles = self.userRoles.filter(function(userRole, index, array) {
                var unchanged = true;
                for(var subjectCode in self.subjectRoles) {
                    if(userRole.subjectCode == subjectCode) {
                        unchanged = false;
                    }
                }
                return unchanged;
            });

            for(var i = 0;i < unchangedRoles.length;i ++) {
                roles.push({
                    userCode : userCode,
                    roleCode : unchangedRoles[i].code,
                    status : "CONFIRMED"
                });
            }

            $.ajax({
                url : "/admin/user/role",
                type : "PUT",
                contentType : "application/json",
                data : JSON.stringify(roles),
                success: function(result, textStatus, xhr) {
                    callback();
                }, 
                error: function (xhr, textStatus, errorThrown) {
                }
            });
        },
        buildTree : function(parentCode, roles) {
            var children = roles.filter(function(role, index, array) {
                return role.parent == parentCode;
            }).sort(function(role1, role2) {
                return role1.order - role2.order;
            });
            for(var i = 0;i < children.length;i ++) {
                var role = children[i];
                this.createNode(parentCode, role);
                this.buildTree(role.code, roles);
            }
        },
        createNode : function(parentCode, role) {
            var node = $('<li><div><span class="line"></span><label><input type="checkbox" name="code" /> <span></span></label></div></li>');
            var checkbox = node.find("input[type='checkbox']");
            node.find("label span").text(role.name);
            checkbox.attr("value", role.code);
            if(role.checked) {
                checkbox.prop("checked", true);
            }
            var parentNode = this.getNodeByCode(parentCode);
            var ul = parentNode.children("ul");
            if(ul.size() === 0) {
                ul = $("<ul></ul>");
                ul.hide();
                parentNode.append(ul);
                var branch = $('<a href="javascript:void(0)" class="fold" role="branch"></a>');
                parentNode.children("div").prepend(branch);
                branch.append(parentNode.children("div").children("span.line"));
            }
            if(parentNode.children("div").find("input[type='checkbox']").is(":checked")) {
                checkbox.attr("disabled", "disabled");
                checkbox.prop("checked", true);
            }
            ul.append(node);
        },
        getNodeByCode : function(code) {
            if(code == "0") {
                return $("#tree");
            } else {
                return $("input[type='checkbox'][name='code'][value='" + code + "']").closest("li");
            }
        }
    });
});