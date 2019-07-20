(function (factory) {
	if (typeof define === "function" && define.amd) {
		// AMD
		define("jquery.rtree", ["jquery"], factory);
	} else if (typeof exports === "object") {
		// CommonJS
		factory(require("jquery"));
	} else {
		// Browser globals
		factory(jQuery);
	}
}(function ($) {
    $["RTree"] = function() {
        var _httpClient = new HttpClient();
        var _user;
        var _appResources;
        var _appOperations;
        var _toolbar;
        var _subject;
        var _editor;
        var _loading = new Loading();
        var _overlayForm = new OverlayForm();

        function _init() {
            _loading.start();
            var context = new Context();
            context.start(new Starter(), function() {
                _loading.end();
                context.addFilter(new Filter(function() {
                    if(_user) {
                        return true;
                    } else {
                        $.overlay({
                            id : "not-login",
                            close : "disabled"
                        });
                        return false;
                    }
                }));
                context.addFilter(new Filter(function() {
                    if(_user.appCode != undefined) {
                        return true;
                    } else {
                        $("#no-app").find("a.btn").attr("href", " /#admin/app/apply/" + _user.code);
                        $.overlay({
                            id : "no-app",
                            close : "disabled"
                        });
                        return false;
                    }
                }));
                context.filter(function() {
                    _prepare(function() {
                        _toolbar = new Toolbar();
                        _subject = new Subject();
                        _editor = new Editor();
                    });
                });
            });
        }

        function _prepare(callback) {
            if(_user.appCode != undefined) {
                $.async.parallel({
                    resources : function(notice) {
                        _httpClient.getResources(_user.appCode, function(resources) {
                            _appResources = resources;
                            notice();
                        });
                    },
                    operations : function(notice) {
                        _httpClient.getOperations(_user.appCode, function(operations) {
                            _appOperations = operations;
                            notice();
                        });
                    }
                }, function() {
                    $("input[name='appCode']").val(_user.appCode);
                    callback();
                });
            } else {
                callback();
            }
        }

        function _globalValidateRoleName(roleName, callback) {
            if(roleName.length === 0) {
                return;
            }
            var roleNames = [];
            $.each($("div.main div.view:visible a.node-text span"), function(i, value) {
                roleNames.push($(value).text());
            });
            if(roleNames.indexOf(roleName) < 0) {
                _httpClient.validateRoleName(_user.appCode, roleName, function(success) {
                    callback(success);
                });
            } else {
                callback(false);
            }
        }

        (function() {
            _mainHeight();
            _sideBarHeight()
            $(window).on("resize", function() {
                _mainHeight();
                _sideBarHeight();
            });

            $("#outline-btn").on("click", function() {
                _onToggleSideBar();
            });

            if(!$.browser.msie || $.browser.version > 10) {
                $(window).on("beforeunload", function(){ 
                    return "";
                });
            }

            if($.browser.msie && $.browser.version >= 10) {
                $("body").append($('<link rel="stylesheet" type="text/css" href="css/ie.css" />'));
            }

            function _mainHeight() {
                var height = $(window).height() - 84 - 30;
                $("div.main").css("height", height);
            }

            function _sideBarHeight() {
                 var height = $(window).height() - 84;
                $("div.side-bar").css("height", height);
            }

            function _onToggleSideBar() {
                if($("div.side-bar").css("left") !== "0px") {
                    $("div.side-bar").css("left", 0);
                    $("div.workboard").css("margin-left", "280px");
                    $("#outline-btn").attr("title", "隐藏左侧菜单");
                } else {
                    $("div.side-bar").css("left", "-280px");
                    $("div.workboard").css("margin-left", 0);
                    setTimeout(function() {
                        $("#outline-btn").attr("title", "显示左侧菜单");
                    }, 500);
                }
            }

            function _createOverlay(id, options, opener) {
                var content = $($("#" + id).html());
                var formId = new Date().getTime();
                content.attr("id", formId);
                var overlay = $("<div class='overlay'></div>");
                overlay.append(content);
                if(options.close === undefined || options.close === "enabled") {
                    content.prepend($('<a href="javascript:void(0)" class="close-btn"><span class="glyphicon glyphicon-remove"></span></a>'));
                }

                $("body").append(overlay);

                _locateForm();
                $(window).on("resize.overlay", function() {
                    _locateForm();
                });

                function _locateForm() {
                    content.css("left", $(window).width() / 2 - content.width() / 2);
                    content.css("top", $(window).height() / 2 - content.height() / 2);
                }

                function _close() {
                    $("div.overlay").remove();
                    $(window).off("resize.overlay");
                    $("body").off("keyup.overlay");
                    if(options.onClose) {
                        options.onClose(opener);
                    }
                }

                if(opener) {
                    $.fn.overlay = {
                        close : function() {
                            _close();
                        }
                    }
                }
                
                content.find("a.close-btn").on("click", function() {
                    _close();
                });

                // $("body").on("keyup.overlay", function(event) {
                //     if(event.keyCode === 27) {
                //         _close();
                //     }
                // });

                // overlay.on("click.overlay", function(event) {
                //     _close();
                // });

                content.on("click", function(event) {
                    event.stopPropagation();
                });

                if(options.onComplete) {
                    options.onComplete(content, opener);
                }

                return {
                    close : _close
                }
            }

            $.overlay = function(options) {
                return _createOverlay(options.id, options);
            }

            $.fn.overlay = function(options) {
                $(this).on("click", function(event) {
                    event.preventDefault();
                    var opener = $(this);
                    var id = opener.attr("href").substring(1);

                    _createOverlay(id, options, opener);
                });
            }

            $.fn.dropdownMenu = function(options) {
                var self = $(this);
                var link = self.children("a");
                var dropdown = self.find("ul.dropdown-list");
                link.on("click", function(event) {
                    event.stopPropagation();
                    if(dropdown.is(":hidden")) {
                        $("ul.dropdown-list").hide();
                        dropdown.show();
                        $("body, div.view, svg").one("click", function(event) {
                            if(dropdown.is(":visible")) {
                                dropdown.hide();
                            }
                        });
                    } else {
                        dropdown.hide();
                        $("body, div.view, svg").off("click");
                    }
                });

                _bindSelectItem(dropdown.find("li"))

                function _bindSelectItem(item) {
                    item.on("click", function(event) {
                        event.stopPropagation();
                        if($(this).hasClass("disabled")) {
                            return;
                        }
                        dropdown.hide();
                        if(options.onSelect) {
                            options.onSelect($(this).find("a").attr("data-value"));
                        }
                    });
                }

                function _addItem(obj) {
                    var item = $('<li><a href="javascript:void(0)"></a></li>');
                    item.find("a").attr("data-value", obj.value);
                    item.find("a").text(obj.label);
                    dropdown.append(item);
                    _bindSelectItem(item);
                }

                return {
                    addItem : _addItem,
                }
            }

            $.async = {};
            $.async.parallel = function(options, callback) {
                if(!Object.keys) {
                    Object.keys = function(obj) {
                        var keys = [];
                        for(var key in obj) {
                            keys.push(key);
                        }
                        return keys;
                    }
                }
                var tasks = Object.keys(options);
                var completed = 0;
                function _notice() {
                    if(++completed === tasks.length) {
                        callback(result);
                    }
                }
                
                var result = {};
                for(var task in options) {
                    options[task].call(task, function(value) {
                        result[task] = value;
                        _notice();
                    });
                }
            }

            $.fn.validate = function() {
                var valid = true;
                function _validateGroup(group) {
                    var input = $(group).find("input");
                    var ruleObjs = $(group).find(".error-msg[validation]");
                    $.each(ruleObjs, function(j, ruleObj) {
                        var rule = $(ruleObj).attr("validation");
                        var parts = rule.split(", ");
                        var method = parts[0];
                        var params = [];
                        if(parts.length > 1) {
                            params = parts.slice(1);
                        }
                        var targetInput = input;
                        if($(ruleObj).attr("for")) {
                            targetInput = $(group).find("input[name='" + $(ruleObj).attr("for") + "']");
                        }
                        if(!$(ruleObj).attr("ignore-blank") || targetInput.val().length > 0) {
                            if(!targetInput[method].apply(targetInput, params)) {
                                valid = false;
                                $(group).addClass("invalid");
                                $(group).find(".error-msg").hide();
                                $(ruleObj).show();
                                $(group).find("span.glyphicon-exclamation-sign").show();
                                return false;
                            }
                        }
                    });
                }

                if($(this)[0].tagName.toLowerCase() === "form") {
                    var form  = $(this);
                    form.find("div.input-group").removeClass("invalid");
                    form.find("div.input-group .error-msg").hide();
                    form.find("span.glyphicon-exclamation-sign").hide();
                    $.each(form.find("div.input-group:visible"), function(i, group) {
                        _validateGroup(group);
                    });
                } else if($(this)[0].tagName.toLowerCase() === "div" && $(this).hasClass("input-group")) {
                    var group = $(this);
                    group.removeClass("invalid");
                    group.removeClass("ok");
                    group.removeClass("error");
                    group.find(".error-msg").hide();
                    group.find("span.glyphicon-exclamation-sign").hide();
                    _validateGroup(group);
                }
                return valid;
            }

            $.toaster = function(message, id) {
                if(id && $("#" + id).size() > 0) {
                    return;
                }
                var toaster = $($("#toaster").get(0).outerHTML);
                toaster.removeAttr("id");
                if(id) {
                    toaster.attr("id", id);
                }
                toaster.find("p").text(message);
                $("body").append(toaster);
                toaster.show();
                toaster.css("left", $(window).width() / 2 - toaster.width() / 2);
                setTimeout(function() {
                    var opacity = 1;
                    var timer = setInterval(function() {
                        opacity = opacity - 0.1;
                        toaster.css("opacity", opacity).css("filter", "alpha(opacity=" + opacity * 100 + ")");
                        if(opacity < 0) {
                            clearInterval(timer);
                            toaster.remove();
                        }
                    }, 50);
                }, 2000);
            }

            $.fn.outerHTML = function() {
                return $(this).clone().wrap('<div></div>').parent().html();
            };
        })(); 

        function Loading() {
            var _overlay;
            function _start() {
                _overlay = $.overlay({
                    id : "loading",
                    close : "disabled"
                });
            }

            function _end() {
                if(_overlay) {
                    _overlay.close();
                    _overlay = null;
                }
            }

            return {
                start : _start,
                end : _end
            }
        }

        function OverlayForm() {
            var _overlay;
            function _open(id, initialize, onSave) {
                _overlay = $.overlay({
                    id : id,
                    onComplete : function(content) {
                        var form = content.find("form");
                        initialize(form);

                        content.find("button[role='cancel']").on("click", function() {
                            _overlay.close();
                        });

                        content.find("input").on("keyup", function(event) {
                            if(event.keyCode === 13 || event.keyCode === 110) {
                                onSave(form);
                            }
                        });

                        content.find("button[role='save']").on("click", function() {
                            onSave(form);
                        });
                    }
                });
            }

            function _close() {
                _overlay.close();
            }

            return {
                open : _open,
                close : _close
            }
        }

        function HttpClient() {
            (function() {
                $.ajaxSetup({
                    beforeSend: function(jqXHR, settings) {
                        settings.url = "/api/v1" + settings.url;
                        if($.browser.msie) {
                            if(settings.url.indexOf("?") > 0) {
                                settings.url = settings.url + "&";
                            } else {
                                settings.url = settings.url + "?";
                            }
                            settings.url = settings.url + "timestamp=" + new Date().getTime()
                        }
                    },
                    complete : function(jqXHR, textStatus) {
                    },
                    error: function (jqXHR, textStatus, errorThrown) {
                    }
                });
            })();

            function _getUser(callback) {
                $.ajax({
                    url : "/admin/me",
                    success: function(adminUser, textStatus, xhr) {
                        if(!adminUser.superUser) {
                            _user = adminUser;
                            callback(adminUser);
                        } else {
                            callback();
                        }
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                        callback();
                    }
                });
            }

            function _getResources(appCode, callback) {
                $.ajax({
                    url : "/admin/app/" + appCode + "/resource",
                    success: function(resources, textStatus, xhr) {
                        callback(resources);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getResource(code, callback) {
                $.ajax({
                    url : "/admin/resource/" + code,
                    success: function(resource, textStatus, xhr) {
                        callback(resource);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _createResource(resource, callback) {
                $.ajax({
                    url : "/admin/resource",
                    type : "POST",
                    contentType : "application/json",
                    data : JSON.stringify(resource),
                    success: function(result, textStatus, xhr) {
                        callback(result);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateResource(resource, callback) {
                $.ajax({
                    url : "/admin/resource/" + resource.code,
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(resource),
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getRoleResources(roleCode, callback) {
                $.ajax({
                    url : "/admin/role/" + roleCode + "/resource",
                    success: function(resources, textStatus, xhr) {
                        callback(resources);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getOperations(appCode, callback) {
                $.ajax({
                    url : "/admin/app/" + appCode + "/operation",
                    success: function(operations, textStatus, xhr) {
                        callback(operations);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getOperation(code, callback) {
                $.ajax({
                    url : "/admin/operation/" + code,
                    success: function(operation, textStatus, xhr) {
                        callback(operation);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _createOperation(operation, callback) {
                $.ajax({
                    url : "/admin/operation",
                    type : "POST",
                    contentType : "application/json",
                    data : JSON.stringify(operation),
                    success: function(result, textStatus, xhr) {
                        callback(result);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateOperation(operation, callback) {
                $.ajax({
                    url : "/admin/operation/" + operation.code,
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(operation),
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getSubjects(appCode, callback) {
                $.ajax({
                    url : "/admin/app/" + appCode + "/subject",
                    success: function(subjects, textStatus, xhr) {
                        callback(subjects);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getSubject(code, callback) {
                $.ajax({
                    url : "/admin/subject/" + code,
                    success: function(subject, textStatus, xhr) {
                        callback(subject);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getRoleTree(subjectCode, callback) {
                $.ajax({
                    url : "/admin/subject/" + subjectCode + "/roleTree",
                    success: function(roleTree, textStatus, xhr) {
                        callback(roleTree);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateRoleTree(subjectCode, roleTree, callback) {
                 $.ajax({
                    url : "/admin/subject/" + subjectCode + "/roleTree",
                    type : "PUT",
                    contentType : "text/plain",
                    data : roleTree,
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateSubjectRoles(subjectRoles, callback) {
                $.ajax({
                    url : "/admin/subject/roles",
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(subjectRoles),
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _createSubject(subject, callback) {
                $.ajax({
                    url : "/admin/subject",
                    type : "POST",
                    contentType : "application/json",
                    data : JSON.stringify(subject),
                    success: function(result, textStatus, xhr) {
                        callback(result);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateSubject(subject, callback) {
                $.ajax({
                    url : "/admin/subject/" + subject.code,
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(subject),
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _validateSubjectName(appCode, subjectName, callback) {
                $.ajax({
                    url : "/validation/app/" + appCode + "/subject/name/" + subjectName,
                    success: function(result, textStatus, xhr) {
                        callback(result.success);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _validateOperationName(appCode, operationName, callback) {
                $.ajax({
                    url : "/validation/app/" + appCode + "/operation/name/" + operationName,
                    success: function(result, textStatus, xhr) {
                        callback(result.success);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _validateRoleName(appCode, roleName, callback) {
                $.ajax({
                    url : "/validation/app/" + appCode + "/role/name/" + roleName,
                    success: function(result, textStatus, xhr) {
                        callback(result.success);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _getRolesBySubject(appCode, subjectCode, callback) {
                $.ajax({
                    url : "/admin/app/" + appCode + "/subject/" + subjectCode + "/role",
                    success: function(roles, textStatus, xhr) {
                        callback(roles);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _updateRoleOrder(roles, callback) {
                $.ajax({
                    url : "/admin/role/order",
                    type : "PUT",
                    contentType : "application/json",
                    data : JSON.stringify(roles),
                    success: function(result, textStatus, xhr) {
                        callback();
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                    }
                });
            }

            function _lockSubject(subjectCode, onSuccess, onError) {
                $.ajax({
                    url : "/admin/lock/subject/" + subjectCode,
                    success: function(result, textStatus, xhr) {
                        onSuccess(result);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                        onError();
                    }
                });
            }

            function _unlockSubject(subjectCode, callback) {
                $.ajax({
                    url : "/admin/unlock/subject/" + subjectCode,
                    success: function(result, textStatus, xhr) {
                        callback(result);
                    }, 
                    error: function (xhr, textStatus, errorThrown) {
                        onError();
                    }
                });
            }

            return {
                getUser : _getUser,
                getResource : _getResource,
                getResources : _getResources,
                updateResource : _updateResource,
                getRoleResources : _getRoleResources,
                createResource : _createResource,
                getOperation : _getOperation,
                getOperations : _getOperations,
                createOperation : _createOperation,
                updateOperation : _updateOperation,
                getSubject : _getSubject,
                getSubjects : _getSubjects,
                createSubject : _createSubject,
                updateSubject : _updateSubject,
                validateSubjectName : _validateSubjectName,
                validateOperationName : _validateOperationName,
                validateRoleName : _validateRoleName,
                getRolesBySubject : _getRolesBySubject,
                getRoleTree : _getRoleTree,
                updateRoleTree : _updateRoleTree,
                updateSubjectRoles : _updateSubjectRoles,
                updateRoleOrder : _updateRoleOrder,
                lockSubject : _lockSubject,
                unlockSubject : _unlockSubject
            }
        }

        function Starter() {
            return function(callback) {
                _httpClient.getUser(function() {
                    callback();
                });
            }
        }

        function Context() {
            var _firstFilter;
            var _lastFilter
            function _start(starter, callback) {
                starter(callback);
            }

            function _addFilter(filter) {
                if(_lastFilter) {
                    _lastFilter.setNext(filter);
                } else {
                    _firstFilter = filter;
                }
                _lastFilter = filter;
            }

            function _doFilter(callback) {
                _firstFilter.doFilter(callback);
            }

            return {
                start : _start,
                addFilter : _addFilter,
                filter : _doFilter
            }
        }

        function Filter(func, next) {
            var _next;
            function _setNext(next) {
                _next = next;
            }

            function _do(callback) {
                if(func()) {
                    if(_next) {
                        _next.doFilter(callback);
                    } else {
                        callback();
                    }
                }
            }
            return {
                doFilter : _do,
                setNext : _setNext
            }
        }

        function Subject() {
            var _subjects = {};
            var _current;
            var _dropdownContext = $("ul.menu > li").eq(0).find("div.dropdown");
            var _dropdownList = _dropdownContext.find("ul.dropdown-list");

            if(navigator.appVersion.indexOf("Mac") > 0) {
                $.each(_dropdownList.find("li a"), function(i, value) {
                    var type = $(value).attr("data-value");
                    var span = $(value).find("span");
                    if(type === "save-subject") {
                        span.text("⌘S");
                    }
                });
            }

            var _dropdownMenu = _dropdownContext.dropdownMenu({
                onSelect : function(value) {
                    if(value === "new-subject") {
                        _onAddSubject();
                    } else if(value === "save-subject") {
                        _onSaveSubject();
                    } else if(value) {
                        _switchSubject(value);
                    }
                }
            });
            _initSubjects();

            $("#main-save-btn").on("click", function() {
                _onSaveSubject();
            });

            $("#end-btn").on("click", function() {
                _httpClient.unlockSubject(_current.obj.code, function() {
                    _end();
                });
            });

            $(document).on("keydown", function(event) {
                if((event.ctrlKey || event.metaKey) && event.keyCode === 83) {
                    event.stopPropagation();
                    event.preventDefault();
                    if(_canSave()) {
                        _onSaveSubject();
                    }
                }
            });

            function _initSubjects() {
                _httpClient.getSubjects(_user.appCode, function(subjects) {
                    if(subjects.length > 0) {
                        for(var i = 0;i < subjects.length;i ++) {
                            _create(subjects[i]);
                        }
                        //_switchSubject(subjects[0].code);
                    }
                });
            }

            function _end() {
                _toolbar.hide();
                _toolbar.clearDataChange(_current.obj.code);
                $("div.side-bar ul.tree-menu").hide();
                $("div.main div.view").hide();
                $("#end-btn").parent().hide();
                _disableSave();
                _current.lock = undefined;
            }

            function _showLockError() {
                var overlay = $.overlay({
                    id : "lock-error",
                    close : "disabled",
                    onComplete : function(content) {
                        content.find("a").on("click", function() {
                            overlay.close();
                        });
                    }
                });
            }

            function _onSaveSubject() {
                if(_current.lock === "readonly") {
                    var overlay = $.overlay({
                        id : "refresh",
                        close : "disabled",
                        onComplete : function(content) {
                            content.find("a[role='refresh']").on("click", function() {
                                window.location.reload();
                            });
                            content.find("a[role='close']").on("click", function() {
                                overlay.close();
                            });
                        }
                    });
                } else {
                    _httpClient.lockSubject(_current.obj.code, function(result) {
                        if(!result) {
                            _save();
                        } else if(!result.success) {
                            _showLocked();
                        }
                    }, function() {
                        _showLockError();
                    });
                }

                function _showLocked() {
                    var overlay = $.overlay({
                        id : "locked",
                        close : "disabled",
                        onComplete : function(content) {
                            content.find("a").on("click", function() {
                                overlay.close();
                            });
                        }
                    });
                }

                function _save() {
                    var rawRoles = _toolbar.getData(_current.obj.code).roles;
                    var roles = [];
                    var roleCodes = [];
                    var valid = true;
                    _current.tree.obj.clearError();

                    if(_current.menu.context.find("input.invalid").size() > 0
                        || _current.tree.context.find("a.node-text.invalid").size() > 0
                        || $("input[name='roleName'].invalid").size() > 0) {
                        return;
                    }

                    for(var i = 0;i < rawRoles.length;i ++) {
                        var rawRole = rawRoles[i];
                        if(rawRole.changeType && rawRole.changeType.length > 0) {
                            if(rawRole.changeType.indexOf("DELETE_ROLE") >= 0) {
                                roles.push({
                                    code : rawRole.code,
                                    name : rawRole.name,
                                    appCode : _user.appCode,
                                    subjectCode : _current.obj.code,
                                    parent : -1
                                });
                                roleCodes.push(rawRole.code);
                            } else {
                                rawRole.name = _current.menu.obj.getNameByCode(rawRole.code);
                                if(rawRole.name.length === 0) {
                                    valid = false;
                                    _current.tree.obj.invalidNode(rawRole.code);
                                    break;
                                }
                                var role = {
                                    code : rawRole.code,
                                    name : rawRole.name,
                                    parent : rawRole.parent,
                                    roleType : rawRole.roleType,
                                    appCode : _user.appCode,
                                    subjectCode : _current.obj.code,
                                    order : _current.menu.obj.getOrderByCode(rawRole.code)
                                };

                                if(rawRole.changeType.indexOf("NEW") >= 0 
                                    || rawRole.changeType.indexOf("CHANGE_PERMISSION") >= 0) {
                                    role.resources = [];
                                    for(var j = 0;j < rawRole.resources.length;j ++) {
                                        var rawResource = rawRole.resources[j];
                                        for(var k = 0; k < rawResource.operations.length;k ++) {
                                            var rawOperation = rawResource.operations[k];
                                            role.resources.push({
                                                code : rawResource.code,
                                                operationCode : rawOperation.code,
                                                appCode : _user.appCode
                                            });
                                        }
                                    }
                                }
                                roles.push(role);
                                roleCodes.push(rawRole.code);
                            }
                        }
                    }

                    if(valid) {
                        var roleOrders = _current.menu.obj.getOrderChangedData().filter(function(roleOrder, index, array) {
                            return roleCodes.indexOf(roleOrder.code) < 0;
                        });
                        if(roleOrders.length > 0 || roles.length > 0) {
                            _loading.start();
                            $.async.parallel({
                                orders : function(notice) {
                                    _httpClient.updateRoleOrder(roleOrders, function() {
                                        notice();
                                    });
                                },
                                roles : function(notice) {
                                    var subjecRoles = {
                                        code : _current.obj.code,
                                        appCode : _user.appCode,
                                        roleTree : _getCurrentTreeContent(),
                                        roles : roles
                                    };
                                    _httpClient.updateSubjectRoles(subjecRoles, function() {
                                        notice();
                                    });
                                }
                            }, function() {
                                console.log("save success");
                                _toolbar.clearDataChange(_current.obj.code);
                                _disableSave();
                                delete _current["changed"];
                                _loading.end();
                                _reload(_current.obj.code);
                                _editor.clearStack();
                            });
                        } else {
                            $.toaster("没有任何修改！", "no-changes");
                            console.log("no changes");
                        }
                    } else {
                        $.toaster("角色名称不能为空！", "blank-role-name");
                        console.log("invalid");
                    }
                }
            }

            function _onAddSubject() {
                _overlayForm.open("subject", function(form) {
                    var subjectName = form.find("input[name='name']");
                    var group = subjectName.closest("div.input-group");

                    subjectName.on("change", function() {
                        _onChange($(this));
                    });

                    function _onChange(input) {
                        group.removeClass("invalid");
                        group.find(".glyphicon").hide();
                        group.find(".error-msg").hide();

                        if(input.val().length > 0) {
                            if(input.val() == input.data("originName")) {
                                group.find(".glyphicon-ok").show();
                            } else {
                                group.find(".checking").show();
                                _httpClient.validateSubjectName(_user.appCode, input.val(), function(success) {
                                    group.find(".checking").hide();
                                    if(success) {
                                        group.find(".glyphicon-ok").show();
                                    } else {
                                        group.addClass("invalid");
                                        group.find(".glyphicon-exclamation-sign").show();
                                        group.find(".error-msg[verify]").show();
                                    }
                                });
                            }
                        }
                    }
                }, function(form) {
                    if(form.find(".error-msg[verify]:visible").size() === 0 && form.validate()) {
                        _httpClient.createSubject(form.serializeObject(), function(subject) {
                            _create(subject);
                            _overlayForm.close();
                            _switchSubject(subject.code);
                        });
                    }
                });
            } 

            function _create(subject) {
                var treeContext = $('<div class="view"></div>');
                $("div.main").append(treeContext);
                var tree = new Tree(treeContext);

                var menuContext = $('<ul class="tree-menu"></ul>');
                $("div.side-bar").append(menuContext);
                var menu = new Menu(menuContext);
                tree.setMenu(menu);
                menu.setTree(tree);

                treeContext.hide();
                menuContext.hide();

                _subjects[subject.code] = {
                    obj : subject,
                    tree : {
                        context : treeContext,
                        obj : tree
                    },
                    menu : {
                        context : menuContext,
                        obj : menu
                    },
                    loaded : false
                }

                _dropdownMenu.addItem({
                    value : subject.code,
                    label : subject.name
                });
            }

            function _reload(code) {
                _subjects[code].loaded = false;
                _switchSubject(code);
            }

            function _switchSubject(code) {
                var subject = _subjects[code];
                $("div.view").hide();
                $("ul.tree-menu").hide();
                subject.tree.context.show();
                subject.menu.context.show();
                $("#current-subject").text(subject.obj.name);
                $("#current-subject").data("code", subject.obj.code);
                _current = subject;

                if(!subject.loaded) {
                    $.async.parallel({
                        roles : function(notice) {
                            _httpClient.getRolesBySubject(_user.appCode, code, function(roles) {
                                subject.roles = roles;
                                notice(); 
                            });
                        },
                        roleTree : function(notice) {
                            _httpClient.getRoleTree(code, function(roleTree) {
                                subject.roleTree = roleTree;
                                notice();
                            });
                        }
                    }, function() {
                        subject.loaded = true;
                        if(!subject.roles || subject.roles.length === 0) {
                            subject.tree.obj.initialize($('<svg width="1000" height="500" role="canvas"></svg>'));
                            var id = subject.tree.obj.createRootNode(subject.obj.name);
                            subject.menu.obj.createRoot(subject.obj.name, id);
                        } else {
                            _refresh(subject.roleTree, subject.roles);
                        }
                        _editor.push();
                    });
                } else {
                    subject.tree.obj.focus();
                }
                _editor.onSwitch();

                _disableSave();
                $("#end-btn").parent().hide();
                if(!subject.lock) {
                    _startEdit();
                } else if(subject.lock === "locked") {
                    $("#end-btn").parent().show();
                    if(_current["changed"]) {
                        _enableSave();
                    }
                }

                function _startEdit() {
                    var overlay = $.overlay({
                        id : "start-edit",
                        close : "disabled",
                        onComplete : function(content) {
                            content.find("h3").text("当前主题：" + subject.obj.name);
                            content.find("a[role='edit']").on("click", function() {
                                _httpClient.lockSubject(subject.obj.code, function(result) {
                                    overlay.close();
                                    if(!result) {
                                        subject.lock = "locked";
                                        $("#end-btn").parent().show();
                                    } else if(!result.success) {
                                        subject.lock = "failed";
                                        _lockFailed();
                                    }
                                }, function() {
                                    _showLockError();
                                });
                            });
                            content.find("a[role='view']").on("click", function() {
                                subject.lock = "readonly";
                                overlay.close();
                            });
                        }
                    });
                }

                function _lockFailed() {
                    var overlay = $.overlay({
                        id : "lock-failed",
                        close : "disabled",
                        onComplete : function(content) {
                            content.find("a").on("click", function() {
                                subject.lock = "readonly";
                                overlay.close();
                            });
                        }
                    });
                }
            }

            function _refresh(roleTree, roles) {
                var subject = _current;
                var svg = $(roleTree);
                svg.find("svg[root]").data("subject", subject.obj);
                $.each(svg.find("svg[code]"), function(i, node) {
                    for(var j = 0;j < roles.length;j ++) {
                        var role = roles[j];
                        if(role.code == $(node).attr("code")) {
                            $(node).data("role", role);
                        }
                    }
                });
                subject.tree.context.html("");
                subject.tree.obj.initialize(svg);

                subject.menu.context.html("");
                subject.menu.obj.createRoot(subject.obj.name, subject.obj.code);
                subject.menu.obj.initialize(roles);

                subject.tree.obj.focus();
            }

            function _enableSave() {
                if(_current.lock === "locked") {
                    $("li[role='save']").removeClass("disabled");
                    $("#main-save-btn").removeAttr("disabled");
                    _current["changed"] = true;
                }
            }

            function _disableSave() {
                $("li[role='save']").addClass("disabled");
                $("#main-save-btn").attr("disabled", "true");
            }

            function _canSave() {
                return !$("#main-save-btn").attr("disabled");
            }

            function _getCurrentTreeContent() {
                var tree = _current.tree.context.children("svg").outerHTML();
                tree = tree.replace(new RegExp('xmlns="http://www.w3.org/2000/svg"', "gm"), "");
                return tree;
            }

            return {
                currentSubject : function() {
                    return _current;
                },
                enableSave : _enableSave,
                disableSave : _disableSave,
                canSave : _canSave,
                currentTree : _getCurrentTreeContent,
                refresh : _refresh
            }
        }

        function Editor() {
            var _undoStack = {};
            var _redoStack = {};
            var _dropdownContext = $("ul.menu > li").eq(1).find("div.dropdown");
            var _dropdownList = _dropdownContext.find("ul.dropdown-list");

            if(navigator.appVersion.indexOf("Mac") > 0) {
                $.each(_dropdownList.find("li a"), function(i, value) {
                    var type = $(value).attr("data-value");
                    var span = $(value).find("span");
                    if(type === "undo") {
                        span.text("⌘Z");
                    } else if(type === "redo") {
                        span.text("⌘Y");
                    } else if(type === "remove") {
                        span.text("⌘D");
                    } else if(type === "insert-above") {
                        span.text("⌘A");
                    } else if(type === "insert-below") {
                        span.text("⌘B");
                    } else if(type === "insert") {
                        span.text("⌘I");
                    }
                });
            }

            var _dropdownMenu = _dropdownContext.dropdownMenu({
                onSelect : function(value) {
                    if(value === "undo") {
                        _undo();
                    } else if(value === "redo") {
                        _redo();
                    } else if(value === "remove") {
                        _handleMenu("remove", true);
                    } else if(value === "insert-above") {
                        _handleMenu("insertSiblingAbove");
                    } else if(value === "insert-below") {
                        _handleMenu("insertSiblingBelow");
                    } else if(value === "insert") {
                        _handleMenu("insertChild");
                    }
                }
            });

            $(document).on("keydown", function(event) {
                if(event.ctrlKey || event.metaKey) {
                    if(event.keyCode === 89) {
                        event.stopPropagation();
                        event.preventDefault();
                        _redo();
                    } else if(event.keyCode === 90) {
                        event.stopPropagation();
                        event.preventDefault();
                        _undo();
                    } else if(event.keyCode === 73) {
                        event.stopPropagation();
                        event.preventDefault();
                        _handleMenu("insertChild");
                    } else if(event.keyCode === 65) {
                        event.stopPropagation();
                        event.preventDefault();
                        _handleMenu("insertSiblingAbove");
                    } else if(event.keyCode === 66) {
                        event.stopPropagation();
                        event.preventDefault();
                        _handleMenu("insertSiblingBelow");
                    } else if(event.keyCode === 68) {
                        event.stopPropagation();
                        event.preventDefault();
                        _handleMenu("remove", true);
                    }
                }
            });

            function _handleMenu(func, cascade) {
                var subject = _subject.currentSubject();
                if(subject) {
                    var menu = subject.menu.obj;
                    var selected = menu.getSelectedMenu();
                    if(selected.size() > 0) {
                        if(cascade != undefined) {
                            if(menu.childrenSize(selected) === 0) {
                                menu[func](selected, cascade);
                            }
                        } else {
                            menu[func](selected);
                        }
                    }
                } 
            }

            function _undo() {
                var undos = _undos();
                if(undos.length > 1) {
                    _redos().push(undos[undos.length - 1]);
                    undos.pop();
                    _do(undos[undos.length - 1]);
                    _enableRedo();
                    if(undos.length === 1) {
                        _disableUndo();
                    }
                }
            }

            function _do(things) {
                var subjectData = _subject.currentSubject();
                _toolbar.setData(subjectData.obj.code, $.extend(true, {}, things.data));
                var changedRoles = things.data.roles;
                var originRoles = subjectData.roles.filter(function(role, index, array) {
                    var changed = false;
                    for(var i = 0;i < changedRoles.length;i ++) {
                        if(changedRoles[i].code == role.code) {
                            changed = true;
                        }
                    }
                    return !changed;
                });
                originRoles = originRoles.concat(changedRoles);
                _subject.refresh(things.tree, originRoles);
            }

            function _redo() {
                var redos = _redos();
                if(redos.length > 0) {
                    _do(redos[redos.length - 1]);
                    redos.pop();
                    _push(true);
                    if(redos.length === 0) {
                        _disableRedo();
                    }
                }
            }

            function _push(redo) {
                var tree = _subject.currentSubject().tree.obj;
                if(tree.getSelectedNode()) {
                    var size = tree.childrenSize(tree.getSelectedNode());
                    _disableRemove(size !== 0);
                }

                var undos = _undos();
                if(undos.length === 0) {
                    _doPush();
                } else {
                    var top = undos[undos.length - 1];
                    var newTree = _subject.currentTree();
                    var newData = _toolbar.getData(_subjectCode());
                    if(newTree != top.tree || JSON.stringify(top.data) != JSON.stringify(newData)) {
                        _doPush();
                    }
                }

                function _doPush() {
                    undos.push({
                        tree : _subject.currentTree(),
                        data : $.extend(true, {}, _toolbar.getData(_subjectCode()))
                    });
                    if(!redo) {
                        var redos = _redos();
                        redos = [];
                        _disableRedo();
                    }
                    if(undos.length > 1) {
                        _enableUndo();
                    }
                }
            }

            function _disableUndo() {
                var li = _dropdownList.find("a[data-value='undo']").parent();
                if(!li.hasClass("disabled")) {
                    _dropdownList.find("a[data-value='undo']").parent().addClass("disabled");
                }
            }

            function _enableUndo() {
                _dropdownList.find("a[data-value='undo']").parent().removeClass("disabled");
            }

            function _isUndoEnabled() {
                return !_dropdownList.find("a[data-value='undo']").parent().hasClass("disabled");
            }

            function _disableRedo() {
                var li = _dropdownList.find("a[data-value='redo']").parent();
                if(!li.hasClass("disabled")) {
                    _dropdownList.find("a[data-value='redo']").parent().addClass("disabled");
                }
            }

            function _enableRedo() {
                _dropdownList.find("a[data-value='redo']").parent().removeClass("disabled");
            }

            function _isRedoEnabled() {
                return !_dropdownList.find("a[data-value='redo']").parent().hasClass("disabled");
            }

            function _undos() {
                return _undoStack[_subjectCode()];
            }

            function _redos() {
                return _redoStack[_subjectCode()];
            }

            function _subjectCode() {
                return _subject.currentSubject().obj.code;
            }

            function _onSwitch() {
                if(!_undoStack[_subjectCode()]) {
                    _undoStack[_subjectCode()] = [];
                    _redoStack[_subjectCode()] = [];
                } else {
                    _disableUndo();
                    _disableRedo();
                    if(_undos().length > 1) {
                        _enableUndo();
                    }
                    if(_redos().length > 1) {
                        _enableRedo();
                    }
                }
            }

            function _disableRemove(disabled) {
                var li = _dropdownList.find("a[data-value='remove']").parent();
                if(disabled) {
                    if(!li.hasClass("disabled")) {
                        li.addClass("disabled");
                    }
                } else {
                    li.removeClass("disabled");
                }
            }

            function _clearStack() {
                _undoStack[_subjectCode()] = [];
                _redoStack[_subjectCode()] = [];
            }

            return {
                push : _push,
                onSwitch : _onSwitch,
                disableRemove : _disableRemove,
                clearStack : _clearStack
            }
        }

        function Toolbar() {
            var _context = $("div.tool-bar");
            var _resourceUl = _context.find("ul.resource-list");
            var _nodeId;

            var _data = {};
            
            _init();
            function _init() {
                _context.find("ul.tab-nav li").on("click", function() {
                    if(!$(this).hasClass("active")) {
                        _selectTab($(this));
                    }
                });

                _onDragToolbar();

                _context.find("#dock-to-right-btn").on("click", function() {
                    _context.addClass("fix-on-right").removeClass("free");
                    _context.data("left", _context.css("left"));
                    _context.data("top", _context.css("top"));
                    _context.removeAttr("style");
                });

                _context.find("#free-dock").on("click", function() {
                    _context.addClass("free").removeClass("fix-on-right");
                    _context.css("left", _context.data("left"));
                    _context.css("top", _context.data("top"));
                });

                _context.find("#close-dock-btn").on("click", function() {
                    _context.hide();
                });

                _initTypeDropdown();
                _onChange();
            }

            function _initTypeDropdown() {
                var container = _context.find("div[role='select-type']");
                var inputName = container.find("input[type='text']");
                var inputValue = container.find("input[type='hidden']");
                var dropdownList = container.find("ul.dropdown-list");
                inputName.on("click", function(event) {
                    event.stopPropagation();
                    if(dropdownList.is(":hidden")) {
                        dropdownList.show();
                        $("body, svg").one("click.select-type", function() {
                            dropdownList.hide();
                        });
                    } else {
                        dropdownList.hide();
                    }
                });
                dropdownList.find("li").on("click", function(event) {
                    event.stopPropagation();
                    inputName.val($(this).find("a").text()).change();
                    inputValue.val($(this).find("a").attr("data-value")).change();
                    dropdownList.hide();
                });
            }

            function _getSubjectCode() {
                return $("#current-subject").data("code");
            }

            function _onChange() {
                _context.find("input[name='roleName']").on("change", function() {
                    var self = $(this);
                    self.removeClass("invalid");
                    var roleName = $.trim(self.val());
                    if(roleName.length === 0) {
                        self.addClass("invalid");
                        $.toaster("角色名称不能为空！", "blank-role-name");
                    } else {
                        _globalValidateRoleName(self.val(), function(success) {
                            if(success) {
                                var current = _subject.currentSubject();
                                current.tree.obj.setText($("#node-" + _nodeId), self.val())
                                current.menu.obj.setText($("#menu-" + _nodeId), self.val())
                                _getRole()["name"] = self.val(); 
                                _setChangeType("CHANGE_BASIC");
                            } else {
                                self.addClass("invalid");
                                $.toaster("输入的角色已经存在，换个名称重试！", "duplicate-role-name");
                            }
                        });
                    }
                });
                _context.find("input[name='roleType']").on("change", function() {
                    _getRole()["roleType"] = _context.find("input[name='roleType']").val(); 
                    _setChangeType("CHANGE_BASIC");
                });
            }

            function _switchRole(role) {
                // if(_context.find("input[name='roleCode']").val() == role.code) {
                //     return;
                // }

                _context.find("input[name='roleCode']").val(role.code);
                _context.find("input[name='roleName']").val(role.name);
                _context.find("input[name='roleName']").removeClass("invalid");

                _nodeId = role.id;

                var subjectCode = _getSubjectCode();
                if(!_data[subjectCode]) {
                    _data[subjectCode] = {
                        roles : [],
                        roleMap : {}
                    }
                }
                var roleData = _data[subjectCode];
                if(roleData.roleMap[role.code]) {
                    roleData.roleMap[role.code].name = role.name;
                    _render(roleData.roleMap[role.code]["resources"]);
                    _selectRoleType();
                } else {
                    if(role.code.indexOf("role-") < 0) {
                        _httpClient.getRoleResources(role.code, function(permissions) {
                            var resources = [];
                            var resourceMap = {};

                            for(var i = 0;i < permissions.length;i ++) {
                                var permission = permissions[i];
                                var resourceCode = permission.code;
                                var operations = [];
                                var operationMap = {};
                                if(!resourceMap[resourceCode]) {
                                    for(var j = 0;j < permissions.length;j ++) {
                                        if(resourceCode === permissions[j].code) {
                                            var operation = {
                                                code : permissions[j].operationCode,
                                                name : permissions[j].operationName
                                            };
                                            operations.push(operation);
                                            operationMap[permissions[j].operationCode] = operation;
                                        }
                                    }

                                    var resource = {
                                        code : resourceCode,
                                        name : permission.name,
                                        operations : operations,
                                        operationMap : operationMap
                                    }

                                    resources.push(resource);
                                    resourceMap[resourceCode] = resource;
                                }
                            }

                            var newRole = {
                                resources : resources,
                                resourceMap : resourceMap
                            };
                            $.extend(newRole, role);

                            roleData.roles.push(newRole);
                            roleData.roleMap[role.code] = newRole;
                            _render(newRole.resources);
                            _selectRoleType();
                        });
                    } else {
                        var newRole = {
                            roleType : "PROTECTED",
                            changeType : ["NEW"],
                            resources : [],
                            resourceMap : {}
                        };
                        $.extend(newRole, role);

                        roleData.roles.push(newRole);
                        roleData.roleMap[role.code] = newRole;
                        _render(newRole.resources);
                        _selectRoleType();
                    }
                }

                function _render(resources) {
                    _resourceUl.html("");
                    for(var i = 0;i < resources.length;i ++) {
                        var resource = resources[i];
                        var resourceItem = _createResourceItem(resource);

                        var operations = resource.operations;
                        for(var j = 0;j < operations.length;j ++) {
                            operationItem = _createOperationItem(operations[j]);
                            resourceItem.find("ul li.add").before(operationItem);
                        }

                        _resourceUl.append(resourceItem);
                    }
                    var addResourceLi = $($("#add-resource").get(0).outerHTML);
                    addResourceLi.removeAttr("id");
                    _resourceUl.append(addResourceLi);

                    _bind();
                }

                function _selectRoleType() {
                    var roleType = _getRole()["roleType"];
                    _context.find("div[role='select-type'] ul.dropdown-list li a[data-value='" + roleType + "']").parent().trigger("click");
                }

                function _bind() {
                    // toggle operation
                    _bindToggleOperations(_resourceUl.find("a[role='view-operation']"));

                    // show selector dropdown
                    _bindResourceSelector(_resourceUl.find("a[role='show-resource-dropdown']"));
                    _bindOperationSelector(_resourceUl.find("a[role='show-operation-dropdown']"));
                    
                    // edit
                    _bindEditResource(_resourceUl.find("a[role='add-resource'], a[role='edit-resource']"));
                    _bindEditOperation(_resourceUl.find("a[role='add-operation'], a[role='edit-operation']"));
                   
                    // delete
                    _bindDeleteResource(_resourceUl.find("[role='delete-resource']"));
                    _bindDeleteOperation(_resourceUl.find("[role='delete-operation']"));
                }
            }

            function _getRole() {
                return _data[_getSubjectCode()].roleMap[_context.find("input[name='roleCode']").val()];
            }

            function _removeRole(code) {
                if(code.indexOf("role-") === 0) {
                    var roles = _data[_getSubjectCode()].roles;
                    var roleMap = _data[_getSubjectCode()].roleMap;
                    var index = -1;
                    for(var i = 0;i < roles.length;i ++) {
                        if(roles[i].code === code) {
                            index = i;
                            break;
                        }
                    }
                    if(index >= 0) {
                        roles.splice(index, 1);
                    } 
                    delete roleMap[code];
                } else {
                    var role = _data[_getSubjectCode()].roleMap[code];
                    if(!role) {
                        role = {
                            code : code,
                            name : "anything",
                            changeType : "DELETE_ROLE"
                        }
                        _data[_getSubjectCode()].roles.push(role);
                        _data[_getSubjectCode()].roleMap[code] = role;
                    } else {
                        role["changeType"] = "DELETE_ROLE";
                    }
                }
            }

            function _getResourcesOfRole() {
                return _getRole()["resources"];
            }

            function _getOperationsOfResource(resourceCode) {
                return _getRole()["resourceMap"][resourceCode]["operations"];
            }

            function _addResource(resource) {
                var role = _getRole();
                role.resources.push(resource);
                role.resourceMap[resource.code] = resource;
                _setChangeType("CHANGE_PERMISSION");
            }

            function _addOperation(resourceCode, operation) {
                var resource = _getRole()["resourceMap"][resourceCode];
                resource.operations.push(operation);
                resource.operationMap[operation.code] = operation;
                _setChangeType("CHANGE_PERMISSION");
            }

            function _updateResource(resource) {
                var role = _getRole();
                var oldResource = role.resourceMap[resource.code];
                oldResource.name = resource.name;
                for(var i = 0;i < _appResources.length;i ++) {
                    if(_appResources[i].code == resource.code) {
                        $.extend(_appResources[i], resource);
                    }
                }
            }

            function _updateOperation(resourceCode, operation) {
                var resource = _getRole()["resourceMap"][resourceCode];
                var oldOperation = resource.operationMap[operation.code];
                oldOperation.name = operation.name;
                for(var i = 0;i < _appOperations.length;i ++) {
                    if(_appOperations[i].code == operation.code) {
                        $.extend( _appOperations[i], operation); 
                    }
                }
            }

            function _removeResource(resourceCode) {
                var role = _getRole();
                var resources = role.resources;
                var index = -1;
                for(var i = 0;i < resources.length;i ++) {
                    if(resources[i].code === resourceCode) {
                        index = i;
                        break;
                    }
                }
                if(index >= 0) {
                    resources.splice(index, 1);
                }
                delete role.resourceMap[resourceCode];
                _setChangeType("CHANGE_PERMISSION");
            }

            function _removeOperation(resourceCode, operationCode) {
                var resource = _getRole()["resourceMap"][resourceCode];
                var operations = resource.operations;
                var index = -1;
                for(var i = 0;i < operations.length;i ++) {
                    if(operations[i].code === operationCode) {
                        index = i;
                        break;
                    }
                }
                if(index >= 0) {
                    operations.splice(index, 1);
                }
                delete resource.operationMap[operationCode];
                _setChangeType("CHANGE_PERMISSION");
            }

            function _createResourceItem(resource) {
                var resourceItem = $($("#resource-item").get(0).outerHTML);
                resourceItem.removeAttr("id");
                resourceItem.data("resource-code", resource.code);
                resourceItem.find("[role='view-operation'] span:last-child").text(resource.name);

                var operationUl = $("<ul></ul>");
                resourceItem.append(operationUl);

                var addOperationLi = $($("#add-operation").get(0).outerHTML);
                addOperationLi.removeAttr("id");
                operationUl.append(addOperationLi);

                return resourceItem;
            }

            function _createOperationItem(operation) {
                var operationItem = $($("#operation-item").get(0).outerHTML);
                operationItem.removeAttr("id");
                operationItem.data("operation-code", operation.code);
                operationItem.find("div > span").eq(0).text(operation.name);
                return operationItem;
            }

            function _bindKeyboardSelection(input, selector) {
                if(_options().size() === 0) {
                    return;
                }

                $.fn.reverse = [].reverse;

                input.off("keydown");
                input.on("keydown", function(event) {
                    if(event.keyCode === 38) {
                        var selected = _selected();
                        if(selected.size() === 0) {
                            selector.scrollTo(_last(), 0);
                            _select(_last());
                        } else {
                            _unselect(selected);
                            _select(_prev(selected));
                        }
                    } else if(event.keyCode === 40) {
                        var selected = _selected();
                        if(selected.size() === 0) {
                            _select(_first());
                        } else {
                            _unselect(selected);
                            _select(_next(selected));
                        }
                    } else if(event.keyCode === 13) {
                        _selected().trigger("click");
                    }
                });
                _options().off("mouseover");
                _options().on("mouseover", function(event) {
                    _options().removeClass("selected");
                    $(this).addClass("selected");
                });

                function _select(option) {
                     option.addClass("selected");
                }

                function _unselect(option) {
                    option.removeClass("selected");
                }
                
                function _selected() {
                    return selector.find("li:not(.divider):not(.add):visible.selected");
                }

                function _options() {
                    return selector.find("li:not(.divider):not(.add):visible");
                }

                function _first() {
                    return _options().eq(0);
                }

                function _last() {
                    return _options().reverse().eq(0);
                }

                function _prev(option) {
                    var options = _options();
                    var prev;
                    options.reverse().each(function(i, value) {
                        if($(value).index() < option.index()) {
                            prev = $(value);
                            return false;
                        }
                    });
                    if(!prev) {
                        selector.scrollTo(_last(), 0);
                        return _last();
                    }
                    selector.scrollTo(prev, 0);
                    return prev;
                }

                function _next(option) {
                    var options = _options();
                    var next;
                    $.each(options, function(i, value) {
                        if($(value).index() > option.index()) {
                            next = $(value);
                            return false;
                        }
                    });
                    if(!next) {
                        selector.scrollTo(_first(), 0);
                        return _first();
                    }
                    selector.scrollTop(next.offset().top - selector.offset().top - 176 + selector.scrollTop());
                    return next;
                }
            }

            function _bindResourceSelector(obj) {
                obj.on("click", function(event) {
                    event.stopPropagation();
                    var dropdownList = $(this).closest("li").find("ul.dropdown-list");
                    dropdownList.find("li").show();
                    dropdownList.find("li:not(.divider):not(.add)").remove();

                    var selectedResources = _getResourcesOfRole();
                    for(var i = 0;i < _appResources.length;i ++) {
                        var appResource = _appResources[i];
                        var selected = false;
                        for(var j = 0;j < selectedResources.length;j ++) {
                            if(appResource.code == selectedResources[j].code) {
                                selected = true;
                            }
                        }
                        if(!selected) {
                            var li = $('<li><a href="javascript:void(0)"></a></li>');
                            li.find("a").text(appResource.name);
                            li.find("a").data("value", appResource.code);
                            dropdownList.find("li.divider").before(li);

                            li.on("click", function(event) {
                                event.stopPropagation();
                                var resource = {
                                    code : $(this).find("a").data("value"),
                                    name : $(this).find("a").text(),
                                    operations : [],
                                    operationMap : {}
                                };
                                var resourceItem = _createResourceItem(resource);
                                _addResource(resource);
                                
                                $(this).closest("li.add").before(resourceItem);
                                _bindToggleOperations(resourceItem.find("a[role='view-operation']"));
                                _bindOperationSelector(resourceItem.find("a[role='show-operation-dropdown']"));
                                _bindEditOperation(resourceItem.find("a[role='add-operation']"));
                                _bindEditResource(resourceItem.find("a[role='edit-resource']"));
                                _bindDeleteResource(resourceItem.find("button[role='delete-resource']"));
                                _hideDropdown();
                            });
                        }
                    }

                    _hideDropdown();
                    _showDropdown(dropdownList);
                    _bindKeyboardSelection(dropdownList.siblings("input"), dropdownList);

                    $("body, svg").one("click.hide-dropdown", function() {
                        _hideDropdown();
                    });

                    _context.find("ul.resource-list").one("scroll", function() {
                        _hideDropdown();
                    });

                    dropdownList.siblings("input").one("click", function(event) {
                        event.stopPropagation();
                    });

                    _bindAutoComplete($(this).closest("li").find("div.dropdown"));
                });
            }

            function _bindOperationSelector(obj) {
                obj.on("click", function(event) {
                    event.stopPropagation();
                    var dropdownList = $(this).closest("li").find("ul.dropdown-list");
                    dropdownList.find("li").show();
                    dropdownList.find("li:not(.divider):not(.add)").remove();

                    var resourceCode = $(this).closest("ul").closest("li").data("resource-code");
                    var selectedOperations = _getOperationsOfResource(resourceCode);
                    for(var i = 0;i < _appOperations.length;i ++) {
                        var appOperation = _appOperations[i];
                        var selected = false;
                        for(var j = 0;j < selectedOperations.length;j ++) {
                            if(appOperation.code == selectedOperations[j].code) {
                                selected = true;
                            }
                        }
                        if(!selected) {
                            var li = $('<li><a href="javascript:void(0)"></a></li>');
                            li.find("a").text(appOperation.name);
                            li.find("a").data("value", appOperation.code);
                            dropdownList.find("li.divider").before(li);

                            li.on("click", function(event) {
                                event.stopPropagation();
                                var operation = {
                                    code : $(this).find("a").data("value"),
                                    name : $(this).find("a").text()
                                };
                                var operationItem = _createOperationItem(operation);
                                var resourceCode = $(this).closest("li.add").parent().closest("li").data("resource-code");
                                _addOperation(resourceCode, operation);
                                $(this).closest("li.add").before(operationItem);
                                _bindDeleteOperation(operationItem.find("button[role='delete-operation']"));
                                _bindEditOperation(operationItem.find("a[role='edit-operation']"));
                                _hideDropdown();
                            });
                        }
                    }

                    _hideDropdown();
                    _showDropdown(dropdownList);
                    _bindKeyboardSelection(dropdownList.siblings("input"), dropdownList);

                    $("body, svg").one("click.hide-dropdown", function() {
                        _hideDropdown();
                    });

                    _context.find("ul.resource-list").one("scroll", function() {
                        _hideDropdown();
                    });

                    dropdownList.siblings("input").one("click", function(event) {
                        event.stopPropagation();
                    });

                    _bindAutoComplete($(this).closest("li").find("div.dropdown"));
                });
            }

            function _bindAutoComplete(dropdown) {
                var input = dropdown.find("input[autocomplete]");
                var selector = dropdown.find("ul.dropdown-list");
                input.on("keyup", function(event) {
                    selector.find("li:not(.divider):not(.add)").hide();
                    selector.find("li a:contains('" + $(this).val() + "')").parent().show();
                });
            }

            function _bindToggleOperations(obj) {
                obj.on("click", function(event) {
                    if($(this).closest("li").hasClass("fold")) {
                        $(this).closest("li").removeClass("fold").addClass("unfold");
                        $(this).find("span:first-child").removeClass("glyphicon-chevron-right").addClass("glyphicon-chevron-down");
                    } else {
                        $(this).closest("li").removeClass("unfold").addClass("fold");
                        $(this).find("span:first-child").removeClass("glyphicon-chevron-down").addClass("glyphicon-chevron-right");
                    }
                });
            }

            function _bindShowBtn(obj) {
                $.each(obj, function(i, value) {
                    var showBtn = $(value).siblings("a[role='show']");
                    showBtn.on("mousedown", function(event) {
                        event.stopPropagation();
                    });
                    showBtn.on("click", function(event) {
                        $(value).css("right", 0);
                        $("body, svg").one("mousedown", function() {
                            $(value).css("right", -71);
                        });
                    });
                    $(value).on("mousedown", function(event) {
                        event.stopPropagation();
                    });
                });
            }

            function _bindDeleteResource(obj) {
                _bindShowBtn(obj);
                obj.on("click", function(event) {
                    event.stopPropagation();
                    var resourceCode = $(this).closest("li").data("resource-code");
                    _removeResource(resourceCode);
                    $(this).closest("li").remove();
                });
            }

            function _bindDeleteOperation(obj) {
                _bindShowBtn(obj);
                obj.on("click", function(event) {
                    event.stopPropagation();
                    var resourceCode = $(this).closest("ul").closest("li").data("resource-code");
                    var operationCode = $(this).closest("li").data("operation-code");
                    _removeOperation(resourceCode, operationCode);
                    $(this).closest("li").remove();
                });
            }

            function _bindEditResource(obj) {
                obj.on("click", function() {
                    _hideDropdown();
                    var self = $(this);
                    if(self.attr("role") === "edit-resource") {
                        var loadingOverlay = $.overlay({
                            id : "loading",
                            close : "disabled"
                        });
                        _httpClient.getResource(self.closest("li").data("resource-code"), function(resource) {
                            loadingOverlay.close();
                            _showOverlay(resource);
                        });
                    } else {
                        _showOverlay();
                    }

                    function _showOverlay(resource) {
                        _overlayForm.open("resource", function(form) {
                            if(resource) {
                                var resourceCode = $("<input type='hidden' name='code'></input>");
                                form.append(resourceCode);
                                for(var key in resource) {
                                    form.find("input[name='" + key + "']").val(resource[key]);
                                }
                                form.siblings("h2").text("修改资源");
                            }
                        }, function(form) {
                            if(form.validate()) {
                                if(resource) {
                                    var data = form.serializeObject();
                                    _httpClient.updateResource(data, function() {
                                        _updateResource({
                                            code : resource.code, 
                                            name : data.name
                                        });
                                        self.closest("li").find("[role='view-operation'] span:last-child").text(data.name);
                                        _overlayForm.close();
                                    });
                                } else {
                                    _httpClient.createResource(form.serializeObject(), function(resource) {
                                        _appResources.push(resource);
                                        self.closest("li.add-resource").find("a[role='show-resource-dropdown']").trigger("click");
                                        _overlayForm.close();
                                    });
                                }
                            }
                        });
                    }
                });
            }

            function _bindEditOperation(obj) {
                obj.on("click", function() {
                    _hideDropdown();
                    var self = $(this);
                    if(self.attr("role") === "edit-operation") {
                        var loadingOverlay = $.overlay({
                            id : "loading",
                            close : "disabled"
                        });
                        _httpClient.getOperation(self.closest("li").data("operation-code"), function(operation) {
                            loadingOverlay.close();
                            _showOverlay(operation);
                        });
                    } else {
                        _showOverlay();
                    }
                
                    function _showOverlay(operation) {
                        _overlayForm.open("operation", function(form) {
                            var operationName = form.find("input[name='name']");
                            var group = operationName.closest("div.input-group");

                            if(operation) {
                                var operationCode = $("<input type='hidden' name='code'></input>");
                                form.append(operationCode);
                                for(var key in operation) {
                                    form.find("input[name='" + key + "']").val(operation[key]);
                                }
                                form.siblings("h2").text("修改操作");
                                operationName.data("originName", operation.name)
                            }

                            operationName.on("change", function() {
                                group.removeClass("invalid");
                                group.find(".glyphicon").hide();
                                group.find(".error-msg").hide();

                                if($(this).val().length > 0) {
                                    if($(this).val() == $(this).data("originName")) {
                                        group.find(".glyphicon-ok").show();
                                    } else {
                                        group.find(".checking").show();
                                        _httpClient.validateOperationName(_user.appCode, $(this).val(), function(success) {
                                            group.find(".checking").hide();
                                            if(success) {
                                                group.find(".glyphicon-ok").show();
                                            } else {
                                                group.addClass("invalid");
                                                group.find(".glyphicon-exclamation-sign").show();
                                                group.find(".error-msg[verify]").show();
                                            }
                                        });
                                    }
                                }
                            });
                        }, function(form) {
                            if(form.find(".error-msg[verify]:visible").size() === 0 && form.validate()) {
                                if(operation) {
                                    var data = form.serializeObject();
                                    _httpClient.updateOperation(data, function() {
                                        _updateOperation(self.closest("ul").closest("li").data("resource-code"), {
                                            code : operation.code,
                                            name : data.name
                                        });
                                        self.closest("li").find("div > span:first-child").text(data.name);
                                        _overlayForm.close();
                                    });
                                } else {
                                    _httpClient.createOperation(form.serializeObject(), function(operation) {
                                        _appOperations.push(operation);
                                        self.closest("li.add-operation").find("a[role='show-operation-dropdown']").trigger("click");
                                        _overlayForm.close();
                                    });
                                }
                            }
                        });
                    }
                });
            }

            function _showDropdown(dropdownList) {
                dropdownList.show();
                dropdownList.siblings("input").removeAttr("readonly");
                dropdownList.siblings("input").focus();
                var container = dropdownList.closest("div.dropdown");
                if(container.hasClass("dropdown-2")) {
                    dropdownList.css("top",  dropdownList.siblings("input").offset().top - parseInt(_context.css("top")) - 15);
                }
            }

            function _hideDropdown() {
                _context.find("ul.dropdown-list").hide();
                _context.find("div.dropdown input").blur();
                _context.find("div.dropdown-2 input").val("");
                _context.find("div.dropdown input").attr("readonly", "true");
            }

            function _selectTab(tab) {
                _context.find("ul.tab-nav li").removeClass("active");
                tab.addClass("active");
                tab.parent().siblings("div.panel").removeClass("active");
                tab.parent().siblings("div.panel").eq(tab.index()).addClass("active");
            }

            function _onDragToolbar() {
                _context.find("ul.tab-nav").on("mousedown", function(event) {
                    var self = $(this);
                    var toolbar = _context;
                    var offset = {
                        x : event.pageX - parseInt(toolbar.css("left")),
                        y : event.pageY - parseInt(toolbar.css("top"))
                    };
                    $("body").on("mousemove.toolbar", function(event) {
                        if(self.closest("div.tool-bar").hasClass("free")) {
                            toolbar.css("left", event.pageX - offset.x);
                            toolbar.css("top", event.pageY - offset.y);
                        }
                    });
                    $(window).on("mouseup", function() {
                        $("body").off("mousemove.toolbar");
                    });
                }).on("mouseup", function(event) {
                    $("body").off("mousemove.toolbar");
                });
            }

            function _setRoleName(name) {
                var role = _getRole();
                if(role) {
                    _context.find("input[name='roleName']").val(name);
                    _context.find("input[name='roleName']").removeClass("invalid");
                    _getRole()["name"] = name;
                    _setChangeType("CHANGE_BASIC");
                }
            }

            function _setChangeType(type) {
                var changeType = _getRole()["changeType"];
                if(!changeType) {
                    _getRole()["changeType"] = [type];
                } else  {
                    if(changeType.indexOf("NEW") < 0 && changeType.indexOf(type) < 0) {
                        changeType.push(type);
                    }
                }
                _subject.enableSave();
                _editor.push();
            }

            function _getData(subjectCode) {
                return _data[subjectCode];
            }

            function _setData(subjectCode, data) {
                _data[subjectCode] = data;
            }

            function _clearAllDataChange() {
                for(var subjectCode in _data) {
                    _clearDataChange(subjectCode);
                }
            }

            function _clearDataChange(subjectCode) {
                var roles = _data[subjectCode].roles;
                for(var i = 0;i < roles.length;i ++) {
                    roles[i].changeType = [];
                }
            }

            return {
                switchRole : _switchRole,
                show : function() {
                    _context.show();
                },
                hide : function() {
                    _context.hide();
                },
                isHidden : function() {
                    return _context.is(":hidden");
                },
                isDraggable : function() {
                    return _context.hasClass("free");
                },
                setPosition : function(position) {
                    _context.css("left", position.x).css("top", position.y);
                },
                setRoleName : _setRoleName,
                getData : _getData,
                setData : _setData,
                setChangeType : _setChangeType,
                removeRole : _removeRole,
                clearAllDataChange : _clearAllDataChange,
                clearDataChange : _clearDataChange
            }
        }

        function Menu(context) {
            var _tree;
            var _container = context;
            var _sideBar = $("div.side-bar");

            function _init(roles) {
                var root = _container.children("li").eq(0).find("div");
                root.attr("code", 0);
                _build(root, roles);
            }

            function _build(parent, roles) {
                var parentCode = parent.attr("code");
                var children = roles.filter(function(role, index, array) {
                    return role.parent == parentCode;
                }).sort(function(role1, role2) {
                    return role1.order - role2.order;
                });
                for(var i = 0;i < children.length;i ++) {
                    var id = _tree.getIdByCode(children[i].code);
                    if(id) {
                        var item = _insertChild(parent, id);
                        item.find("div").attr("code", children[i].code);
                        item.find("div").attr("parent", children[i].parent);
                        item.find("div").find("a").text(_textToContent(children[i].name));
                        _build(item.find("div"), roles);
                    }
                }
            }

            function _setTree(tree) {
                _tree = tree;
            }

            function _create(parentMenu, id) {
                var item = $('<li><div><span class="no-children"></span><a href="javascript:void(0)">双击节点这里添加文字</a></div></li>');
                if(!id) {
                    id = new Date().getTime();
                }
                item.find("div").attr("id", "menu-" + id);
                if(parentMenu) {
                    item.find("div").attr("parent-id", parentMenu.attr("id"));
                    _onMove(item);
                } else {
                    item.find("div").data("root", true);
                }

                item.find("div").contextmenu(function(event) {
                    event.stopPropagation();
                    event.preventDefault();
                    if(_container.find("div[id^='menu-']").not($(this)).find("input").size() === 0) {
                        _onShowContextMenu($(this), event);
                    }
                });

                item.find("div").dblclick(function() {
                    if(_container.find("div[id^='menu-']").not($(this)).find("input").size() === 0
                        && !_tree.isNodeOnEditing()) {
                        _onEditText($(this));
                    }
                });

                item.find("div").on("click", function(event) {
                    //event.stopPropagation();
                    _hideContextMenu();
                    _tree.hideContextMenu();
                    if(_container.find("div[id^='menu-']").not($(this)).find("input").size() === 0
                        && !_tree.isNodeOnEditing()) {
                        _container.find("input").blur();
                        _onSelect($(this));
                        var node = _getNode($(this));
                        _tree.select(node);
                        _tree.switchRole(node);
                    }
                });

                return item;
            }

            function _createRoot(textContent, id) {
                var item = _create(null, id);
                item.find("a").text(textContent);
                item.find("div").off("dblclick");
                item.find("div").off("click");
                _container.append(item);
            }

            function _insertChild(menu, id) {
                var item = _create(menu, id);

                if(menu) {
                    var childrenContainer;
                    if(_childrenSize(menu) > 0) {
                        childrenContainer = menu.siblings("ul");
                    } else {
                        childrenContainer = $("<ul></ul>");
                        menu.closest("li").append(childrenContainer);
                        _born(menu);
                    }
                    childrenContainer.append(item);
                    if(!id) {
                        _tree.insertChildNode(_getNode(menu), _getIdWithoutPrefix(item.find("div")));
                    }
                    _setOrder(item);
                } else {
                    _container.append(item);
                }
                return item;
            }

            function _onMove(item) {
                var timer1;
                var timer2;
                item.find("a").on("mousedown", function(event) {
                    event.preventDefault();
                })
                item.on("mousedown", function(event1) {
                    event1.stopPropagation();
                    var self = $(this);
                    if(event1.which === 1) {
                        var topOffset = 84;
                        var offset = {
                            x : event1.pageX - self.offset().left - _sideBar.scrollLeft(),
                            y : event1.pageY - self.offset().top - _sideBar.scrollTop()
                        };
                        timer1 = setTimeout(function() {
                            var mover = $(self.get(0).outerHTML);
                            mover.attr("mover", "true");
                            mover.addClass("move");
                            mover.css("left", event1.pageX - offset.x)
                            mover.css("top", event1.pageY - offset.y - topOffset);
                            self.css("visibility", "hidden");
                            self.parent().append(mover);

                            $(document).on("mousemove", function(event2) {
                                var left = event2.pageX - offset.x;
                                var top = event2.pageY - offset.y;
                                mover.css("left", left);
                                mover.css("top", top - topOffset);
                                clearTimeout(timer2);
                                timer2 = setTimeout(function() {
                                    $.each(self.parent().children("li[mover!='true']"), function(i, value) {
                                        if(Math.abs(left - $(value).offset().left) < 280) {
                                            if(i < self.index()) {
                                                if(top - _sideBar.scrollTop() - $(value).offset().top < 0 
                                                    && top - _sideBar.scrollTop() - $(value).offset().top > -$(value).height()) {
                                                    $(value).before(self);
                                                    _setChangeOrderStatus(self);
                                                }
                                            } else if(i > self.index()) {
                                                if(top - _sideBar.scrollTop() - $(value).offset().top > 0 
                                                    && top - _sideBar.scrollTop() - $(value).offset().top < $(value).height()) {
                                                    $(value).after(self);
                                                    _setChangeOrderStatus(self);
                                                }
                                            }
                                        }
                                    });
                                }, 150);
                            });
                        }, 200);
                    }

                    $(document).on("mouseup", function(event) {
                        item.off("mousedown");
                        $(document).off("mouseup");
                        $(document).off("mousemove");
                        item.siblings("[mover]").remove();
                        item.removeClass("move");
                        item.css("left", "")
                        item.css("top", "");
                        self.css("visibility", "");
                        clearTimeout(timer1);
                        _onMove(item);
                        _setOrder(item);
                    });
                });
            }

            function _setChangeOrderStatus(item) {
                var ul = item.parent();
                if(ul.find("li > div[code]").size() > 0) {
                    ul.attr("order-changed", "true");
                    _subject.enableSave();
                }
            }

            function _getOrderChangedData() {
                var data = [];
                $.each(_container.find("ul[order-changed]"), function(i, ul) {
                    $.each($(ul).children("li").children("div[code]"), function(j, div) {
                        data.push({
                            code : $(div).attr("code"),
                            order : $(div).parent().attr("order")
                        });
                    });
                });
                return data;
            }

            function _getIdWithoutPrefix(menu) {
                return menu.attr("id").substring(5);
            }

            function _getNode(menu) {
                return _tree.getNodeById("node-" + _getIdWithoutPrefix(menu))
            }

            function _insertSiblingAbove(menu, id) {
                _unfold(menu);
                var item = _create(menu, id);
                menu.closest("li").before(item);
                if(!id) {
                    _tree.insertSiblingNodeAbove(_getNode(menu), _getIdWithoutPrefix(item.find("div")));
                }
                _setOrder(item);
            }

            function _insertSiblingBelow(menu, id) {
                _unfold(menu);
                var item = _create(menu, id);
                menu.closest("li").after(item);
                if(!id) {
                    _tree.insertSiblingNodeBelow(_getNode(menu), _getIdWithoutPrefix(item.find("div")));
                }
                _setOrder(item);
            }

            function _setOrder(item) {
                $.each(item.parent().children("li"), function(i, value) {
                    $(value).attr("order", i + 1);
                });
            }

            function _remove(menu, cascade) {
                menu.parent().remove();
                var parentMenu = _getMenuById(menu.attr("parent-id"));
                if(_childrenSize(parentMenu) === 0) {
                    _noChildren(parentMenu);
                }
                if(cascade) {
                    _tree.remove(_getNode(menu));
                }
            }

            function _born(menu) {
                menu.find("span").removeClass("no-children").addClass("menu-down");
                menu.find("span").on("click", function() {
                    var children = $(this).closest("li").children("ul");
                    if(children.is(":hidden")) {
                        _unfold(menu);
                    } else {
                        _fold(menu);
                    }
                });
            }

            function _noChildren(menu) {
                menu.find("span").removeClass("menu-right").removeClass("menu-down").addClass("no-children");
                menu.siblings("ul").remove();
                menu.find("span").off("click");
            }

            function _fold(menu) {
                menu.find("span").removeClass("menu-down").addClass("menu-right");
                menu.siblings("ul").slideUp("fast");
            }

            function _unfold(menu) {
                menu.find("span").removeClass("menu-right").addClass("menu-down");
                menu.siblings("ul").slideDown("fast");
            }

            function _moveUp(menu) {
                var item = menu.closest("li");
                item.prev().before(menu.closest("li"));
                _setChangeOrderStatus(item);
                _setOrder(item);
            }

            function _moveDown(menu) {
                var item = menu.closest("li");
                item.next().after(menu.closest("li"));
                _setChangeOrderStatus(item);
                _setOrder(item);
            }

            function _childrenSize(menu) {
                return menu.siblings("ul").children().size();
            }

            function _getMenuById(id) {
                return $("#" + id);
            }

            function _onSelect(menu) {
                _container.find("li div").removeClass("selected");
                if(menu) {
                    menu.addClass("selected");
                }
            }

            function _onShowContextMenu(obj, event) {
                event.preventDefault();
                event.stopPropagation();

                _hideContextMenu();
                _tree.hideContextMenu();
                $("div.view ul.context-menu").remove();
                var menu = $($("#node-text-menu").get(0).outerHTML);
                menu.css("left", 50);
                menu.css("top", event.pageY + _sideBar.scrollTop() - 64);
                if(obj.data("root")) {
                    menu.find("li.divider").eq(0).remove();
                    menu.find("li[role='insert-sibling-above']").remove();
                    menu.find("li[role='insert-sibling-below']").remove();
                    menu.find("li[role='remove-node']").remove();
                    menu.find("li[role='edit-text']").remove();
                    menu.find("li[role='edit-node']").remove();
                }
                if(_childrenSize(obj) > 0) {
                    menu.find("li[role='remove-node']").remove();
                }
                 menu.find("li[role='move-up']").prev().hide();
                if(obj.find("span").hasClass("menu-down")) {
                    menu.find("li[role='fold-children']").show();
                    menu.find("li[role='unfold-children']").hide();
                    menu.find("li[role='move-up']").prev().show();
                } else if(obj.find("span").hasClass("menu-right")) {
                    menu.find("li[role='fold-children']").hide();
                    menu.find("li[role='unfold-children']").show();
                    menu.find("li[role='move-up']").prev().show();
                } else {
                    menu.find("li[role='fold-children']").hide();
                    menu.find("li[role='unfold-children']").hide();
                }
                if(obj.closest("li").prev().size() > 0) {
                    menu.find("li[role='move-up']").show();
                    menu.find("li[role='move-up']").prev().show();
                } else {
                    menu.find("li[role='move-up']").hide();
                }
                if(obj.closest("li").next().size() > 0) {
                    menu.find("li[role='move-down']").show();
                    menu.find("li[role='move-up']").prev().show();
                } else {
                    menu.find("li[role='move-down']").hide();
                }
                _sideBar.append(menu);

                menu.find("li").on("click", function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    
                    if($(this).attr("role") === "edit-text") {
                        _onEditText(obj);
                    } else if($(this).attr("role") === "insert-child-node") {
                        _insertChild(obj);
                    } else if($(this).attr("role") === "insert-sibling-above") {
                        _insertSiblingAbove(obj);
                    } else if($(this).attr("role") === "insert-sibling-below") {
                        _insertSiblingBelow(obj);
                    } else if($(this).attr("role") === "remove-node") {
                        _remove(obj, true);
                    } else if($(this).attr("role") === "edit-node") {
                        _onEditNode(obj);
                    } else if($(this).attr("role") === "fold-children") {
                        _fold(obj);
                    } else if($(this).attr("role") === "unfold-children") {
                        _unfold(obj);
                    } else if($(this).attr("role") === "move-up") {
                        _moveUp(obj);
                    } else if($(this).attr("role") === "move-down") {
                        _moveDown(obj);
                    } 
                    _hideContextMenu();
                });

                $("body, div.side-bar, svg").one("click.menu-context-menu", function() {
                    _hideContextMenu();
                });
                $("body").one("keyup.menu-context-menu", function(event) {
                    if(event.keyCode === 27) {
                        _hideContextMenu();
                    }
                });
            }

            function _onEditText(menu) {
                var a = menu.find("a:last-child");
                var input = $("<input type='text' placeholder='请输入文字' maxlength='100'></input>");
                var text = _contentToText(a.text());
                input.val(text);
                input.data("origin-text", text);
                a.after(input);
                a.remove();
                input.focus();
                input.select();

                $("body, div.view, svg").on("click.save-menu-text", function(event) {
                    _onSaveText();
                });
                $("body, div.view, svg").on("keyup.save-menu-text", function(event) {
                    if(event.keyCode === 27) {
                        _onSaveText();
                    }
                });
                menu.on("click", function(event) {
                    event.stopPropagation();
                });
                input.on("click", function(event) {
                    event.stopPropagation();
                });
                input.on("blur", function(event) {
                    _onSaveText();
                });
                input.on("mousedown", function(event) {
                    event.stopPropagation();
                });
                input.on("keyup", function(event) {
                    if(event.keyCode === 13 || event.keyCode === 110) {
                        _onSaveText();
                    }
                });

                function _onSaveText() {
                    var input = menu.find("input");
                    if(input.size() > 0) {
                        input.removeClass("invalid");
                        var text = input.val();
                        text = $.trim(text);
                        if(text.length === 0) {
                            input.addClass("invalid");
                            input.focus();
                            $.toaster("角色名称不能为空！", "blank-role-name");
                        } else if(text == input.data("origin-text") || 
                            (_getNode(menu).data("role") && text == _getNode(menu).data("role").name)) {
                            _doSave();
                        } else {
                            _globalValidateRoleName(text, function(success) {
                                if(success) {
                                    _doSave();
                                } else {
                                    input.addClass("invalid");
                                    input.focus();
                                    $.toaster("输入的角色已经存在，换个名称重试！", "duplicate-role-name");
                                }
                            });
                        }
                    }

                    function _doSave() {
                        var a = $("<a href='javascript:void(0)'></a>");
                        a.text(_textToContent(text));
                        input.after(a);
                        input.remove();
                        _saveNodeText(menu);
                        $("body, div.view, svg").off("click.save-menu-text");
                        $("body, div.view, svg").off("keyup.save-menu-text");
                    }
                }
            }

            function _saveNodeText(menu) {
                var text = _contentToText(menu.find("a:last-child").text());
                _tree.setText(_getNode(menu), text);
                _toolbar.setRoleName(text);
            }

            function _setText(menu, text) {
                menu.find("a:last-child").text(_textToContent(text));
            }

            function _onEditNode(menu) {
                _tree.onEditNode(_getNode(menu));
            }

            function _textToContent(text) {
                var text = $.trim(text);
                if(text === "") {
                    return "双击节点这里添加文字";
                }
                return text;
            }

            function _contentToText(content) {
                if(content === "双击节点这里添加文字") {
                    return "";
                }
                return content;
            }

            function _roleIdToMenuId(roleId) {
                return "menu-" + roleId.substring(5);
            }

            function _getOrderByCode(code) {
                if(code.indexOf("role-") === 0) {
                    return _container.find("div[id='" + _roleIdToMenuId(code) + "']").closest("li").attr("order");
                } else {
                    return _container.find("div[code='" + code + "']").closest("li").attr("order");
                }
            }

            function _getNameByCode(code) {
                if(code.indexOf("role-") === 0) {
                    return _contentToText(_container.find("div[id='" + _roleIdToMenuId(code) + "'] a").text());
                } else {
                    return _contentToText(_container.find("div[code='" + code + "'] a").text());
                }
            }

            function _isMenuOnEditing() {
                return _container.find("div[id^='menu-'] input").size() > 0;
            }

            function _hideContextMenu() {
                _sideBar.find("ul.context-menu").remove();
            }

            function _getSelectedMenu() {
                return _container.find("div[id^='menu-'].selected");
            }

            return {
                initialize : _init,
                createRoot : _createRoot,
                setTree : _setTree,
                insertChild : _insertChild,
                insertSiblingAbove : _insertSiblingAbove,
                insertSiblingBelow : _insertSiblingBelow,
                remove : _remove,
                getMenuById : _getMenuById,
                setText : _setText,
                select : _onSelect,
                getOrderByCode : _getOrderByCode,
                getOrderChangedData : _getOrderChangedData,
                getNameByCode : _getNameByCode,
                isMenuOnEditing : _isMenuOnEditing,
                hideContextMenu : _hideContextMenu,
                getSelectedMenu : _getSelectedMenu,
                childrenSize : _childrenSize
            }
        }

        function Tree(context) {
            var NODE_SPACE_X = 179;
            var NODE_SPACE_Y = 84;
            var _menu;
            var _canvas;

            function _init(svg) {
                _setData(svg);

                var timer;
                _canvas.on("mousedown", function(event) {
                    var self = $(this);
                    timer = setTimeout(function() {
                        var offset = {
                            x : event.pageX - parseInt(self.css("left")),
                            y : event.pageY - parseInt(self.css("top"))
                        };
                        var mousedownOrigin = {
                            x : event.pageX,
                            y : event.pageY
                        }
                        if(_canvas.hasClass("focus")) {
                            $.each(context.find("a.node-text"), function(i, value) {
                                var origin = {
                                    x : parseInt($(value).css("left")),
                                    y : parseInt($(value).css("top"))
                                };
                                $(value).data("origin", origin);
                            });
                        } else {
                            _canvas.addClass("focus");
                            $.each(context.find("a.node-text"), function(i, value) {
                                var origin = {
                                    x : parseInt($(value).css("left")),
                                    y : parseInt($(value).css("top"))
                                };
                                $(value).css("left", origin.x).css("top", origin.y);
                                $(value).next("textarea").css("left", origin.x).css("top", origin.y);
                                $(value).data("origin", origin);
                            });
                        }
                        $("body").on("mousemove.canvas", function(event) {
                            self.css("left", event.pageX - offset.x);
                            self.css("top", event.pageY - offset.y);
                            $.each(context.find("a.node-text"), function(i, value) {
                                var origin = $(value).data("origin");
                                $(value).css("left", origin.x + event.pageX - mousedownOrigin.x)
                                    .css("top", origin.y + event.pageY - mousedownOrigin.y);
                            });
                        });
                    }, 100);

                    $(window).one("mouseup", function() {
                        $("body").off("mousemove.canvas");
                    });
                }).on("mouseup", function(event) {
                    clearTimeout(timer);
                    $("body").off("mousemove.canvas");
                });

                $("body").on("click.canvas", function() {
                    if(_canvas.hasClass("focus")) {
                        _canvas.removeClass("focus");
                    }
                    $("body").off("mousemove.canvas");
                });

                _canvas.on("click.canvas", function(event) {
                    event.stopPropagation();
                    $("div.view ul.context-menu").remove();
                });

                context.contextmenu(function(event) {
                    event.preventDefault();
                });
            }

            function _setData(svg) {
                context.html("");
                context.append(svg);
                _canvas = context.find("svg[role='canvas']");
                var rootNode = svg.find("svg[id^='node-'][root='true']");
                if(rootNode && rootNode.data("subject")) {
                    _createRootText(rootNode, rootNode.data("subject").name);
                    var rootId = rootNode.attr("id");
                    var rootCode = rootNode.data("subject").code;
                    _getText(rootNode).attr("id", "text-" + rootCode);
                    rootNode.attr("id", "node-" + rootCode);
                    svg.find("svg[h-id='" + rootId + "']").attr("h-id", "node-" + rootCode)
                    $.each(svg.find("svg[id^='node-'][root!='true']"), function(i, node) {
                        if($(node).attr("parent-id") === rootId) {
                            $(node).attr("parent-id","node-" +  rootCode);
                        }
                        if($(node).data("role")) {
                            _createText($(node), $(node).data("role").name);
                        }
                    });
                }
            }

            function _setMenu(menu) {
                _menu = menu;
            }

            function _createRootNode(textContent) {
                var node = $($("#root-node").html());
                _canvas.append(node);

                var id = new Date().getTime();
                node.attr("id", "node-" + id);
                node.data("root", true); 
                node.attr("root", true);

                _createRootText(node, textContent);

                node.contextmenu(function(event) {
                    _onShowContextMenu(node, event, _locateRootNodeText);
                });
                return id;
            }

            function _createRootText(node, textContent) {
                var text = $('<a class="node-text" href="javascript:void(0)"><span></span></a>');
                text.addClass("root");
                text.find("span").text(textContent);
                text.attr("id", "text-" + _getIdWithoutPrefix(node));
                text.data("root", true);
                
                context.append(text);
                _locateRootNodeText(text, node);

                text.contextmenu(function(event) {
                    _onShowContextMenu(text, event, _locateRootNodeText);
                });
            }

            function _createNode(parentId, id) {
                var node = $($("#sub-node").html());
                if(!id) {
                    id = new Date().getTime();
                }
                node.attr("id", "node-" + id);
                node.attr("code", "role-" + id);
                node.attr("parent-id", parentId);
                node.attr("parent", _getNodeById(parentId).attr("code"));
                var text = _createText(node);

                node.contextmenu(function(event) {
                    _onShowContextMenu(node, event, _locateNodeText);
                });
                return {
                    node : node,
                    text : text
                };
            }

            function _createText(node, textContent) {
                var text = $('<a class="node-text" href="javascript:void(0)"><span></span></a>');
                if(textContent) {
                    text.find("span").text(textContent);
                }
                context.append(text);
                _locateNodeText(text, node);
                text.attr("id", "text-" + _getIdWithoutPrefix(node)); 

                text.on("click", function(event) {
                    //event.stopPropagation();
                    _hideContextMenu();
                    _menu.hideContextMenu();
                    if(text.siblings().find("textarea").size() === 0
                        && !_menu.isMenuOnEditing()) {
                        _onEditNode(node);
                        _onSelect(node);
                        _menu.select(_getMenu(node));
                        _editor.disableRemove(_childrenSize(_getSelectedNode()) != 0);
                    }
                });

                text.dblclick(function(event) {
                    event.stopPropagation();
                    if(text.siblings().find("textarea").size() === 0
                        && !_menu.isMenuOnEditing()) {
                        _onEditText(text, node, _locateNodeText);
                    }
                });

                text.contextmenu(function(event) {
                    event.stopPropagation();
                    event.preventDefault();
                    if(text.siblings().find("textarea").size() === 0
                        && !_menu.isMenuOnEditing()) {
                        _onShowContextMenu(text, event, _locateNodeText);
                    } 
                });

                text.on("mousedown", function(event) {
                    event.preventDefault();
                });
                return text;
            }

            function _insertChildNode(parentNode, id) {
                if(parentNode.attr("root")) {
                    parentNode.find("[role='branch']").show();
                } else {
                    parentNode.find("[role='branch'][expanded]").attr("expanded", "true");
                }
                var parentId = parentNode.attr("id");

                var nodeGroup = _createNode(parentId, id);
                var node = nodeGroup.node;
                var text = nodeGroup.text;

                var size = _childrenSize(parentNode);
                if(size === 0) {
                    var position = _getPosition(parentNode); 

                    if(!parentNode.attr("root")) {
                        var rzParentNode = _getParentOfRestrictedZone({
                            x : position.x + NODE_SPACE_X,
                            y : position.y
                        });
                        if(rzParentNode) {
                            if(position.y < _getPosition(rzParentNode).y) {
                                var steps = (position.y - _getPosition(_getTopNode(rzParentNode.attr("id"))).y) / NODE_SPACE_Y + 1;
                                for(var i = 0;i < steps;i ++) {
                                    _moveUpAllAbove(parentNode, _getOffspring(rzParentNode));
                                }
                            } else {
                                var steps = (_getPosition(_getBottomNode(rzParentNode.attr("id"))).y - position.y) / NODE_SPACE_Y + 1;
                                for(var i = 0;i < steps;i ++) {
                                    _moveDownAllBelow(parentNode, _getOffspring(rzParentNode));
                                }
                            }
                        }
                    }
                    _add(node, parentNode);
                } else if(size % 2 === 0) {
                    _addOnBottom(node, parentNode);
                    if(_isOverlaped(_getPosition(node))) {
                        _moveDownAllBelow(node, [node]);
                    }
                } else {
                    _addOnTop(node, parentNode);
                    if(_isOverlaped(_getPosition(node))) {
                        _moveUpAllAbove(node, [node]);
                    }
                }

                _growVBranch(parentNode);
                _locateNodeText(text, node);

                if(!id) {
                    _menu.insertChild(_getMenu(parentNode), _getIdWithoutPrefix(node));
                }
                _strengthen();
                _switchRole(node);
            }

            function _getParentOfRestrictedZone(position) {
                var nodes = _getNodeByPosition(position);
                if(nodes.size() > 0) {
                    return _getNodeById(nodes.eq(0).attr("parent-id"));
                } else {
                    var topNodes = context.find("svg[id^='node-'][x='" + position.x + "'][top='true']");
                    var node;
                    $.each(topNodes, function(i, value) {
                        var topNode = $(value);
                        var parentNode = _getNodeById(topNode.attr("parent-id"));
                        var bottomNode = _getBottomNode(parentNode.attr("id"));
                        if(_getPosition(topNode).y <= position.y && _getPosition(bottomNode).y >= position.y) {
                            node = parentNode;
                        }
                    });
                    return node;
                }
            }

            function _getOffspring(node) {
                var id = node.attr("id");
                var offspring = [];
                var children = _getNodesByParentId(id);
                $.each(children, function(i, value) {
                    offspring.push($(value));
                });
                var result = $.merge([], offspring);
                for(var i = 0;i < offspring.length;i ++) {
                    result = $.merge(result, _getOffspring(offspring[i]));
                }
                return result;
            }

            function _insertSiblingNodeAbove(siblingsNode, id) {
                var parentId = siblingsNode.attr("parent-id");
                var nodeGroup = _createNode(parentId, id);
                var node = nodeGroup.node;
                var text = nodeGroup.text;

                node.attr("x", parseInt(siblingsNode.attr("x")));

                if(siblingsNode.attr("top")) {
                    siblingsNode.removeAttr("top");
                    node.attr("top",true);
                }
                node.attr("y", parseInt(siblingsNode.attr("y")) - NODE_SPACE_Y);
                _canvas.append(node);
                var position = _getPosition(node);
                if(_isOverlaped(position)) {
                    _moveUpAllAbove(node, [node]);
                }

                _growVBranch(_getNodeById(parentId));
                _locateNodeText(text, node);

                if(!id) {
                    _menu.insertSiblingAbove(_getMenu(siblingsNode), _getIdWithoutPrefix(node));
                }
                _strengthen();
                _switchRole(node);
            }

            function _insertSiblingNodeBelow(siblingsNode, id) {
                var parentId = siblingsNode.attr("parent-id");
                var nodeGroup = _createNode(parentId, id);
                var node = nodeGroup.node;
                var text = nodeGroup.text;

                node.attr("x", parseInt(siblingsNode.attr("x")));

                if(siblingsNode.attr("bottom")) {
                    siblingsNode.removeAttr("bottom");
                    node.attr("bottom",true);
                }
                node.attr("y", parseInt(siblingsNode.attr("y")) + NODE_SPACE_Y);
                _canvas.append(node);
                var position = _getPosition(node);
                if(_isOverlaped(position)) {
                    _moveDownAllBelow(node, [node]);
                }

                _growVBranch(_getNodeById(parentId));
                _locateNodeText(text, node);

                if(!id) {
                    _menu.insertSiblingBelow(_getMenu(siblingsNode), _getIdWithoutPrefix(node));
                }
                _strengthen();
                _switchRole(node);
            }

            function _strengthen() {
                _strengthenByTop();
                _strengthenByBottom();
                _strengthenByRight();
                _locateRootNodeText(_getText(_getRoot()), _getRoot());
            }

            function _strengthenByBottom() {
                var height = _canvas.height();
                var bottomNodes = context.find("svg[bottom]").filter(function(index) {
                    return _getPosition(context.find("svg[bottom]").eq(index)).y + 50 > height;
                });

                if(bottomNodes.size() > 0) {
                    var maxY = Number.MIN_VALUE;
                    $.each(bottomNodes, function(i, value) {
                        var position = _getPosition($(value));
                        if(position.y > maxY) {
                            maxY = position.y;
                        }
                    });
                    _canvas.attr("height", maxY + 100);
                }
            }

            function _strengthenByTop() { 
                var height = _canvas.height();
                var topNodes = context.find("svg[top]").filter(function(index) {
                    return _getPosition(context.find("svg[top]").eq(index)).y - 50 < 0;
                });

                if(topNodes.size() > 0) {
                    _canvas.attr("height", height + 100);
                    for(var i = 0;i < 2;i ++) {
                        $.each(context.find("svg[id^='node']"), function(i, value) {
                            _moveDown($(value));
                        });
                    }
                }
            }

            function _strengthenByRight() {
                var width = _canvas.width();
                var nodes = context.find("svg[id^='node']");
                var maxX = Number.MIN_VALUE;
                $.each(nodes, function(i, value) {
                    var position = _getPosition($(value));
                    if(position.x > maxX) {
                        maxX = position.x;
                    }
                });
                if(maxX > width - 150) {
                    _canvas.attr("width", width + 200);
                }
            }

            function _shrink() {
                _shrinkByTop();
                _shrinkByBottom();
                _shrinkByRight();
                _locateRootNodeText(_getText(_getRoot()), _getRoot());
            }

            function _shrinkByTop() {
                var height = _canvas.height();
                if(height <= 500) {
                    return;
                }
                var topNodes = context.find("svg[top]").sort(function(node1, node2) {
                    return _getPosition($(node1)).y - _getPosition($(node2)).y;
                });
                if(topNodes.size() > 0 && _getPosition(topNodes.eq(0)).y > 150) {
                    $.each(context.find("svg[id^='node']"), function(i, value) {
                        _moveUp($(value));
                    });
                    _canvas.attr("height", height - 50);
                }
            }

            function _shrinkByBottom() {
                var height = _canvas.height();
                if(height <= 500) {
                    return;
                }
                var bottomNodes = context.find("svg[bottom]").sort(function(node1, node2) {
                    return _getPosition($(node2)).y - _getPosition($(node1)).y;
                });

                if(bottomNodes.size() > 0 && _getPosition(bottomNodes.eq(0)).y + 100 < height) {
                    _canvas.attr("height", height - 50);
                }
            }

            function _shrinkByRight() {
                var width = _canvas.width();
                if(width < 1000) {
                    return;
                }
                var nodes = context.find("svg[id^='node']");
                var maxX = Number.MIN_VALUE;
                $.each(nodes, function(i, value) {
                    var position = _getPosition($(value));
                    if(position.x > maxX) {
                        maxX = position.x;
                    }
                });
                if(maxX < width - 150) {
                    _canvas.attr("width", width - 100);
                }
            }

            function _getMenu(node) {
                return _menu.getMenuById("menu-" + _getIdWithoutPrefix(node));
            }

            function _getIdWithoutPrefix(node) {
                return node.attr("id").substring(5);
            }

            function _getTopNode(parentId) {
                return context.find("svg[parent-id='" + parentId + "'][top]");
            }

            function _getBottomNode(parentId) {
                return context.find("svg[parent-id='" + parentId + "'][bottom]");
            }

            function _add(node, parentNode) {
                if(parentNode.attr("root")) {
                    node.attr("x", parseInt(parentNode.attr("x")) + 147);
                } else {
                    node.attr("x", parseInt(parentNode.attr("x")) + NODE_SPACE_X);
                }
                node.attr("y", parentNode.attr("y"));
                node.attr("top", true);
                node.attr("bottom", true);
                _canvas.append(node);
            }

            function _addOnTop(node, parentNode) {
                var parentId = parentNode.attr("id");
                var topNode = _getTopNode(parentId);
                topNode.removeAttr("top");
                node.attr("x", parseInt(topNode.attr("x")));
                node.attr("y", parseInt(topNode.attr("y")) - NODE_SPACE_Y);
                node.attr("top", true);
                topNode.before(node);
            }

            function _addOnBottom(node, parentNode) {
                var parentId = parentNode.attr("id");
                var bottomNode = _getBottomNode(parentId);
                bottomNode.removeAttr("bottom");
                node.attr("x", parseInt(bottomNode.attr("x")));
                node.attr("y", parseInt(bottomNode.attr("y")) + NODE_SPACE_Y);
                node.attr("bottom", true);
                bottomNode.after(node);
            }

            function _remove(node, cascade) {
                var parentId = node.attr("parent-id");
                var siblings = _getNodesByParentId(parentId);
                var nephews = 0;
                var aboveSiblings = [];
                var belowSiblings = [];
                var position = _getPosition(node);
                // $.each(siblings, function(i, value) {
                //     if($(value).attr("id") != node.attr("id")) {
                //         nephews = nephews + _childrenSize($(value));
                //         var y = _getPosition($(value)).y
                //         if(position.y < y) {
                //             belowSiblings.push($(value));
                //         } else {
                //             aboveSiblings.push($(value));
                //         }
                //     }
                // });
                // if(nephews === 0 && (aboveSiblings.length > 0 || belowSiblings.length > 0)) {
                //     if(aboveSiblings > belowSiblings) {
                //         _moveDownAllAbove(node, [node]);
                //     } else {
                //         _moveUpAllBelow(node, [node]);
                //     }
                // }

                var parentNode = _getNodeById(parentId);

                _getText(node).remove();
                node.remove();

                siblings = _getNodesByParentId(parentId);
                if(siblings.size() > 0) {
                    if(_getTopNode(parentId).size() === 0) {
                        var minY = Number.MAX_VALUE;
                        $.each(siblings, function(i, value) {
                            y = _getPosition($(value)).y
                            if(minY > y) {
                                minY = y;
                            }
                        });
                        _getNodeByPosition({
                            x : position.x,
                            y : minY
                        }).attr("top", true);
                    } 
                    if(_getBottomNode(parentId).size() === 0) {
                        var maxY = Number.MIN_VALUE;
                        $.each(siblings, function(i, value) {
                            y = _getPosition($(value)).y
                            if(maxY < y) {
                                maxY = y;
                            }
                        });
                        _getNodeByPosition({
                            x : position.x,
                            y : maxY
                        }).attr("bottom", true);
                    }
                }

                if(siblings.size() <= 1) {
                    if(siblings.size() === 0) {
                        var branch = parentNode.find("[role='branch'][expanded='true']");
                        branch.attr("expanded", "false");
                    }
                    _chopVBranch(parentNode);
                    _growVBranch(parentNode);
                } else {
                    _growVBranch(parentNode);
                }

                if(cascade) {
                    _menu.remove(_getMenu(node));
                }

                _toolbar.removeRole(_getRoleCode(node));
                var nodes = context.find("svg[id^='node-'][root!='true']");
                if(nodes.size() > 0) {
                    _switchRole(nodes.eq(0));
                } else {
                    _toolbar.hide();
                }
                _shrink();
                _focus();
            }

            function _growVBranch(parentNode) {
                var offset = 17;
                var parentId = parentNode.attr("id");
                var childrenSize = _childrenSize(parentNode);
                if(childrenSize > 1) {
                    var topNode = _getTopNode(parentId);
                    var bottomNode = _getBottomNode(parentId);
                    var x = parseInt(topNode.attr("x"));
                    var fromY = parseInt(topNode.attr("y")) + offset;
                    var toY = parseInt(bottomNode.attr("y")) + offset;
                    var parentY = parseInt(parentNode.attr("y")) + offset;
                    if(fromY > parentY) {
                        fromY = parentY;
                    }
                    if(toY < parentY) {
                        toY = parentY;
                    }
                    var branch = parentNode.siblings("[branch='v'][h-id='" + parentId + "']");
                    if(branch.size() === 0) {
                        branch = _createBranch();
                    }
                    branch.find("path").attr("d", "M " + x + " " + fromY + " L " + x + " " + toY);
                } else if(childrenSize === 1) {
                    var branch = parentNode.siblings("[branch='v'][h-id='" + parentId + "']");
                    if(branch.size() === 0) {
                        branch = _createBranch();
                    }
                    var node = _getNodesByParentId(parentId).eq(0);
                    var x = parseInt(node.attr("x"));
                    var fromY = parseInt(parentNode.attr("y")) + offset;
                    var toY = parseInt(node.attr("y")) + offset;
                    branch.find("path").attr("d", "M " + x + " " + fromY + " L " + x + " " + toY);
                }

                function _createBranch() {
                    var branch = $('<svg><path stroke-dasharray="3,3" style="stroke:#999;stroke-width:1" /></svg>');
                    branch.attr("branch", "v");
                    branch.attr("h-id", parentId);
                    parentNode.after(branch);
                    return branch;
                }
            }

            function _chopVBranch(parentNode) {
                parentNode.next("[branch='v']").remove();
            }

            function _getNodeGroup(id) {
                var node;
                if(id.indexOf("text-") === 0) {
                    return {
                        text : $("#" + id),
                        node : $("#node-" + id.substring(id.indexOf("text-") + 5))
                    };
                } else {
                    return {
                        text : $("#text-" + id.substring(id.indexOf("node-") + 5)),
                        node : $("#" + id)
                    };
                }
            }

            function _getRoot() {
                return context.find("svg[root='true']");
            }

            function _getText(node) {
                var id = node.attr("id");
                return context.find("#text-" + id.substring(id.indexOf("node-") + 5));
            }

            function _getNodeById(id) {
                return context.find("svg[id='" + id + "']");
            }

            function _getNodeByPosition(position) {
                return context.find("svg[x='" + position.x + "'][y='" + position.y + "']");
            }

            function _getNodesByParentId(parentId) {
                return context.find("svg[parent-id='" + parentId + "']");
            }

            function _getPosition(node) {
                return {
                    x : parseInt(node.attr("x")),
                    y : parseInt(node.attr("y"))
                };
            }

            function _childrenSize(node) {
                return _getNodesByParentId(node.attr("id")).size();
            }

            function _hasChildren(node) {
                return _childrenSize(node) > 0;
            }

            function _isOverlaped(position) {
                return _getNodeByPosition(position).size() > 1;
            }

            function _getColonizer(position, aggressorId) {
                return context.find("svg[x='" + position.x + "'][y='" + position.y + "'][id!='" + aggressorId + "']");
            }

            function _moveUpAllAbove(node, except) {
                _moveHalf(node, except, true);
            }

            function _moveDownAllBelow(node, except) {
                _moveHalf(node, except, false);
            }

            function _moveUpAllBelow(node, except) {
                _moveHalf(node, except, true, true);
            }

            function _moveDownAllAbove(node, except) {
                _moveHalf(node, except, false, true);
            }

            function _moveHalf(node, except, up, reverse) {
                var position = _getPosition(node);
                var nodes = context.find("svg[id^='node-']");
                $.each(nodes, function(i, value) {
                    if(_allow($(value))) {
                        if((up && _getPosition($(value)).y <= position.y) 
                            || (!up && _getPosition($(value)).y >= position.y)) {
                            if(reverse) {
                                _move($(value), !up);
                            } else {
                                _move($(value), up);
                            } 
                        }
                    }
                });

                function _allow(target) {
                    var allow = true;
                    for(var i = 0;i < except.length;i ++) {
                        if(target.attr("id") == except[i].attr("id")) {
                            allow = false;
                        }
                    }
                    return allow;
                }
            }

            function _moveUp(node) {
                _move(node, true);
            }

            function _moveDown(node) {
                _move(node, false);
            }

            function _move(node, up) {
                if(up) {
                    node.attr("y", parseInt(node.attr("y")) - NODE_SPACE_Y);
                } else {
                    node.attr("y", parseInt(node.attr("y")) + NODE_SPACE_Y);
                }
                if(node.attr("root")) {
                    _locateRootNodeText(_getText(node), node);
                } else {
                    _locateNodeText(_getText(node), node);
                }
                _growVBranch(_getNodeById(node.attr("parent-id")));
            }

            function _isNephewOfUpperBrother(y, nephew) {
                var brother = _getNodeById(nephew.attr("parent-id"));
                return parseInt(y) > parseInt(brother.attr("y"));
            }

            var NODE_WIDTH = 12;
            function _locateRootNodeText(text, node) {
                var canvasLeft = parseInt(_canvas.css("left"));
                var canvasTop = parseInt(_canvas.css("top"));
                var nodeOffsetX = parseInt(node.attr("x"));
                var nodeOffsetY = parseInt(node.attr("y"));

                text.css("left", canvasLeft + nodeOffsetX + 105 - NODE_WIDTH - text.width());
                text.css("top", canvasTop + nodeOffsetY + NODE_WIDTH + 6 - text.height() / 2);
            }

            function _locateNodeText(text, node) {
                var canvasLeft = parseInt(_canvas.css("left"));
                var canvasTop = parseInt(_canvas.css("top"));
                var nodeOffsetX = parseInt(node.attr("x"));
                var nodeOffsetY = parseInt(node.attr("y"));

                var left = canvasLeft + nodeOffsetX + 30 + NODE_WIDTH + 8;
                var top = canvasTop + nodeOffsetY + NODE_WIDTH + 6 - text.height() / 2;
                text.css("left", left);
                text.css("top", top);
                text.next("textarea").css("left", left + 1);
                text.next("textarea").css("top", top + 1);
            }

            function _onEditText(text, node, locate) {
                if(text.find("textarea").size() > 0) {
                    return;
                }
                var textarea = $('<textarea col="6" row="3" class="node-text" maxlength="100"></textarea>');
                var textContent = $.trim(text.find("span").text());
                textarea.val(textContent);
                textarea.data("origin-text", textContent);
                text.after(textarea);
                text.css("height", "78px");
                locate(text, node);
                textarea.focus();
                textarea.select();
                text.unbind("dblclick");
                $("body, div.view, svg").one("click.save-text", function(event) {
                    _onSaveText();
                });
                $("body, div.view, svg").one("keyup.save-text", function(event) {
                    if(event.keyCode === 27) {
                        _onSaveText();
                    }
                });
                textarea.on("click", function(event) {
                    event.stopPropagation();
                });
                textarea.on("mousedown", function(event) {
                    event.stopPropagation();
                });
                textarea.on("blur", function(event) {
                    event.stopPropagation();
                    _onSaveText();
                });
                textarea.on("keyup", function(event) {
                    if(event.keyCode === 13 || event.keyCode === 110) {
                        _onSaveText();
                        return false;
                    }
                });

                function _onSaveText() {
                    text.removeClass("invalid");
                    var content = textarea.val();
                    content = $.trim(content.replace(new RegExp("\n"), ""));
                    if(content.length === 0) {
                        text.addClass("invalid");
                        textarea.focus();
                        $.toaster("角色名称不能为空！", "blank-role-name");
                    } else if(content == textarea.data("origin-text") 
                        || (node.data("role") && content == node.data("role").name)) {
                        _doSave();
                    } else {
                        _globalValidateRoleName(content, function(success) {
                            if(success) {
                                _doSave();
                            } else {
                                text.addClass("invalid");
                                textarea.focus();
                                $.toaster("输入的角色已经存在，换个名称重试！", "duplicate-role-name");
                            }
                        });
                    }
                    
                    function _doSave() {
                        text.find("span").text(content);
                        text.css("height", "");
                        textarea.remove();
                        locate(text, node);
                        text.dblclick(function(event) {
                            event.stopPropagation();
                            _onEditText(text, node, _locateNodeText);
                        });
                        $("body, div.view, svg").off("click.save-text");
                        $("body, div.view, svg").off("keyup.save-text");
                        _saveMenuText(node);
                    }
                }
            }

            function _saveMenuText(node) {
                var text =  _getText(node).find("span").text();
                _menu.setText(_getMenu(node), text);
                _toolbar.setRoleName(text);
            }

            function _setText(node, text) {
                _getText(node).find("span").text(text);
                _locateNodeText(_getText(node), node);
            }

            function _onEditNode(node) {
                if(_toolbar.isHidden()) {
                    _toolbar.show();

                    if(_toolbar.isDraggable()) {
                        var position = _getPosition(node);

                        var left = parseInt($("div.side-bar").css("left"));
                        var offsetX = left === 0 ? 280 : 0;
                        var offsetY = 80;
                        var minX = offsetX;
                        var maxX = $(window).width() - 280;
                        var minY = offsetY;
                        var maxY = $(window).height() - 300;

                        var x = offsetX + position.x + 180 - $("div.main").scrollLeft();
                        if(x < minX) {
                            x = minX;
                        }
                        if(x > maxX) {
                            x = maxX;
                        }
                        var y = offsetY + position.y - 140 - $("div.main").scrollTop();
                        if(y < minY) {
                            y = minY;
                        }
                        if(y > maxY) {
                            y = maxY;
                        }
                        _toolbar.setPosition({
                            x : x,
                            y : y
                        });
                    }
                }
                _switchRole(node);
            }

            function _switchRole(node) {
                var id = _getIdWithoutPrefix(node);
                var parentNode = _getNodeById(node.attr("parent-id"));
                var parent;
                if(parentNode.attr("root")) {
                    parent = "0";
                } else if(node.attr("parent")) {
                    parent = node.attr("parent");
                } else {
                    parent = "role-" + _getIdWithoutPrefix(parentNode);
                }
                _toolbar.switchRole({
                    code : _getRoleCode(node),
                    name : _getText(node).find("span").text(),
                    roleType : node.data("role") ? node.data("role").roleType : "PROTECTED",
                    parent : parent,
                    id : id
                });
            }

            function _getRoleCode(node) {
                if(node.attr("code")) {
                    return node.attr("code");
                } else {
                    return "role-" + _getIdWithoutPrefix(node);
                }
            }

            function _onShowContextMenu(obj, event, locate) {
                event.preventDefault();
                event.stopPropagation();
                var nodeGroup = _getNodeGroup(obj.attr("id"));

                _hideContextMenu();
                _menu.hideContextMenu();
                var menu = $($("#node-text-menu").get(0).outerHTML);
                var left = $("div.side-bar").css("left");
                left = left.substring(0, left.indexOf("px"));
                menu.css("left", event.pageX - left - 280 + $("div.main").scrollLeft());
                menu.css("top", event.pageY - 84 + $("div.main").scrollTop());
                menu.find("li[role='move-up']").remove();
                menu.find("li[role='move-down']").remove();
                menu.find("li[role='fold-children']").remove();
                menu.find("li[role='unfold-children']").remove();
                menu.find("li.divider").eq(1).remove();
                if(obj.data("root")) {
                    menu.find("li.divider").remove();
                    menu.find("li[role='insert-sibling-above']").remove();
                    menu.find("li[role='insert-sibling-below']").remove();
                    menu.find("li[role='remove-node']").remove();
                    menu.find("li[role='edit-text']").remove();
                    menu.find("li[role='edit-node']").remove();
                }
                if(_childrenSize(nodeGroup.node) > 0) {
                    menu.find("li[role='remove-node']").remove();
                }
                context.append(menu);

                menu.find("li").on("click", function(event) {
                    event.preventDefault();
                    event.stopPropagation();
                    
                    if($(this).attr("role") === "edit-text") {
                        _onEditText(nodeGroup.text, nodeGroup.node, locate);
                    } else if($(this).attr("role") === "insert-child-node") {
                        _insertChildNode(nodeGroup.node);
                    } else if($(this).attr("role") === "insert-sibling-above") {
                        _insertSiblingNodeAbove(nodeGroup.node);
                    } else if($(this).attr("role") === "insert-sibling-below") {
                        _insertSiblingNodeBelow(nodeGroup.node);
                    } else if($(this).attr("role") === "remove-node") {
                        _remove(nodeGroup.node, true);
                    } else if($(this).attr("role") === "edit-node") {
                        _onEditNode(nodeGroup.node);
                    }
                    _hideContextMenu()
                });
            
                $("body, div.view, svg").one("click.node-context-menu", function() {
                    _hideContextMenu()
                });
                $("body").one("keyup.node-context-menu", function(event) {
                    if(event.keyCode === 27) {
                        _hideContextMenu()
                    }
                });
            }

            function _onSelect(node) {
                context.find("a.node-text").removeClass("selected");
                if(node) {
                    _getText(node).addClass("selected");
                }
            }

            function _getIdByCode(code) {
                if(code.indexOf("role-") === 0) {
                    return code.substring(5);
                } else {
                    var node = context.find("svg[code='" + code + "']");
                    if(node.size() > 0) {
                        return context.find("svg[code='" + code + "']").attr("id").substring(5);
                    }
                }
            }

            function _focus() {
                context.find("a.node-text:not(.root)").eq(0).trigger("click");
            }

            function _isNodeOnEditing() {
                return context.find("a.node-text").next("textarea.node-text").size() > 0;
            }

            function _hideContextMenu() {
                $("div.view ul.context-menu").remove();
            }

            function _getNodeByCode(code) {
                if(code.indexOf("-") > 0) {
                    return _getNodeById("node-" + code.substring(5));
                } else {
                    return context.find("svg[code='" + code + "']");
                }
            }

            function _invalidNode(code) {
                _getText(_getNodeByCode(code)).addClass("invalid");
            }

            function _clearError() {
                context.find("a.node-text").removeClass("valid");
            }

            function _getSelectedNode() {
                if(context.find("a.node-text.selected").size() > 0) {
                    return _getNodeGroup(context.find("a.node-text.selected").attr("id")).node;
                }
            }

            return {
                initialize : _init,
                setMenu : _setMenu,
                createRootNode : _createRootNode,
                insertChildNode : _insertChildNode,
                insertSiblingNodeAbove : _insertSiblingNodeAbove,
                insertSiblingNodeBelow : _insertSiblingNodeBelow,
                remove : _remove,
                onEditNode : _onEditNode,
                getNodeById : _getNodeById,
                setText : _setText,
                select : _onSelect,
                switchRole : _switchRole,
                getIdByCode : _getIdByCode,
                focus : _focus,
                isNodeOnEditing : _isNodeOnEditing,
                hideContextMenu : _hideContextMenu,
                getNodeByCode : _getNodeByCode,
                invalidNode : _invalidNode,
                clearError : _clearError,
                childrenSize : _childrenSize,
                getSelectedNode : _getSelectedNode
            }
        }

        return {
            initialize : _init
        }
    }
}));