$(function(){


    //展示和隐藏评论列表
	$("[nc_type='sd_commentbtn']").live('click',function(){
		var $this = $(this);
		$.get('index.php?act=index&op=login', function(result){
		    if(result=='0'){
		    	login_dialog();
		    }else{
				var data = $this.attr('data-param');
		        eval("data = "+data);
		        //隐藏转发模块
		        $('#forward_'+data.txtid).hide();
				if($('#tracereply_'+data.txtid).css("display")=='none'){
					//加载评论列表
			        $("#tracereply_"+data.txtid).load('index.php?act=store_snshome&op=commenttop&id='+data.txtid);
			        $('#tracereply_'+data.txtid).show();	
				}else{
					$('#tracereply_'+data.txtid).hide();
				}
				return false;
		    }
		});
	});
	
	//评论提交
	$("[nc_type='scommentbtn']").live('click',function(){
		var data = $(this).attr('data-param');
        eval("data = "+data);
		if($("#commentform_"+data.txtid).valid()){			
			var cookienum = $.cookie(COOKIE_PRE+'commentnum');
			cookienum = parseInt(cookienum);
			if(cookienum >= MAX_RECORDNUM && $("#commentseccode"+data.txtid).css('display')=="none"){
				//显示验证码
				$("#commentseccode"+data.txtid).show();
				var nchash = $("#commentseccode"+data.txtid).find("[name='nchash']").val();
				$("#commentseccode"+data.txtid).find("[name='codeimage']").attr('src','index.php?act=seccode&op=makecode&nchash='+nchash+'&t=' + Math.random());
			}else if(cookienum >= MAX_RECORDNUM && $("#commentseccode"+data.txtid).find("[name='captcha']").val() == ''){
				showDialog('请填写验证码');
			}else{
				ajaxpost('commentform_'+data.txtid, '', '', 'onerror');
				//隐藏验证码
				$("#commentseccode"+data.txtid).hide();
				$("#commentseccode"+data.txtid).find("[name='codeimage']").attr('src','');
				$("#commentseccode"+data.txtid).find("[name='captcha']").val('');
			}
		}
		return false;
	});

	//删除评论
	$("[nc_type='scomment_del']").live('click',function(){
		var obj = $(this);
		showDialog('您确定要删除该信息吗？','confirm', '', function(){
			var data_str = $(obj).attr('data-param');
	        eval("data_str = "+data_str);
	        ajax_get_confirm('','index.php?act=store_snshome&op=delcomment&scid='+data_str.scid+'&stid='+data_str.stid);
			return false;
		});
	});
	

	//展示和隐藏转发表单
	$("[nc_type='sd_forwardbtn']").live('click',function(){
		var $this = $(this);
		$.get('index.php?act=index&op=login', function(result){
		    if(result=='0'){
		    	login_dialog();
		    }else{
				var data = $this.attr('data-param');
		        eval("data = "+data);
		        //隐藏评论模块
		        $('#tracereply_'+data.txtid).hide();
				if($('#forward_'+data.txtid).css("display")=='none'){
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
		    }
		});
	});


	//转发提交
	$("[nc_type='s_forwardbtn']").live('click',function(){
		var data = $(this).attr('data-param');
		var form = $(this).parents('form:first');
		var seccode = $("#forwardseccode"+data.txtid);
        eval("data = "+data);
		if(form.valid()){
			var cookienum = $.cookie(COOKIE_PRE+'forwardnum');
			cookienum = parseInt(cookienum);
			if(!isNaN(cookienum) && cookienum >= MAX_RECORDNUM){
			    if (seccode.css('display') == 'none') {
	                //显示验证码
	                seccode.show();
	                var nchash = seccode.find("[name='nchash']").val();
	                seccode.find("[name='codeimage']").attr('src','index.php?act=seccode&op=makecode&nchash='+nchash+'&t=' + Math.random());
			    } else if(seccode.find("[name='captcha']").val() == ''){
	                showDialog('请填写验证码');
			    }
			}else{
				ajaxpost('forwardform_'+data.txtid, '', '', 'onerror');
				//隐藏验证码
				seccode.hide().find("[name='codeimage']").attr('src','').end().find("[name='captcha']").val('');
				//隐藏表单
				$('#forward_'+data.txtid).hide();
				$('#content_forward'+data.txtid).val('');
			}
		}
		return false;
	});

    //删除动态
	$("[nc_type='sd_del']").live('click',function(){
		var data_str = $(this).attr('data-param');
        eval("data_str = "+data_str);
        var url = "index.php?act=store_snshome&op=deltrace&id="+data_str.txtid;
		showDialog('您确定要删除该信息吗？','confirm', '', function(){
			ajaxget(url);
			return false;
		});
	});
	
	// 查看大图
	$('[nc_type="thumb-image"]').die().live('click',function(){
		src = $(this).find('img').attr('src');
		max_src = src.replace('_240.', '_1280.');
		$(this).parent().hide().next().children('[nc_type="origin-image"]').append('<img src="'+max_src+'" />').end().show();
	});
	$('[nc_type="origin-image"]').die().live('click',function(){
		$(this).html('').parent().hide().prev().show();
	});
});