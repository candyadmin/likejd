
	DialogManager.close = function(id) {
		__DIALOG_WRAPPER__[id].hide();
		ScreenLocker.unlock();
  }
	DialogManager.show = function(id) {
		if (__DIALOG_WRAPPER__[id]) {
			__DIALOG_WRAPPER__[id].show();
			ScreenLocker.lock();
			return true;
		}
		return false;
  }
  var recommend_max = 4;//推荐数
  var goods_max = 8;//商品数
  var brand_max = 12;//品牌限制
  var recommend_show = 1;//当前选择的商品推荐
  var slide_pic_max = 5;//切换广告图片限制
  var sale_max = 5;//促销区组数
  var sale_goods_max = 5;//商品数
	var titles = new Array();
	titles["category_list"] = '推荐分类';
	titles["brand_list"] = '推荐品牌';
	titles["recommend_list"] = '商品推荐';
	titles["upload_tit"] = '标题设置';
	titles["upload_act"] = '活动图片';
	titles["recommend_pic"] = '推荐模块';
	titles["upload_adv"] = '切换广告图片';
	titles["sale_list"] = '促销商品推荐';

$(function(){
    //自定义滚定条
    $('.middle').perfectScrollbar();
    //随Y轴滚动固定层定位
    $('.middle').waypoint(function(event, direction) {
    	$(this).parent().toggleClass('sticky', direction === "down");
            event.stopPropagation();
    });
    $(".middle").sortable({
        items: 'dl',
        update: function(event, ui) {//推荐拖动排序保存
            $("#recommend_input_list").html('');
            $(".middle dl").each(function(){
                var recommend_id = $(this).attr("recommend_id");
                $("#recommend_input_list").append('<input type="hidden" name="recommend_list['+recommend_id+']" value="">');
            });
            update_data("recommend_list");//更新数据
        }
    });
    $("#sale_list_form").sortable({//促销区拖动排序保存
        items: 'dl',
        update: function(event, ui) {
            update_data("sale_list");//更新数据
        }
    });
});
function show_dialog(id) {//弹出框
	if(DialogManager.show(id)) return;
	var d = DialogManager.create(id);//不存在时初始化(执行一次)
	var dialog_html = $("#"+id+"_dialog").html();
	$("#"+id+"_dialog").remove();
	d.setTitle(titles[id]);
	d.setContents('<div id="'+id+'_dialog" class="'+id+'_dialog">'+dialog_html+'</div>');
	d.setWidth(640);
	d.show('center',1);
	update_dialog(id);
}
function replace_url(url) {//去当前网址
	return url.replace(UPLOAD_SITE_URL+"/", '');
}
function get_input(n,id,k,v) {//生成隐藏域代码
	return '<input type="hidden" name="'+n+'['+id+']['+k+']" value="'+v+'">';
}
function update_data(id) {//更新
	var get_text = $.ajax({
		type: "POST",
		url: 'index.php?act=web_api&op=code_update',
		data: $("#"+id+"_form").serialize(),
		async: false
		}).responseText;
	return get_text;
}
function update_dialog(id) {//初始化数据
	switch (id) {
		case "category_list":
			$("#category_list_form dl").sortable({ items: 'dd' });
			break;
		case "recommend_list":
			gcategoryInit("recommend_gcategory");
			$("#recommend_list_form dl dd ul").sortable({ items: 'li' });
			break;
		case "brand_list":
			$("#show_brand_list").load('index.php?act=web_api&op='+id);//查询数据
			$("#brand_list_form dd ul").sortable();
			break;
		case "sale_list":
			gcategoryInit("gcategory");
			$("#sale_list_dialog dl dd ul").sortable({ items: 'li' });
			break;
		default:
			$("#"+id+"_dialog tr.odd").click(function() {
				$(this).next("tr").toggle();
				$(this).find(".title").toggleClass("ac");
				$(this).find(".arrow").toggleClass("up");
			});
			$("#"+id+"_dialog .type-file-file").change(function() {//初始化图片上传控件
				$("#"+id+"_dialog .type-file-text").val($(this).val());
			});
			$("#upload_adv_form ul").sortable({ items: 'li' });
			break;
	}
}
function upload_type(id){//标题类型选择
	var obj = $("#upload_"+id+"_form");
	var get_type = obj.find("input:checked").val();
	obj.find("table[id^='upload_"+id+"_type_']").hide();
	$("#upload_"+id+"_type_"+get_type).show();
}
//分类相关
function get_goods_class() {//查询子分类
	var gc_id = $("#gc_parent_id").val();
	if (gc_id>0) {
		$.get('index.php?act=web_api&op=category_list&id='+gc_id, function(data) {
			$(data).each(function(){
				var obj = $(this);
				var gc_id = obj.attr("gc_id");
				if ($("dd[gc_id='"+gc_id+"']").size()==0) $("#category_list_form .category-list dl").append(obj);//不存在时添加
			});
		});
	}
}
function del_gc_parent(gc_id) {//删除已选分类
	var obj = $("dt[select_class_id='"+gc_id+"']");
	obj.parent().remove();
}
function del_goods_class(gc_id) {//删除已选分类
	var obj = $("dd[gc_id='"+gc_id+"']");
	obj.remove();
}
function update_category() {//更新分类
	var get_text = update_data("category_list");
	if (get_text=='1') {
		$(".home-templates-board-layout .category-list ul").html('');
		$("#category_list_form .category-list dl").each(function(){
			var obj = $(this);
			var text_append = '';
			var gc_name = '';
			obj.find("dd").each(function(){
				var dd = $(this);
				gc_name = dd.attr("title");
				text_append += '<li title="'+gc_name+'">';
				text_append += '<a href="javascript:void(0);">'+gc_name+'</a>';
				text_append += '</li>';
			});
		  $(".home-templates-board-layout .category-list ul").append(''+text_append+'');
		});
		DialogManager.close("category_list");
	}
}
//商品推荐相关
function show_recommend_dialog(id) {//弹出框
	recommend_show = id;
	$("dl[select_recommend_id]").hide();
	$("dl[select_recommend_id='"+id+"']").show();
	show_dialog('recommend_list');
}
function get_recommend_goods() {//查询商品
	var gc_id = 0;
	$('#recommend_gcategory > select').each(function() {
		if ($(this).val()>0) gc_id = $(this).val();
	});
	var goods_name = $.trim($('#recommend_goods_name').val());
	if (gc_id>0 || goods_name!='') {
		$("#show_recommend_goods_list").load('index.php?act=web_api&op=recommend_list&'+$.param({'id':gc_id,'goods_name':goods_name }));
	}
}
function del_recommend(id) {//删除商品推荐
    if ($(".middle dl").size()<2) {
         return;//保留一个
    }
	if(confirm('您确定要删除吗?')) {
		$(".middle dl[recommend_id='"+id+"']").remove();
		$("input[name='recommend_list["+id+"]']").remove();
		$("dl[select_recommend_id='"+id+"']").remove();
		$("dl[select_recommend_pic_id='"+id+"']").remove();
		$("div[select_recommend_pic_id='"+id+"']").remove();
		update_data("recommend_list");//更新数据
	}
}
function add_recommend() {//增加商品推荐
	for (var i = 1; i <= recommend_max; i++) {//防止数组下标重复
		if ($(".middle dl[recommend_id='"+i+"']").size()==0) {//编号不存在时添加
			var add_html = '';
			var del_append = '';
			del_append = '<a href="JavaScript:del_recommend('+i+');"><i class="icon-remove-sign "></i>删除</a>';//删除
			add_html = '<dl recommend_id="'+i+'"><dt><h4>商品推荐</h4>'+del_append+
    			'<a href="JavaScript:show_recommend_dialog('+i+');"><i class="icon-shopping-cart"></i>商品块</a><a href="JavaScript:show_recommend_pic_dialog('+i+');"><i class="icon-lightbulb"></i>广告块</a></dt>'+
    			'<dd><ul class="goods-list"><li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li>'+
    			'<li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li>'+
    			'<li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li></ul></dd></dl>';
			$("#btn_add_list").before(add_html);
			$("#add_recommend_list").before('<dl select_recommend_id="'+i+'"><dt><h4 class="dialog-handle-title">商品推荐模块标题名称</h4>'+
    			'<div class="dialog-handle-box"><span class="left"><input name="recommend_list['+i+'][recommend][name]" value="商品推荐" type="text" class="w200"></span>'+
    			'<span class="right">修改该区域中部推荐商品模块选项卡名称，控制名称字符在4-8字左右，超出范围自动隐藏</span>'+
    			'<div class="clear"></div></div></dt><dd><h4 class="dialog-handle-title">推荐商品</h4><ul class="dialog-goodslist-s1 goods-list">'+
    			'<div class="s-tips"><i></i>小提示：单击查询出的商品选中，双击已选择的可以删除，最多8个，保存后生效。</div></ul></dd></dl>');
			$("#recommend_list_form dl dd ul").sortable({ items: 'li' });
			break;
		}
	}
}
function select_recommend_goods(goods_id) {//商品选择
	var id = recommend_show;
	var obj = $("dl[select_recommend_id='"+id+"']");
	if(obj.find("img[select_goods_id='"+goods_id+"']").size()>0) return;//避免重复
	if(obj.find("img[select_goods_id]").size()>=goods_max) return;
	var goods = $("#show_recommend_goods_list img[goods_id='"+goods_id+"']");
	var text_append = '';
	var goods_pic = goods.attr("src");
	var goods_name = goods.attr("title");
	var goods_price = goods.attr("goods_price");
	var market_price = goods.attr("market_price");
	text_append += '<div ondblclick="del_recommend_goods('+goods_id+');" class="goods-pic">';
	text_append += '<span class="ac-ico" onclick="del_recommend_goods('+goods_id+');"></span>';
	text_append += '<span class="thumb size-72x72">';
	text_append += '<i></i>';
  	text_append += '<img select_goods_id="'+goods_id+'" title="'+goods_name+'" src="'+goods_pic+'" onload="javascript:DrawImage(this,72,72);" />';
	text_append += '</span></div>';
	text_append += '<div class="goods-name">';
	text_append += '<a href="'+SHOP_SITE_URL+'/index.php?act=goods&goods_id='+goods_id+'" target="_blank">';
  	text_append += goods_name+'</a>';
	text_append += '</div>';
	text_append += '<input name="recommend_list['+id+'][goods_list]['+goods_id+'][goods_id]" value="'+goods_id+'" type="hidden">';
	text_append += '<input name="recommend_list['+id+'][goods_list]['+goods_id+'][market_price]" value="'+market_price+'" type="hidden">';
	text_append += '<input name="recommend_list['+id+'][goods_list]['+goods_id+'][goods_name]" value="'+goods_name+'" type="hidden">';
	text_append += '<input name="recommend_list['+id+'][goods_list]['+goods_id+'][goods_price]" value="'+goods_price+'" type="hidden">';
	text_append += '<input name="recommend_list['+id+'][goods_list]['+goods_id+'][goods_pic]" value="'+replace_url(goods_pic)+'" type="hidden">';
	obj.find("ul").append('<li id="select_recommend_'+id+'_goods_'+goods_id+'">'+text_append+'</li>');
}
function del_recommend_goods(goods_id) {//删除已选商品
	var id = recommend_show;
	var obj = $("dl[select_recommend_id='"+id+"']");
	obj.find("img[select_goods_id='"+goods_id+"']").parent().parent().parent().remove();
}
function update_recommend() {//更新
    var id = recommend_show;
    $("li[id^='select_recommend_"+id+"_pic_']").remove();
    $("div[select_recommend_pic_id='"+id+"']").remove();
	var get_text = update_data("recommend_list");
	if (get_text=='1') {
	    var obj = $("dl[select_recommend_id='"+id+"']");
		var text_append = '';
		var recommend_name = obj.find("dt input").val();
		$(".middle dl[recommend_id='"+id+"'] dt h4").html(recommend_name);
		obj.find("img").each(function() {
			var goods = $(this);
			var goods_pic = goods.attr("src");
			var goods_name = goods.attr("title");
			text_append += '<li><span><a href="javascript:void(0);"><img title="'+goods_name+'" src="'+goods_pic+'"/></span></a></li>';
		});
	  $("dl[recommend_id='"+id+"'] dd ul").html('');
	  $(".middle dl[recommend_id='"+id+"'] dd").html('<ul class="goods-list">'+text_append+'</ul>');
		DialogManager.close("recommend_list");
	}
}
//品牌相关
function select_brand(brand_id) {//品牌选择
	if($("img[select_brand_id='"+brand_id+"']").size()>0) return;//避免重复
	if($("img[select_brand_id]").size()>=brand_max) return;
	var obj = $("img[brand_id='"+brand_id+"']");
	var text_append = '';
	var brand_pic = obj.attr("src");
	var brand_id = obj.attr("brand_id");
	var brand_name = obj.attr("title");
	text_append += '<div class="brands-pic"><span class="ac-ico" onclick="del_brand('+brand_id+');"></span>';
	text_append += '<span class="thumb size-88x29">';
	text_append += '<i></i>';
	text_append += '<img ondblclick="del_brand('+brand_id+');" select_brand_id="'+brand_id+'" src="'+brand_pic+'" onload="javascript:DrawImage(this,68,34);" />';
	text_append += '</span></div>';
	text_append += '<div class="brands-name">';
	text_append += brand_name+'</div>';
	text_append += get_input('brand_list',brand_id,'brand_id',brand_id);
	text_append += get_input('brand_list',brand_id,'brand_name',brand_name);
	text_append += get_input('brand_list',brand_id,'brand_pic',replace_url(brand_pic));

	$("#brand_list_form dd ul.dialog-brandslist-s1").append('<li>'+text_append+'</li>');
}
function get_recommend_brand() {//查询品牌
    var brand_name = $.trim($('#recommend_brand_name').val());
	if (brand_name!='') {
		$("#show_brand_list").load('index.php?act=web_api&op=brand_list&'+$.param({'brand_name':brand_name }));
	}
}
function del_brand(brand_id) {//删除已选品牌
	var obj = $("img[select_brand_id='"+brand_id+"']");
	obj.parent().parent().parent().remove();
}
function update_brand() {//更新品牌
	var get_text = update_data("brand_list");
	if (get_text=='1') {
		$(".right ul.brands").html('');
		$("img[select_brand_id]").each(function() {
			var obj = $(this);
			var text_append = '';
			var brand_pic = obj.attr("src");
			var brand_name = obj.attr("title");
			text_append = '<img title="'+brand_name+'" src="'+brand_pic+'"/>';
		  $(".right ul.brands").append('<li><span>'+text_append+'</span></li>');
		});
		DialogManager.close("brand_list");
	}
}
//图片上传
function update_pic(id,pic) {//更新图片
	if (id=='tit') {
	    var tit_floor = $.trim($('#tit_floor').val());
	    var tit_title = $.trim($('#tit_title').val());
	    var get_type = $("#upload_tit_form input:checked").val();
	    $("#left_tit dd").hide();
	    $("#left_tit dd.tit-"+get_type).show();
	    if (get_type=='txt') {
		    $("#picture_floor").html('<span>'+tit_floor+'</span><h2>'+tit_title+'</h2>');
		}
	}
	var obj = $("#picture_"+id);
	obj.html('<img src="'+UPLOAD_SITE_URL+'/'+pic+'" />');
	DialogManager.close("upload_"+id);
}
//中部推荐图片上传
function show_recommend_pic_dialog(id) {//弹出框
    show_dialog('recommend_pic');
	var obj = $("#recommend_pic_form");
	recommend_show = id;

	obj.find("div[select_recommend_pic_id]").hide();
	obj.find("input[name='key_id']").val(id);
	var recommend_name = $("input[name='recommend_list["+id+"][recommend][name]']").val();
	obj.find("input[name='recommend_list[recommend][name]']").val(recommend_name);
    if(obj.find("div[select_recommend_pic_id='"+id+"']").size()==0) {//不存在时添加
        var text_append = '<div select_recommend_pic_id="'+id+'" class="middle-banner">'+
							'<a href="javascript:void(0);" recommend_pic_id="11" class="left-a">194*194</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="12" class="left-b">194*194</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="14" class="middle-a">388*388</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="21" class="right-a">194*194</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="24" class="right-b">194*194</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="31" class="bottom-a">194*110</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="32" class="bottom-b">194*110</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="33" class="bottom-c">194*110</a>'+
							'<a href="javascript:void(0);" recommend_pic_id="34" class="bottom-d">194*110</a>'+
            	  '</div>';
        obj.find("#add_recommend_pic").append(text_append);
	}
	obj.find("div[select_recommend_pic_id='"+id+"']").show();
	obj.find("div[select_recommend_pic_id='"+id+"'] a").each(function() {
    	    $(this).bind('click', function() {
    	        select_recommend_pic(this);
    	    });
	    });
	obj.find("div[select_recommend_pic_id='"+id+"'] a[recommend_pic_id='11']").trigger("click");//默认选中第一个图片
}
function select_recommend_pic(pic_td) {//选中推荐图片
    var obj = $(pic_td);
    var pic_id = obj.attr("recommend_pic_id");
    var pic_name = obj.find("img").attr("title");
    var pic_url = obj.find("img").attr("pic_url");
    var pic_img = obj.find("img").attr("src");
    $("div[select_recommend_pic_id='"+recommend_show+"'] a").css("opacity","");
    obj.css("opacity","100");
    $("input[name='pic_id']").val(pic_id);
    if (pic_img!='') {
        $("input[name='pic_list[pic_name]']").val(pic_name);
        $("input[name='pic_list[pic_url]']").val(pic_url);
        $("#recommend_pic_form .type-file-file").val('');
        $("#recommend_pic_form .type-file-text").val('');
    }
}
function recommend_pic(pic_id,pic_img) {//更新图片
	if (pic_img!='') {
	    var id = recommend_show;
	    var recommend_name = $("input[name='recommend_list[recommend][name]']").val();
	    $("input[name='recommend_list["+id+"][recommend][name]']").val(recommend_name);
	    $("li[id^='select_recommend_"+id+"_goods_']").remove();
	    $("li[id='select_recommend_"+id+"_pic_"+pic_id+"']").remove();

	    var pic_name = $("input[name='pic_list[pic_name]']").val();
	    var pic_url = $("input[name='pic_list[pic_url]']").val();
	    var text_append = '';
	    text_append += '<input name="recommend_list['+id+'][pic_list]['+pic_id+'][pic_id]" value="'+pic_id+'" type="hidden">';
	    text_append += '<input name="recommend_list['+id+'][pic_list]['+pic_id+'][pic_name]" value="'+pic_name+'" type="hidden">';
	    text_append += '<input name="recommend_list['+id+'][pic_list]['+pic_id+'][pic_url]" value="'+pic_url+'" type="hidden">';
	    text_append += '<input name="recommend_list['+id+'][pic_list]['+pic_id+'][pic_img]" value="'+pic_img+'" type="hidden">';
	    var obj = $("dl[select_recommend_id='"+id+"']");
	    obj.find("ul").append('<li id="select_recommend_'+id+'_pic_'+pic_id+'" style="display:none;">'+text_append+'</li>');//插入隐藏的表单代码使在商品编辑时能正常保存

	    obj = $("div[select_recommend_pic_id='"+id+"'] a[recommend_pic_id='"+pic_id+"']");//弹出框中的图片显示
	    if(obj.find("img").size()==0) {//不存在时添加
	        text_append = '<img pic_url="" title="" src="" />';
            obj.html(text_append);
	    }
	    obj.find("img").attr("title",pic_name);
	    obj.find("img").attr("pic_url",pic_url);
	    obj.find("img").attr("src",UPLOAD_SITE_URL+'/'+pic_img);

	    text_append = $("div[select_recommend_pic_id='"+id+"']").html();
        $(".middle dl[recommend_id='"+id+"'] dd").html('<div class="middle-banner">'+text_append+'</div>');//页面中的图片显示
        $(".middle dl[recommend_id='"+id+"'] dd div.middle-banner a").css("opacity","");
	    $(".middle dl[recommend_id='"+id+"'] dt h4").html(recommend_name);
	}
}
//切换图片上传
function add_slide_adv() {//增加图片
	for (var i = 1; i <= slide_pic_max; i++) {//防止数组下标重复
		if ($("#upload_adv_form ul li[slide_adv_id='"+i+"']").size()==0) {//编号不存在时添加
    	    var text_input = '';
    	    text_input += '<input name="adv['+i+'][pic_id]" value="'+i+'" type="hidden">';
    	    text_input += '<input name="adv['+i+'][pic_name]" value="" type="hidden">';
    	    text_input += '<input name="adv['+i+'][pic_url]" value="" type="hidden">';
    	    text_input += '<input name="adv['+i+'][pic_img]" value="" type="hidden">';
			var add_html = '';
			add_html = '<li slide_adv_id="'+i+'"><div class="adv-pic"><span class="ac-ico" onclick="del_slide_adv('+i+
			');"></span><span class="thumb size-106x106" onclick="select_slide_adv('+i+');"><img src="'+ADMIN_TEMPLATES_URL+
			'/images/picture.gif" /></span></div>'+text_input+'</li>';
			$("#upload_adv_form ul").append(add_html);
			select_slide_adv(i);
			break;
		}
	}
}
function select_slide_adv(pic_id) {//选中图片
    var obj = $("#upload_adv_form ul li[slide_adv_id='"+pic_id+"']");
    var pic_name = obj.find("input[name='adv["+pic_id+"][pic_name]']").val();
    var pic_url = obj.find("input[name='adv["+pic_id+"][pic_url]']").val();
    var pic_img = obj.find("img").attr("src");
    $("#upload_adv_form ul li").removeClass("selected");
    $("input[name='slide_id']").val(pic_id);
    $("input[name='slide_pic[pic_name]']").val(pic_name);
    $("input[name='slide_pic[pic_url]']").val(pic_url);
    $("#upload_adv_form .type-file-file").val('');
    $("#upload_adv_form .type-file-text").val('');
    $("#upload_slide_adv").show();
    obj.addClass("selected");
}
function slide_adv(pic_id,pic_img) {//更新图片
    var obj = $("#upload_adv_form ul li[slide_adv_id='"+pic_id+"']");
	if (pic_img!='') {
	    var pic_name = $("input[name='slide_pic[pic_name]']").val();
        var pic_url = $("input[name='slide_pic[pic_url]']").val();
	    obj.find("img").attr("title",pic_name);
	    obj.find("img").attr("src",UPLOAD_SITE_URL+'/'+pic_img);

        obj.find("input[name='adv["+pic_id+"][pic_name]']").val(pic_name);
        obj.find("input[name='adv["+pic_id+"][pic_url]']").val(pic_url);
        obj.find("input[name='adv["+pic_id+"][pic_img]']").val(pic_img);

        var pic_img = $("#upload_adv_form ul li").first().find("img").attr("src");
        $("#picture_adv").html('<img src="'+pic_img+'"/>');
    }
}
function del_slide_adv(pic_id) {//删除图片
    if ($("#upload_adv_form ul li").size()<2) {
         return;//保留一个
    }
	$("#upload_adv_form li[slide_adv_id='"+pic_id+"']").remove();
	var slide_id = $("input[name='slide_id']").val();
	if (pic_id==slide_id) {
    	$("input[name='slide_id']").val('');
    	$("#upload_slide_adv").hide();
	}
}

//商品促销区
function show_sale_dialog(id){//弹出框
	recommend_show = id;
	$("dl[select_sale_id]").hide();
	$("dl[select_sale_id='"+id+"']").show();
	show_dialog('sale_list');
}
function get_goods_list(){//查询商品
	var gc_id = 0;
	$('#gcategory > select').each(function(){
		if ($(this).val()>0) gc_id = $(this).val();
	});
	var goods_name = $.trim($('#order_goods_name').val());
	var goods_order = $('#goods_order').val();
	if (gc_id>0 || goods_name!='') {
		$("#show_sale_goods_list").load('index.php?act=web_api&op=goods_list&'+$.param({'id':gc_id,'goods_order':goods_order,'goods_name':goods_name }));
	}
}
function del_sale_list(id){//删除商品推荐
    if ($("dl[sale_id]").size()<2) {
         return;//保留一个
    }
	if(confirm('您确定要删除吗?')){
		$("dl[sale_id='"+id+"']").remove();
		$("dl[select_sale_id='"+id+"']").remove();
		update_data("sale_list");//更新数据
	}
}
function add_sale_list() {//增加商品推荐
	for (var i = 1; i <= sale_max; i++) {//防止数组下标重复
		if ($("dl[sale_id='"+i+"']").size()==0) {//编号不存在时添加
			var add_html = '';
			add_html = '<dl sale_id="'+i+'"><a href="JavaScript:del_sale_list('+i+');" class="del">X</a><dt><h4>商品推荐</h4><a href="JavaScript:show_sale_dialog('+i+
    			');"><i class="icon-edit"></i>编辑</a><input name="sale_list['+i+'][recommend][name]" value="" type="hidden"></dt>'+
    			'<dd><ul class="goods-list"><li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li>'+
    			'<li><span><i class="icon-gift"></i></span></li><li><span><i class="icon-gift"></i></span></li></ul></dd></dl>';
			$("#add_list").before(add_html);
			$("#select_sale_list").before('<dl select_sale_id="'+i+'"><dt><h4 class="dialog-handle-title">商品推荐模块标题名称</h4>'+
    			'<div class="dialog-handle-box"><span class="left"><input name="recommend" value="商品推荐" type="text" class="w200"></span>'+
    			'<span class="right">修改该区域中部推荐商品模块选项卡名称，控制名称字符在4-8字左右，超出范围自动隐藏</span>'+
    			'<div class="clear"></div></div></dt><dd><ul class="dialog-goodslist-s1 goods-list">'+
    			'<div class="s-tips"><i></i>小提示：单击查询出的商品选中，双击已选择的可以删除，最多5个，保存后生效。</div></ul></dd></dl>');
			$("dl[select_sale_id='"+i+"'] dd ul").sortable({ items: 'li' });
			break;
		}
	}
}
function select_sale_goods(goods_id){//商品选择
	var id = recommend_show;
	var obj = $("dl[select_sale_id='"+id+"']");
	if(obj.find("img[select_goods_id='"+goods_id+"']").size()>0) return;//避免重复
	if(obj.find("img[select_goods_id]").size()>=sale_goods_max) return;
	var goods = $("#show_sale_goods_list img[goods_id='"+goods_id+"']");
	var text_append = '';
	var goods_pic = goods.attr("src");
	var goods_name = goods.attr("title");
	var goods_price = goods.attr("goods_price");
	var market_price = goods.attr("market_price");
	text_append += '<div ondblclick="del_sale_goods('+goods_id+');" class="goods-pic">';
	text_append += '<span class="ac-ico" onclick="del_sale_goods('+goods_id+');"></span>';
	text_append += '<span class="thumb size-72x72">';
	text_append += '<i></i>';
  	text_append += '<img select_goods_id="'+goods_id+'" title="'+goods_name+'" src="'+goods_pic+'" goods_price="'+goods_price+'" market_price="'+market_price+'" onload="javascript:DrawImage(this,72,72);" />';
	text_append += '</span></div>';
	text_append += '<div class="goods-name">';
	text_append += '<a href="'+SITEURL+'/index.php?act=goods&goods_id='+goods_id+'" target="_blank">';
  	text_append += goods_name+'</a>';
	text_append += '</div>';
	obj.find("ul").append('<li>'+text_append+'</li>');

}
function del_sale_goods(goods_id){//删除已选商品
	var id = recommend_show;
	var obj = $("dl[select_sale_id='"+id+"']");
	obj.find("img[select_goods_id='"+goods_id+"']").parent().parent().parent().remove();
}
function update_sale(){//更新
    var id = recommend_show;
    var obj = $("dl[select_sale_id='"+id+"']");
    var text_append = '';
    var recommend_name = obj.find("dt input").val();
    $("dl[sale_id='"+id+"'] dt h4").html(recommend_name);
    $("dl[sale_id='"+id+"'] dt input").val(recommend_name);
    obj.find("img").each(function(){
    	var goods = $(this);
    	var goods_id = goods.attr("select_goods_id");
    	var goods_pic = goods.attr("src");
    	var goods_name = goods.attr("title");
    	var goods_price = goods.attr("goods_price");
    	var market_price = goods.attr("market_price");
    	text_append += '<li><div class="goods-thumb"><img title="'+goods_name+'" src="'+goods_pic+'"/></div>';
    	text_append += '<input name="sale_list['+id+'][goods_list]['+goods_id+'][goods_id]" value="'+goods_id+'" type="hidden">';
    	text_append += '<input name="sale_list['+id+'][goods_list]['+goods_id+'][market_price]" value="'+market_price+'" type="hidden">';
    	text_append += '<input name="sale_list['+id+'][goods_list]['+goods_id+'][goods_name]" value="'+goods_name+'" type="hidden">';
    	text_append += '<input name="sale_list['+id+'][goods_list]['+goods_id+'][goods_price]" value="'+goods_price+'" type="hidden">';
    	text_append += '<input name="sale_list['+id+'][goods_list]['+goods_id+'][goods_pic]" value="'+replace_url(goods_pic)+'" type="hidden">';
    	text_append += '</li>';
    });
    $("dl[sale_id='"+id+"'] dd ul").html('');
    $("dl[sale_id='"+id+"'] dd ul").append(text_append);
	var get_text = update_data("sale_list");
	if (get_text=='1') {
		DialogManager.close("sale_list");
	}
}