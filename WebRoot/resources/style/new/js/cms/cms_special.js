$(document).ready(function(){
    var d;
    var image_object;
	$('a[nctype="nyroModal"]').nyroModal();
    //文件上传
    var textButton1="<input type='text' name='textfield' id='textfield1' class='type-file-text' /><input type='button' name='button' id='button1' value='' class='type-file-button' />";
    $(textButton1).insertBefore("#special_image");
    $("#special_image").change(function(){
        $("#textfield1").val($("#special_image").val());
    });
    var textButton2="<input type='text' name='textfield' id='textfield2' class='type-file-text' /><input type='button' name='button' id='button2' value='' class='type-file-button' />";
    $(textButton2).insertBefore("#special_background");
    $("#special_background").change(function(){
        $("#textfield2").val($("#special_background").val());
    });
    $("input[nctype='cms_image']").live("change", function(){
		var src = getFullPath($(this)[0]);
		$(this).parent().prev().find('.low_source').attr('src',src);
		$(this).parent().find('input[class="type-file-text"]').val($(this).val());
	});


    //图片上传
    $("#picture_image_upload").fileupload({
        dataType: 'json',
            url: "index.php?act=cms_special&op=special_image_upload",
            add: function(e,data) {
                $.each(data.files, function(index, file) {
					var image_content = '<li id=' + file.name.replace('.', '_') + ' class="picture">';
                    image_content += '<div class="size-64x64"><span class="thumb size-64x64"><i></i><img src="' + LOADING_IMAGE + '" alt="" /></span></div>';
                    image_content += '<p class="handle">';
                    image_content += '</p>';

                    $("#special_image_list").append(image_content);
                });
                data.submit();
            },
            done: function (e,data) {
                result = data.result;
                var $image_box = $('#' + result.origin_file_name.replace('.', '_'));
                if(result.status == "success") {
                    $image_box.find('img').attr('src', result.file_url);

                    var $image_handle = $image_box.find('.handle');
					var image_handle = '<a nctype="btn_show_image_insert_link" image_url="'+result.file_url+'" class="insert-link tooltip" title="以图片链接模式插入专题页">&nbsp;</a>';
                    image_handle += '<a nctype="btn_show_image_insert_hot_point" image_name="'+result.file_name+'" image_url="'+result.file_url+'" class="insert-hotpoint tooltip " title="以热点链接模式插入专题页">&nbsp;</a>';
                    image_handle += '<a nctype="btn_drop_special_image" image_name="'+result.file_name+'" class="delete tooltip " title="删除该图片">&nbsp;</a>';
                    $image_handle.html(image_handle);

                    var image_hidden = '<input name="special_image_all[]" type="hidden" value="'+result.file_name+'" />';
                    $image_handle.after(image_hidden);

                    $image_box.attr('id', '');
                } else {
                    $image_box.remove();
                    alert(result.error);
                }
            }
    });

    //图片删除
    $("[nctype='btn_drop_special_image']").live("click",function(){
        var image_object = $(this).parents("li");
        var image_name = $(this).attr("image_name");
        $.getJSON("index.php?act=cms_special&op=special_image_drop", { image_name: image_name }, function(result){
                if(result.status == "success") {
                    image_object.remove();
                } else {
                    showError(result.error);
                }
        });
    });

    //插入图片链接对话框
    $("[nctype='btn_show_image_insert_link']").live('click', function(){
        $("#_dialog_image_insert_link").find("img").attr("src", $(this).attr("image_url"));
        html_form('dialog_image_insert_link', '图片链接', $("#_dialog_image_insert_link").html(), 640);
    });

    //插入图片链接
    $("[nctype='btn_image_insert_link']").live('click', function(){
        var html = $("#special_content").val();
        var item = $(this).parents("table");
        var link = item.find("[nctype='_image_insert_link']").val();
        html += "<div class='special-content-link'>";
        if(link != "") {
            html += "<a href=" + link +" target='_blank'>";
        }
        html += $("<div />").append(item.find("img").clone()).html(); 
        if(link != "") {
            html += "</a>";
        }
        html += "</div>";
        $("#special_content").val(html);
        special_view_update();
        DialogManager.close("dialog_image_insert_link");
    });

    //插入热点图
    $("[nctype='btn_show_image_insert_hot_point']").live('click', function(){
        $("#_dialog_image_insert_hot_point").find("img").attr("src", $(this).attr("image_url"));
        d = html_form('dialog_image_insert_hot_point', '图片热点', $("#_dialog_image_insert_hot_point").html(), 1040);
        var count = 0;
        
        var dialog_object = $("#fwin_dialog_image_insert_hot_point");
        var filename = $(this).attr("image_name").replace(".","");
        image_object = dialog_object.find("[nctype='img_hot_point']");
        var x1 = dialog_object.find("[nctype='x1']");
        var y1 = dialog_object.find("[nctype='y1']");
        var x2 = dialog_object.find("[nctype='x2']");
        var y2 = dialog_object.find("[nctype='y2']");
        var w = dialog_object.find("[nctype='w']");
        var h = dialog_object.find("[nctype='h']");
        var url = dialog_object.find("[nctype='url']");

        image_object.imgAreaSelect({ 
            handles: true,
                fadeSpeed: 200, 
                onSelectChange: preview 
        });

        image_object.attr("usemap","#"+filename);
        image_object.after('<map id="'+filename+'" name="'+filename+'"></map>');

        $("[nctype='delete_hot_point']").live("click",function(){
            var key = $(this).attr("name");
            $("#"+key).remove();
            $("."+key).remove();
        });

        $("[nctype='select_hot_point']").live("click",function(){
            var xy = $(this).attr("name");
            var xyarray = xy.split(",");
            var ias = image_object.imgAreaSelect({ instance: true });
            ias.setSelection(xyarray[0],xyarray[1],xyarray[2],xyarray[3],true);
            ias.setOptions({ show: true });
            ias.update();
        });

        dialog_object.find("[nctype='btn_hot_point_commit']").click(function(){
            count++;
            var key = filename + count;
            var xy = x1.val()+','+y1.val()+','+x2.val()+','+y2.val()+',';
            image_object.parent().find("map").append('<area id='+key+' shape="rect" coords="'+xy+'" href="'+url.val()+'" target="_blank" />');
            dialog_object.find("[nctype='list']").append('<li class="'+key+'"><i></i>热点区域'+count+'<span>('+url.val()+')</span><a nctype="select_hot_point" name="'+xy+'" href="###" class="btn-select-hot-point">选中</a><a nctype="delete_hot_point" class="btn-delete-hot-point" name="'+key+'" href="###">删除</a></li>');
            image_object.after('<div class='+key+' style="width:'+w.val()+'px;height:'+h.val()+'px;position:absolute;left:'+x1.val()+'px;top:'+y1.val()+'px;border:1px solid #cccccc;">'+count+'</div>');
            url.val('');
            var ias = image_object.imgAreaSelect({ instance: true });
            ias.cancelSelection();
        });

        $(".dialog_close_button").unbind().bind("click", function() {
            var ias = image_object.imgAreaSelect({ instance: true });
            ias.cancelSelection();
            DialogManager.close("dialog_image_insert_hot_point");
        });
    });

    //插入图片链接
    $("[nctype='btn_image_insert_hot_point']").live('click', function(){
        var html = $("#special_content").val();
        var hot_point = $(this).parents("table").find(".special-hot-point").clone(); 
        hot_point.find("div").remove();
        hot_point = $("<div />").append(hot_point).html(); 
        html += hot_point;
        $("#special_content").val(html);
        special_view_update();
        var ias = image_object.imgAreaSelect({ instance: true });
        ias.cancelSelection();
        DialogManager.close("dialog_image_insert_hot_point");
    });

    //图片删除
    $("[nctype='btn_drop_special_image']").click(function() {
        $(this).parents(".picture").remove();
    });

    //插入商品对话框
    $("#btn_show_special_insert_goods").click(function(){
        html_form('dialog_special_insert_goods', '插入商品', $("#_dialog_special_insert_goods").html(), 640);
    });

    //选择商品
    $("[nctype='btn_special_goods']").live('click', function(){
        var goods_list = $(this).parents("table").find("[nctype='_special_goods_list']");
        var link_item = $(this).parents("table").find("[nctype='_input_goods_link']");
        var link = link_item.val();
        link_item.val('');
        if(link != '') {
        var url = encodeURIComponent(link);
        $.getJSON("index.php?act=cms_special&op=goods_info_by_url", { url: url}, function(data){
            if(data.result == "true") {
                var temp = '<li nctype="btn_goods_select"><dl>'; 
                temp += '<dt class="name"><a href="'+data.url+'" target="_blank">'+data.title+'</a></dt>';
                temp += '<dd class="image"><a href="'+data.url+'" target="_blank"><img title="'+data.title+'" src="'+data.image+'" /></a></dd>';
                temp += '<dd class="price">价格：<em>'+data.price+'</em></dd>';
                temp += '<dd class="taobao-item-delete" nctype="btn_special_goods_delete" title="删除添加的商品">&nbsp;</dd>';
                temp += '</dl></li>';
                $(goods_list).append(temp);
            } else {
                alert(data.message);
            }
        });
        }
    });

    //删除商品
    $("[nctype='btn_special_goods_delete']").live('click', function(){
        $(this).parent().parent().remove();
    });

    //插入商品列表
    $("[nctype='btn_special_insert_goods']").live('click', function(){
        var html = $("#special_content").val();
        html += "<div class='special-content-goods-list'>";
        var goods_list = $("<div />").append($(this).parents("table").find("[nctype='_special_goods_list']").clone());
        goods_list.find("i").remove();
        goods_list.find("[nctype='btn_special_goods_delete']").remove();
        html += goods_list.html(); 
        html += "</div>";
        $("#special_content").val(html);
        special_view_update();
        DialogManager.close("dialog_special_insert_goods");
    });

    //编辑模式
    $("#btn_content_edit").click(function(){
        $(this).attr("class", "tab-btn actived");
        $("#btn_content_view").attr("class", "tab-btn");
        $("#div_content_edit").show();
        $("#div_content_view").hide();
    });

    //预览模式
    $("#btn_content_view").click(function(){
        $(this).attr("class", "tab-btn actived");
        $("#btn_content_edit").attr("class", "tab-btn");
        $("#div_content_edit").hide();
        $("#div_content_view").show();
    });

    //更新预览窗口
    $("#special_content").change(function(){
        special_view_update();
    });

    function special_view_update() {
        $("#div_content_view").html($("#special_content").val());
    }

    special_view_update();

});

//选取相关
function preview(img, selection) {
    if (!selection.width || !selection.height)
        return;
    var scaleX = 100 / selection.width;
    var scaleY = 100 / selection.height;
    $('#preview img').css({
        width: Math.round(scaleX * 300),
            height: Math.round(scaleY * 300),
            marginLeft: -Math.round(scaleX * selection.x1),
            marginTop: -Math.round(scaleY * selection.y1)
    });
    var dialog_object = $("#fwin_dialog_image_insert_hot_point");
    dialog_object.find("[nctype='x1']").val(selection.x1);
    dialog_object.find("[nctype='y1']").val(selection.y1);
    dialog_object.find("[nctype='x2']").val(selection.x2);
    dialog_object.find("[nctype='y2']").val(selection.y2);
    dialog_object.find("[nctype='w']").val(selection.width);
    dialog_object.find("[nctype='h']").val(selection.height);
}

