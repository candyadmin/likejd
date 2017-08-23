$(function(){
	//修改权限模块
	$("[nc_type='privacydiv']").live('mouseover',function(){
		$(this).find("[nc_type='privacytab']").show();
	});
	$("[nc_type='privacydiv']").live('mouseout',function(){
		$(this).find("[nc_type='privacytab']").hide();
	});
	$("[nc_type='privacyoption']").live('click',function(){
		var obj = $(this);
		var data_str = $(this).attr('data-param');
	    eval( "data_str = "+data_str);
	    var op = "editprivacy";
	    switch(data_str.op){
	    	case 'store':
	    		op = "storeprivacy";
	    		break;
	    	default:
	    		op = "editprivacy";
	    		break;
	    }
	    ajaxget('index.php?act=member_snsindex&op='+op+'&id='+data_str.sid+'&privacy='+data_str.v);
	});
	//表单权限模块
	$("[nc_type='formprivacydiv']").live('mouseover',function(){
		$(this).find("[nc_type='formprivacytab']").show();
	});
	$("[nc_type='formprivacydiv']").live('mouseout',function(){
		$(this).find("[nc_type='formprivacytab']").hide();
	});
	//选择权限
	$("[nc_type='formprivacyoption']").die('click').live('click',function(){
		var data_str = $(this).attr('data-param');
	    eval( "data_str = "+data_str);
	    var hiddenid = "privacy";
	    if(data_str.hiddenid !='' && data_str.hiddenid != undefined){
	    	hiddenid = data_str.hiddenid;
        }
	    //$("[nc_type='formprivacytab']").find('span').removeClass('selected');
	    $(this).parent().find('span').removeClass('selected');
	    $(this).find('span').addClass('selected');
	    $("#"+hiddenid).val(data_str.v);
	});
	//分享单个商品
	$("[nc_type='sharegoods']").bind('click',function(){
		var data_str = $(this).attr('data-param');
	    eval( "data_str = "+data_str);
	    ajaxget('index.php?act=member_snsindex&op=sharegoods_one&dialog=1&gid='+data_str.gid);
	    
	});
	//提交分享商品表单
	$("#weibobtn_goods").die('click').live("click",function(){
		if($("#sharegoods_form").valid()){
			var cookienum = $.cookie(COOKIE_PRE+'weibonum');
			cookienum = parseInt(cookienum);
			if(cookienum >= max_recordnum && $("#sg_seccode").css('display') == 'none'){
				//显示验证码
				$("#sg_seccode").show();
				var nchash = $("#sg_seccode").find("[name='nchash']").val();
				$("#sg_seccode").find("[name='codeimage']").attr('src','index.php?act=seccode&op=makecode&nchash='+nchash+'&t=' + Math.random());
			}else if(cookienum >= max_recordnum && $("#sg_seccode").find("[name='captcha']").val() == ''){
				showDialog('请填写验证码');
			}else{
				ajaxpost('sharegoods_form', '', '', 'onerror');
				//隐藏验证码
				$("#sg_seccode").hide();
				$("#sg_seccode").find("[name='codeimage']").attr('src','');
				$("#sg_seccode").find("[name='captcha']").val('');
			}
		}
		return false;
	});
	//分享单个店铺
	$("[nc_type='sharestore']").bind('click',function(){
		var data_str = $(this).attr('data-param');
	    eval( "data_str = "+data_str);	    
	    ajaxget('index.php?act=member_snsindex&op=sharestore_one&dialog=1&sid='+data_str.sid);
	});
	//删除分享和喜欢的商品
	$("[nc_type='delbtn']").die('click').live('click',function(){
		var data_str = $(this).attr('data-param');
        eval( "data_str = "+data_str);
        showDialog('您确定要删除该信息吗？','confirm', '', function(){
        	ajaxget('index.php?act=member_snsindex&op=delgoods&id='+data_str.sid+'&type='+data_str.tabtype);
			return false;
		});
	});
	//喜欢操作
	$("[nc_type='likebtn']").die('click').live('click',function(){
		var obj = $(this);
		var data_str = $(this).attr('data-param');
        eval( "data_str = "+data_str);
        ajaxget('index.php?act=member_snsindex&op=editlike&inajax=1&id='+data_str.gid);
	});
    //展示和隐藏评论列表
	$("[nc_type='fd_commentbtn']").die('click').live('click',function(){
		var data = $(this).attr('data-param');
        eval("data = "+data);
        //隐藏转发模块
        $('#forward_'+data.txtid).hide();
		if($('#tracereply_'+data.txtid).css("display")=='none'){
			//加载评论列表
	        $("#tracereply_"+data.txtid).load('index.php?act=member_snshome&op=commenttop&type=0&id='+data.txtid+'&mid='+data.mid);
	        $('#tracereply_'+data.txtid).show();	
		}else{
			$('#tracereply_'+data.txtid).hide();
		}
		return false;
	});
    //删除动态
	$("[nc_type='fd_del']").die('click').live('click',function(){
		var data_str = $(this).attr('data-param');
        eval("data_str = "+data_str);
        var url = "index.php?act=member_snsindex&op=deltrace&id="+data_str.txtid;
        if(data_str.type != undefined && data_str.type != ''){
        	url = url+'&type='+data_str.type;
        }
		showDialog('您确定要删除该信息吗？','confirm', '', function(){
			ajaxget(url);
			return false;
		});
	});
	//转发提交
	$("[nc_type='forwardbtn']").die('click').live('click',function(){
		var data = $(this).attr('data-param');
        eval("data = "+data);
		if($("#forwardform_"+data.txtid).valid()){
			var cookienum = $.cookie(COOKIE_PRE+'forwardnum');
			cookienum = parseInt(cookienum);
			if(cookienum >= max_recordnum && $("#forwardseccode"+data.txtid).css('display') == 'none'){
				//显示验证码
				$("#forwardseccode"+data.txtid).show();
				var nchash = $("#forwardseccode"+data.txtid).find("[name='nchash']").val();
				$("#forwardseccode"+data.txtid).find("[name='codeimage']").attr('src','index.php?act=seccode&op=makecode&nchash='+nchash+'&t=' + Math.random());
			}else if(cookienum >= max_recordnum && $("#forwardseccode"+data.txtid).find("[name='captcha']").val() == ''){
				showDialog('请填写验证码');
			}else{
				ajaxpost('forwardform_'+data.txtid, '', '', 'onerror');
				//隐藏验证码
				$("#forwardseccode"+data.txtid).hide();
				$("#forwardseccode"+data.txtid).find("[name='codeimage']").attr('src','');
				$("#forwardseccode"+data.txtid).find("[name='captcha']").val('');
			}
		}
		return false;
	});
	//展示和隐藏转发表单
	$("[nc_type='fd_forwardbtn']").die('click').live('click',function(){
		var data = $(this).attr('data-param');
        eval("data = "+data);
        //隐藏评论模块
        $('#tracereply_'+data.txtid).hide();
		if($('#forward_'+data.txtid).css("display")=='none'){
			//加载评论列表
	        $('#forward_'+data.txtid).show();
	        //添加字数提示
	        if($("#forwardcharcount"+data.txtid).html() == ''){
	        	$("#content_forward"+data.txtid).charCount({
		    		allowed: 140,
		    		warning: 10,
		    		counterContainerID:'forwardcharcount'+data.txtid,
		    		firstCounterText:'还可以输入',
		    		endCounterText:'字',
		    		errorCounterText:'已经超出'
		    	});
	        }
	        //绑定表单验证
			$('#forwardform_'+data.txtid).validate({
				errorPlacement: function(error, element){
					element.next('.error').append(error);
			    },
			    rules : {
			    	forwardcontent : {
			            maxlength : 140
			        }
			    },
			    messages : {
			    	forwardcontent : {
			            maxlength: '不能超过140字'
			        }
			    }
			});
		}else{
			$('#forward_'+data.txtid).hide();
		}
		return false;
	});
	
	// 查看大图
	$('[nc_type="thumb-image"]').die().live('click',function(){
		src = $(this).find('img').attr('src');
		max_src = src.replace('_small.', '_max.');
		$(this).parent().hide().next().children('[nc_type="origin-image"]').append('<img src="'+max_src+'" />').end().show();
	});
	$('[nc_type="origin-image"]').die().live('click',function(){
		$(this).html('').parent().hide().prev().show();
	});
});
function ajaxload_page(objname){
	$('#'+objname).find('.demo').ajaxContent({
		event:'click',
		loaderType:"img",
		loadingMsg:SHOP_TEMPLATES_URL+"/images/transparent.gif",
		target:'#'+objname
	});
}