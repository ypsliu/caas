define(["app/constants", "app/components/admin-user", "app/components/logout", "jquery"], function(constants, adminUser, logout, $) {

    function _reset() {
        adminUser.clear();
        _asAnonymous();
    }

    function _refresh(callback) {
        adminUser.user(function(user) {
            if(user.role === constants.ROLE_SUPER_ADMIN) {
                _asSuperAdmin(user);
            } else if(user.role === constants.ROLE_ADMIN_USER) {
                _asAdminUser(user.app ? user.app.status : "NOAPPLY",user);
            } else if(user.role === constants.ROLE_ANONYMOUS) {
                _asAnonymous();
            }
            if(callback) {
                callback();
            }
        });


    }

    function _asSuperAdmin(user) {
        require(["text!app/components/nav/nav-super-admin-tpl.html",
            "text!app/components/nav/side-nav-super-admin-tpl.html"], function(navTpl, sideNavTpl) {
            $("#header").html(_.template(navTpl));
            $("#sideNav").html(_.template(sideNavTpl));
            $("#sideNav").show();
           _rebuildAppListPanel(true,user);
           _reBuildMenus(user);
            _bind();
        });
    }
   function _reBuildMenus(user){
     //alert("_reBuildMenus");
    if(user.userType=="IPA"){
       $("#changepwd").hide();
    }
   }
    function _asAdminUser(appStatus,user) {
         _reBuildMenus(user);
        require(["text!app/components/nav/nav-admin-user-tpl.html",
            "text!app/components/nav/side-nav-admin-user-tpl.html"], function(navTpl, sideNavTpl) {
            $("#header").html(_.template(navTpl));
            $("#applistpanel").show();
            _rebuildAppListPanel(false,user);
            if(appStatus === "CONFIRMED") {
                $("#sideNav").html(_.template(sideNavTpl));
                $("#sideNav").show();
                $("#role-tree").on("click", function() {
                    window.open("/rtree/rtree.html", "roleTree");
                });
            }
            _bind();
        });
    }

    function _rebuildAppListPanel(isSuperAdmin,user){
      if(isSuperAdmin){
         $("#applistpanel").hide();
      }else{
         if(user.app &&user.app.name){
           $("#currentapp").text(user.app.name);
         }

         $("#applist").empty();
         if(user.appList){
           var applist=user.appList;
           for(var i=0;i<applist.length;i++){
            $("#applist").append('<li style="list-style:none"><a class="appItem" style="list-style:none" href="javascript:void(0)" value="'+applist[i].code+'">'+applist[i].name+'</a></li>');
           }
 $("#applistpanel").show();
         }




      }
      _bindSwitchAppAction();
    }

    function _bindSwitchAppAction(){
      $(".appItem").bind('click',function(){
        var appname=$(this).text();
        var appCode=$(this).attr("value");
        var currentapp =$("#currentapp").text();
        $("#currentapp").text(appname);
        if(appname==currentapp){
          return ;
        }
        _switchApp(appCode);
      });
    }
  function _switchApp(appCode){
    $.ajax({
          url : '/admin/switchApp/'+appCode,
          type : 'POST',
          contentType : "application/json",
          success : function(result, textStatus, xhr) {
              	window.location.reload();
          },
          error : function(xhr, textStatus, errorThrown) {
            console.log(xhr);
            if(xhr.status === 401) {
            //  $("#loginError").show();

            }
          }
        });
  }

    function _asAnonymous() {
        require(["text!app/components/nav/nav-before-login-tpl.html"], function(navTpl) {
            $("#header").html(_.template(navTpl));
            $("#sideNav").hide();
        });
    }

    function _bind() {
        $("#logout").on("click", function() {
            logout.logout(function() {
                _reset();
                var currentUrl = window.location.href;
                if(currentUrl.match(new RegExp("#home$")) || currentUrl.match(new RegExp("#$")) || currentUrl.indexOf("#") < 0) {
                    Backbone.history.loadUrl(Backbone.history.fragment);
                } else {
                    window.location.href = "#";
                }
            });
        });
    }

    return {
        refresh  : _refresh,
        reset : _reset
    }
});
