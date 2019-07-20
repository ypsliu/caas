define(["app/common", "jquery", "jquery.utility", "jquery.dropdown",
	"jquery.colorbox","jquery.prompt"], function(common, $) {

	var _userCode = common.getQueryValue("userCode");
	var _app = {};
	var _roleUrl;

	(function() {
		common.getUser(function(user) {
			var url = window.location.href;
			var isSignup=false;
			var isAppRole=false;
			if(url.indexOf("signup.html")>0){
				isSignup=true;
			}

			if(url.indexOf("app-role.html")>0){
				isAppRole=true;
			}

			if(user) {
				_roleUrl = "/oauth2/user/" + user.code + "/app/{appCode}/role/apply";
			} else {
				_roleUrl = "/common/app/{appCode}/role";
			}
			var appKey = common.getQueryValue("appKey");
			if(appKey) {
				_getApp(appKey);
			} else {
				if(isAppRole){
					_getUserApps();
				}
				if(isSignup){
					_getApps();
				}
			}
		});

		$("#save").on("click", function() {

			// var data = _buildData();
			// alert('data=>'+JSON.stringify(data));
			var selectedApp=_getSelectedApps()
			if(selectedApp.length > 0) {
				$.ajax({
					url : "/oauth2/user/app/access",
					type : "POST",
					contentType : "application/json",
					data : JSON.stringify(selectedApp),
					success : function(result, textStatus, xhr) {
						$.info("申请已提交", "回到首页", function() {
							window.location.href = "/";
						});
					},
					error : function(xhr, textStatus, errorThrown) {
					}
				});
			}
		});
	})();
  function _getSelectedApps(){
		var selectedApp=[];
		$("input[name='app_apply']").each(function(){
			 if($(this).is(':checked')==true){
				  var appcode=$(this).val();
          selectedApp.push(appcode);
			 }
		});
    return selectedApp;
	}
	function _buildData() {
		var data = [];
		for(var appCode in _app) {
			var app = _app[appCode];
			var roles = [];
			for(var roleCode in app.role) {
				var role = app.role[roleCode];
				if(role.roleStatus === "APPLYING") {
					roles.push({
						appCode : _app[appCode].code,
						code : role.code,
						name : role.name
					});
				}
			}
			if(roles.length > 0) {
				data.push({
					userCode : _userCode,
					appCode : appCode,
					appName : app.name,
					role : roles
				});
			}
		}
		return data;
	}

	function _getApp(appKey) {
		$.ajax({
			url : "/common/info/app/" + appKey,
			success : function(app, textStatus, xhr) {
				$("#appName").show();
				$("#appName").text(app.name);
				_app[app.code] = app;
				_getUserAppRoles(_userCode, app.code, function(appCode) {
					_listRole(appCode);
				});
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	}

	function _getApps() {
		$.ajax({
			url : "/common/app",
			success : function(apps, textStatus, xhr) {
				_dropdown(apps);
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	}
	function _getUserApps() {
		$.ajax({
			url : "/oauth2/app",
			success : function(apps, textStatus, xhr) {
				_dropdown(apps);
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	}
	function _dropdown(apps) {
		$("#appList").show();
		var $appList =$("#appList ul.app-list");
		$appList.html("");

		for(var i = 0;i < apps.length;i ++) {
			var app = apps[i];
			var hasAppRole=app.comment;
			var option;
			if(hasAppRole=="1"){
				 option = $('<li  class="list-group-item"><div class="checkbox"> <label><input type="checkbox" name="app_apply" value="'+app.code+'" checked="checked" disabled>'+app.name+'</label></div></li>');
			}else{
			   option = $('<li  class="list-group-item"><div class="checkbox"> <label><input type="checkbox" name="app_apply" value="'+app.code+'">'+app.name+'</label></div></li>');
			}

		//	option.find("a").attr("data-value", app.code);
		//	option.find("label").text(app.name);

			$appList.append(option);
    //  $("#dropdownapp").append(option);
			_app[app.code] = app;
		}
		//$("#appList").dropdown();
	  //	$("input[name='appCode']").on("change", function() {
			//var appCode = $(this).val();
		//  $("#save").removeAttr("disabled");
			// if(!_app[appCode].role) {
			// 	_getUserAppRoles(_userCode, appCode, function() {
			// 		_listRole(appCode);
			// 	});
			// } else {
			// 	_listRole(appCode);
			// }
	//	});
		//appList.show();
	}

	function _getUserAppRoles(userCode, appCode, callback) {
		var url = _roleUrl.replace(new RegExp("\\{appCode\\}"), appCode);
		$.ajax({
			url : url,
			success : function(roles, textStatus, xhr) {
				var roleMap = {};
				for(var i = 0;i < roles.length;i ++) {
					var role = roles[i];
					roleMap[role.code] = roles[i];
				}
				_app[appCode].role = roleMap;
				callback(appCode);
			},
			error : function(xhr, textStatus, errorThrown) {
			}
		});
	}

	function _listRole(appCode) {
		var roleMap = _app[appCode].role;
		$("ul.role-list").html("");
		for(var code in roleMap) {
			var role = roleMap[code];
			var roleLi = $('<li><label><input type="checkbox" name="roleCode"/> <span></span></label></li>');
			roleLi.find("input[type='checkbox']").val(code);
			roleLi.find("input[type='checkbox']").data("appCode", appCode);
			roleLi.find("span").text(role.name);
			if(role.roleStatus === "PENDING") {
				roleLi.find("input[type='checkbox']").prop("checked", true);
				roleLi.find("input[type='checkbox']").prop("disabled", true);
				roleLi.append($('<span class="label label-warning">审批中...</span>'));
			} else if(role.roleStatus === "CONFIRMED") {
				roleLi.find("input[type='checkbox']").prop("checked", true);
				roleLi.find("input[type='checkbox']").prop("disabled", true);
				roleLi.append($('<span class="label label-success">审批通过</span>'));
			} else if(role.roleStatus === "APPLYING") {
				roleLi.find("input[type='checkbox']").prop("checked", true);
			}
			$("ul.role-list").append(roleLi);
			roleLi.find("input[type='checkbox']").on("change", function() {
				var appCode = $(this).data("appCode");
				var currentOption = $("#appList ul.app-list li a[data-value='" + appCode + "']");
				if($(this).is(":checked")) {
					_app[appCode].role[$(this).val()].roleStatus = "APPLYING";
					if(currentOption.find("span.glyphicon").size() === 0) {
						currentOption.append($('<span class="glyphicon glyphicon-paperclip" aria-hidden="true"></span>'));
					}
					$("#save").removeAttr("disabled");
				} else {
					_app[appCode].role[$(this).val()].roleStatus = null;
					currentOption.find("span.glyphicon").remove();
					if($("#appList ul.app-list li a span.glyphicon").size() === 0) {
						$("#save").attr("disabled", "true");
					}
				}
			});
		}
	}

	return {
		buildData : _buildData
	}
});
