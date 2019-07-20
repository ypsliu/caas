define(["text!app/popup/app-select/app-select-tpl.html"], function(tpl) {
    return Backbone.View.extend({
        el : "#content",
        template : _.template(tpl),
        initialize : function (params) {
            this.render();
        },
        render : function (event) {
            var self = this;
            self.$el.html(self.template({}));

            var adminCode = self.model.get("adminCode");

            $("#close").on("click", function() {
                window.parent.$.colorbox.close();
            });

            self.initApp(adminCode);

            self.onSave(adminCode);

            return self;
        },
        initApp : function(adminCode) {
            var self = this;

            $.when($.ajax("/admin/apps/confirmed"), $.ajax("/admin/" + adminCode + "/apps")).done(function(r1, r2) {
                var allApps = r1[0];
                var myApps = r2[0];
                var optionalApps = allApps.filter(function(element, index, array) {
                    for(var i = 0;i < myApps.length;i ++) {
                        if(element.code == myApps[i].code) {
                            return false;
                        }
                    }
                    return true;
                });
                self.handle(optionalApps, myApps);
            });
        },
        handle : function(optionalApps, myApps) {
            _render($("div.table").eq(0).find("tbody"), optionalApps);
            _render($("div.table").eq(1).find("tbody"), myApps);
            function _render(context, apps) {
                context.html("");
                for(var i = 0;i < apps.length;i ++) {
                    var app = apps[i];
                    var row = $('<tr><td><input type="checkbox" name="appCode" /></td><td></td><td></td></tr>');
                    row.data("appCode", app.code);
                    row.find("td").eq(1).text(app.name);
                    row.find("td").eq(2).text(app.appType);
                    context.append(row);
                }
            }
            this.bindCheckbox();
            this.onSelect();
        },
        bindCheckbox : function() {
            this.onCheck($("div.table").eq(0));
            this.onCheck($("div.table").eq(1));
        },
        onCheck : function(context) {
            var allCheck = context.find("input[type='checkbox'][name='all']");
            var checkbox = context.find("input[type='checkbox'][name='appCode']");
            allCheck.off("change");
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
            checkbox.off("change");
            checkbox.on("change", function() {
                if(!$(this).is(":checked")) {
                    allCheck.prop("checked", false);
                }
            });
        },
        onSelect : function() {
            var self = this;
            var left = $("div.table").eq(0).find("tbody");
            var right = $("div.table").eq(1).find("tbody");
            $("#select").on("click", function() {
                right.append(left.find("input[type='checkbox'][name='appCode']:checked").closest("tr"));
                $("input[type='checkbox']").prop("checked", false);
                self.bindCheckbox();
            });
            $("#unselect").on("click", function() {
                left.append(right.find("input[type='checkbox'][name='appCode']:checked").closest("tr"));
                $("input[type='checkbox']").prop("checked", false);
                self.bindCheckbox();
            });
        },
        onSave : function(adminCode) {
            $("#save").on("click", function() {
                var checkbox = $("div.table").eq(1).find("input[type='checkbox'][name='appCode']");
                var appCodes = [];
                $.each(checkbox, function(i, value) {
                    appCodes.push($(value).closest("tr").data("appCode"));
                });
                $.ajax({
                    url : "/admin/" + adminCode + "/apps",
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(appCodes),
                    success: function(result, textStatus, xhr) {
                        window.parent.$.colorbox.close();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            });
        }
    });
});