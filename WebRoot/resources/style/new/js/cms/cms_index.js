var object_module_edit;
var article_save_function;
var limit_count;
var image_count = 1;

$(document).ready(function(){
    var function_list = {};

    //标题修改
    $("[nctype='btn_module_title_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var title = object_module_edit.html();
        $('#dialog_module_title_edit').nc_show_dialog({width: 640, title: '编辑标题'});
        $("#input_module_title").val(title);
    });
    //标题保存
    $("#btn_module_title_save").click(function(){
        object_module_edit.html($("#input_module_title").val());
        $('#dialog_module_title_edit').hide();
    });

    //标签修改
    $(".article-tag-selected-list").sortable();
    $("#btn_module_tag_edit").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var tag_list = object_module_edit.clone();
        tag_list.find("li").append('<i nctype="btn_module_tag_select_drop" class="cms-index-tag-select-drop" class="删除所选"></i>');
        $('#dialog_module_tag_edit').nc_show_dialog({width: 640, title: '编辑标签'});
        $("#article_tag_selected_list").html(tag_list.html());
    });
    //标签选择
    $(".article-tag-list [nctype='btn_tag_select']").live('click', function(){
        var tag_id = $(this).attr("data-tag-id");
        $("#article_tag_selected_list li").each(function(){
            if($(this).attr("data-tag-id") == tag_id) {
                tag_id = 0;
            }
        });
        if(tag_id > 0) {
            $("#article_tag_selected_list").append($(this).clone());
        }
        return false;
    });
    $(".article-tag-selected-list [nctype='btn_tag_select']").live('click', function(){
        $(this).remove();
        return false;
    });
    //标签保存
    $("#btn_module_tag_save").click(function(){
        var tag_list = $("#article_tag_selected_list").clone();
        tag_list.find("[nctype='btn_module_tag_select_drop']").remove();
        object_module_edit.html(tag_list.html());
        $('#dialog_module_tag_edit').hide();
    });


    //图片修改
    $("[nctype='btn_module_image_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        if(parseInt($(this).attr("image_count"), 10) !== 1) { 
            $("#btn_image_upload").attr("multiple", "1");
            image_count = 2;
        }
        var image_list = object_module_edit.clone();
        if(image_list.find("li.picture").length > 0) {
            image_list.find("li").each(function(){
                var image_item = $(this).find("img");
                var link = image_item.parent().attr("href");
                var image_name = image_item.attr("image_name");
                $(this).append('<a nctype="btn_module_drop_image" image_name="'+image_name+'" class="handle-del" title="删除该图片"><i></i>&nbsp;</a>');
                $(this).append('<div class="pic-url">相关网址：<input type="text" class="w200" value="'+link+'"> 网址应包含http://</div>');
            });
        }
        $("#module_image_edit_explain").html("<i></i>" + $(this).attr("data-title"));
        $('#dialog_module_image_edit').nc_show_dialog({width: 640, title: '编辑图片'});
        if(image_list.find("li.picture").length > 0) {
            $("#image_selected_list").html(image_list.html());
        } else {
            $("#image_selected_list").html("");
        }

    });
    //图片上传
    $("#btn_image_upload").fileupload({
        dataType: 'json',
            url: "index.php?act=cms_index&op=image_upload",
            add: function(e,data) {
                data.submit();
            },
                done: function (e,data) {
                    result = data.result;
                    if(result.status == "success") {
                        var image_content = '<li class="picture">';
                        image_content += '<div class="cms-thumb"><a href="" target="_blank"><img data-image-name="'+result.file_name+'" src="'+result.file_url+'" alt="" class="t-img" /></a></div>';
						
                        image_content += '<a nctype="btn_module_drop_image" image_name="'+result.file_name+'" class="handle-del" title="删除该图片"><i></i>&nbsp;</a>';			
						image_content += '<div class="pic-url">';
						image_content += '相关网址：';
                        image_content += '<input type="text" value="" class="w200"/>';
                        image_content += ' 网址应包含http://</div></li>';
                        if(image_count === 1) {
                            $("#image_selected_list li").each(function(){
                                $("#add_form").append('<input name="module_drop_image[]" type="hidden" value="'+$(this).find("img").attr("data-image-name")+'" />');
                            });
                            $("#image_selected_list").html("");
                        }
                        $("#image_selected_list").append(image_content);
                    } else {
                        showError(result.error);
                    }

                }
    });
    //图片删除
    $("[nctype='btn_module_drop_image']").live("click", function(){
        $("#add_form").append('<input name="module_drop_image[]" type="hidden" value="'+$(this).attr("image_name")+'" />');
        $(this).parents("li.picture").remove();
    });
    //图片保存
    $("#btn_module_image_save").click(function(){
        var image_list = $("#image_selected_list").clone();
        image_list.find("li").each(function(){
            $(this).find("img").parent().attr("href", $(this).find("input").val());
        });
        image_list.find(".handle-del").remove();
        image_list.find(".pic-url").remove();
        object_module_edit.html(image_list.html());
        $('#dialog_module_image_edit').hide();
    });

    //文章拖动改变顺序
    $("#article_selected_list").sortable();
    //文章修改
    $("[nctype='btn_module_article_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var article_list = object_module_edit.clone();
        article_save_function = $(this).attr("save_function");
        limit_count = parseInt($(this).attr("limit_count"), 10);
        if(!limit_count) {
            limit_count = 0;
        }
        article_list.find('[nctype="article_view"]').remove();
        article_list.find("li").each(function(){
            var title = $(this).find("span.title");
            var article = $(this).find("span.title a");
            $(this).prepend('<span class="article-image" nctype="cms_index_not_display"><p><img src="'+article.attr("article_image")+'"></p></span>');
            title.prepend('<em class="class-name" nctype="cms_index_not_display">['+article.attr("class_name")+']</em>');
            title.append('<em class="publish-time" nctype="cms_index_not_display">('+article.attr("article_publish_time")+')</em>');
            $(this).append('<span title="'+article.attr("article_abstract")+'" class="article-abstract" nctype="cms_index_not_display">文章摘要：'+article.attr("article_abstract")+'</span>');
            $(this).append('<a nctype="btn_article_select" href="JavaScript:void(0);" class="delete" title="选择删除"></a>');
        });
        $('#dialog_module_article_edit').nc_show_dialog({width: 640, title: '编辑文章'});
        $("#article_selected_list").html(article_list.html());
        $("#article_search_list").html("");
    });
    //文章搜索
    $("#btn_article_search").click(function(){
        var search_type = $("[name='article_search_type']:checked").val();
        var search_keyword = $("#input_article_search_keyword").val();
        if(search_keyword !== "") {
            $("#div_article_select_list").load("index.php?act=cms_base&op=get_article_list&" + $.param({search_type: search_type,search_keyword: search_keyword}));
        }
    });
    //文章选择翻页
    $("#div_article_select_list .demo").live('click',function(e){
        $("#div_article_select_list").load($(this).attr('href'));
        return false;
    });
    //文章选择
    $("#article_search_list [nctype='btn_article_select']").live("click", function(){
        var current_count = $("#article_selected_list li").length;
        if(current_count < limit_count || limit_count === 0) {
            var temp = $(this).parent().clone();
            temp.find("[nctype='btn_article_select']").attr("title","删除");
            $("#article_selected_list").append($("<div />").append(temp).html());
        }
    });
    //文章删除
    $("#article_selected_list [nctype='btn_article_select']").live("click", function(){
        $(this).parent().remove();
    });
    //文章列表保存
    $("#btn_module_article_save").click(function(){
        $("#article_selected_list").find("[nctype='btn_article_select']").remove();
        $("#article_selected_list").find("[nctype='cms_index_not_display']").remove();
        object_module_edit.html("");
        function_list[article_save_function]();
        $('#dialog_module_article_edit').hide();
    });
    //文章列表保存(文字链接)
    function_list['article_type_0_save'] = function() {
        var count = 1;
        $("#article_selected_list li").each(function(){
            if(count > 1) {
                $(this).attr("class", "cms-index-article-normal");
            } else {
                $(this).attr("class", "cms-index-article-focus");
            }
            object_module_edit.append($(this));
            count++;
        });
    };
    //文章列表1-1保存
    function_list['article_type_1_save'] = function() {
        var count = 1;
        $("#article_selected_list li").each(function(){
            $(this).prepend('<span nctype="article_view" class="cms-index-count_'+count+'">'+count+'</span>');
            $(this).append('<span nctype="article_view" class="cms-index-click-count">'+$(this).find('[nctype="article_item"]').attr("article_click")+'人关注</span>');
            object_module_edit.append($(this));
            count++;
        });
    };
    //文章列表1-2保存
    function_list['article_type_2_save'] = function() {
        var count = 1;
        $("#article_selected_list li").each(function(){
            var article = $(this).find('[nctype="article_item"]');
            if(count > 1) {
                $(this).attr("class", "cms-index-article-normal");
                $(this).prepend('<span nctype="article_view" class="cms-index-article-class">'+article.attr("class_name")+'</span>');
                $(this).append('<span nctype="article_view" class="cms-index-article-date">'+article.attr("article_publish_time").substring(8)+'日</span>');
            } else {
                $(this).attr("class", "cms-index-article-focus");
            }
            object_module_edit.append($(this));
            count++;
        });
    };
    //文章列表保存(图文)
    function_list['article_type_3_save'] = function() {
        $("#article_selected_list li").each(function(){
            var article = $(this).find('[nctype="article_item"]');
            $(this).prepend('<div class="cms-thumb" nctype="article_view"><a nctype="article_view" href="'+article.attr("href")+'"><img class="t-img" src="'+article.attr("article_image")+'" /></a></div');
            object_module_edit.append($(this));
        });
    };
    //文章列表1-1保存
    function_list['article_type_4_save'] = function() {
        var count = 1;
        $("#article_selected_list li").each(function(){
            $(this).append('<span nctype="article_view" class="cms-index-article-abstract">'+$(this).find('[nctype="article_item"]').attr("article_abstract")+'</span>');
            object_module_edit.append($(this));
            count++;
        });
    };
    //文章列表2-3保存
    function_list['article_type_5_save'] = function() {
        $("#article_selected_list li").each(function(){
            var article = $(this).find('[nctype="article_item"]');
            $(this).attr("class", "cms-index-article-normal");
            $(this).prepend('<span nctype="article_view" class="cms-index-article-class">'+article.attr("class_name")+'</span>');
            $(this).append('<span nctype="article_view" class="cms-index-article-date">'+article.attr("article_publish_time").substring(8)+'日</span>');
            object_module_edit.append($(this));
        });
    };
    //文章列表保存(文字链接)
    function_list['article_type_6_save'] = function() {
        $("#article_selected_list li").each(function(){
            $(this).attr("class", "cms-index-article-normal");
            object_module_edit.append($(this));
        });
    };

    //商品拖动改变顺序
    $("#goods_selected_list").sortable();
    //商品修改
    $("[nctype='btn_module_goods_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var goods_list = object_module_edit.clone();
        goods_list.find("dl").append('<dd class="taobao-item-delete" nctype="btn_goods_delete" title="删除添加的商品">&nbsp;</dd>');
        limit_count = parseInt($(this).attr("limit_count"), 10);
        if(!limit_count) {
            limit_count = 0;
        }
        $('#dialog_module_goods_edit').nc_show_dialog({width: 640, title: '编辑商品'});
        $("#goods_selected_list").html(goods_list.html());
    });
    //选择商品
    $("#btn_goods_search").live('click', function(){
        var goods_list = $("#goods_selected_list");
        var link_item = $("#input_goods_link");
        var link = link_item.val();
        link_item.val('');
        if($("#goods_selected_list li").length < limit_count || limit_count === 0) {
            if(link != '') {
                var url = encodeURIComponent(link);
                $.getJSON('index.php?act=cms_base&op=goods_info_by_url', { url: url}, function(data){
                    if(data.result == "true") {
                        var temp = '<li nctype="btn_goods_select"><dl>'; 
                        temp += '<dt class="name"><a href="'+data.url+'" target="_blank">'+data.title+'</a></dt>';
                        temp += '<dd class="cms-thumb" title="'+data.title+'"><a href="'+data.url+'" target="_blank"><img src="'+data.image+'" class="t-img"/></a></dd>';
                        temp += '<dd class="price"><em>'+data.price+'</em></dd>';
                        temp += '<dd class="taobao-item-delete" nctype="btn_goods_delete" title="删除添加的商品">&nbsp;</dd>';
                        temp += '</dl></li>';
                        $(goods_list).append(temp);
                    } else {
                        alert(data.message);
                    }
                });
            }
        }
    });
    //删除商品
    $("[nctype='btn_goods_delete']").live('click', function(){
        $(this).parent().parent().remove();
    });
    //保存商品列表
    $("#btn_module_goods_save").live('click', function() {
        var goods_list = $("#goods_selected_list").clone();
        goods_list.find("[nctype='btn_goods_delete']").remove();
        object_module_edit.html(goods_list.html());
        $('#dialog_module_goods_edit').hide();
    });

    //品牌拖动改变顺序
    $("#brand_selected_list").sortable();
    //品牌修改
    $("[nctype='btn_module_brand_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var brand_list = object_module_edit.clone();
        brand_list.find("li").append('<div nctype="btn_brand_select" class="add-brand"><i></i></div>');
        limit_count = parseInt($(this).attr("limit_count"), 10);
        if(!limit_count) {
            limit_count = 0;
        }
        //读取品牌列表
        if($("#div_brand_select_list").html() === '') {
            $("#div_brand_select_list").load("index.php?act=cms_base&op=get_brand_list");
        }
        $('#dialog_module_brand_edit').nc_show_dialog({width: 640, title: '编辑品牌'});
        $("#brand_selected_list").html(brand_list.html());
    });
    //品牌选择翻页
    $("#div_brand_select_list .demo").live('click',function(e){
        $("#div_brand_select_list").load($(this).attr('href'));
        return false;
    });
    //品牌选择
    $("#brand_search_list [nctype='btn_brand_select']").live("click", function(){
        var current_count = $("#brand_selected_list li").length;
        if(current_count < limit_count || limit_count === 0) {
            var temp = $(this).parent().clone();
            $("#brand_selected_list").append($("<div />").append(temp).html());
        }
    });
    //品牌删除
    $("#brand_selected_list [nctype='btn_brand_select']").live("click", function(){
        $(this).parent().remove();
    });
    //品牌列表保存
    $("#btn_module_brand_save").click(function(){
        var brand_list = $("#brand_selected_list").clone();
        brand_list.find("[nctype='btn_brand_select']").remove();
        object_module_edit.html(brand_list.html());
        $('#dialog_module_brand_edit').hide();
    });

    //品牌拖动改变顺序
    $("#goods_class_selected_list").sortable();
    //商品分类修改
    $("[nctype='btn_module_goods_class_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var goods_class_list = object_module_edit.clone();
        goods_class_list.find("dt").prepend("<i>删除</i>");
        goods_class_list.find("dd").prepend("<i>删除</i>");
        //读取品牌列表
        if($("#select_goods_class_list option").length < 1) {
            $("#select_goods_class_list").html("");
            $("#select_goods_class_list").append('<option value="0">请选择</option>');
            $.getJSON("index.php?act=cms_base&op=get_goods_class_list_json", { }, function(result){
                var count = result.length;
                for(var i = 0; i < count; i++) {
                    $("#select_goods_class_list").append('<option value="'+result[i]['gc_id']+'">'+result[i]['gc_name']+'</option>');
                }
            });
        }
        $('#dialog_module_goods_class_edit').nc_show_dialog({width: 640, title: '编辑分类'});
        if(goods_class_list.find("dl").length > 0) {
            $("#goods_class_selected_list").html(goods_class_list.html());
        }
    });
    //商品分类选择
    $("#select_goods_class_list").change(function(){
        $.get("index.php?act=cms_base&op=get_goods_class_detail", {class_id: $(this).val() }, function(result){
            $("#goods_class_selected_list").append(result);
        });
    });
    //商品分类删除
    $("#goods_class_selected_list dt i").live('click', function(){
        $(this).parents("dl").remove();
    });
    $("#goods_class_selected_list dd i").live('click', function(){
        $(this).parents("dd").remove();
    });
    //商品分类列表保存
    $("#btn_module_goods_class_save").click(function(){
        var goods_class_list = $("#goods_class_selected_list").clone();
        goods_class_list.find("i").remove();
        object_module_edit.html(goods_class_list.html());
        $('#dialog_module_goods_class_edit').hide();
    });

    //微商城更新
    $("#btn_module_micro_edit").live('click', function(){
        var micro_url = MICROSHOP_SITE_URL; 
        var api_url = "";
        var micro_html = "";
        var micro_name = "";
        var micro_personal_class = "";
        var micro_personal_commend = "";
        var micro_store_commend = "";
        var count = 0;
        api_url = micro_url+'/index.php?act=api&op=get_micro_name&callback=?';
        $.getJSON(api_url, {data_type: 'html'}, function(result){
            micro_name = result;
            write_html();
        });
        api_url = micro_url+'/index.php?act=api&op=get_personal_class&callback=?';
        $.getJSON(api_url, {data_type: 'html'}, function(result){
            micro_personal_class = result;
            write_html();
        });
        api_url = micro_url+'/index.php?act=api&op=get_personal_commend&callback=?';
        $.getJSON(api_url, {data_type: 'html', data_count: 8}, function(result){
            micro_personal_commend = result;
            write_html();
        });
        api_url = micro_url+'/index.php?act=api&op=get_store_commend&callback=?';
        $.getJSON(api_url, {data_type: 'html', data_count: 10}, function(result){
            micro_store_commend = result;
            write_html();
        });
       function write_html() {
            count++;
            if(count > 3) {
                micro_html += '<div class="cms-module-micro-left">';
                micro_html += '<div class="title-bar">';
                micro_html += '<div class="micro-api-title">'+micro_name+'</div>';
                micro_html += '<div class="micro-api-personal-class">'+micro_personal_class+'</div>';
                micro_html += '<div class="title-more"><a href=' + micro_url + ' class="more" target="_blank">更多</a></div>';
                micro_html += '</div>';
                micro_html += '<div class="micro-api-personal-list">'+micro_personal_commend+'</div>';
                micro_html += '</div>';
                micro_html += '<div class="micro-api-store-list">'+micro_store_commend+'</div>';
                micro_html += '<div class="clear"></div>';
                
                $("#micro_content").html(micro_html);
            }
        }
    });

    //圈子更新
    $("#btn_module_circle_edit").live('click', function(){
        var module_item = $(this).parent().parent().find("[nctype='object_module_edit']");
        var circle_url = CIRCLE_SITE_URL;
        var api_url = "";
        var circle_theme_list = "";
        var circle_reply_themelist = "";
        var circle_more_membertheme = "";
        var count = 0;
        api_url = circle_url+'/index.php?act=api&op=get_theme_list&callback=?';
        $.getJSON(api_url, {data_type: 'html'}, function(result){
            circle_theme_list = result;
            write_html();
        });
        api_url = circle_url+'/index.php?act=api&op=get_reply_themelist&callback=?';
        $.getJSON(api_url, {data_type: 'html'}, function(result){
            circle_reply_themelist = result;
            write_html();
        });
        api_url = circle_url+'/index.php?act=api&op=get_more_membertheme&callback=?';
        $.getJSON(api_url, {data_type: 'html', data_count: 4}, function(result){
            circle_more_membertheme = result;
            write_html();
        });
        function write_html() {
            count++;
            if(count > 2) {
                module_item.html(circle_theme_list + circle_reply_themelist + circle_more_membertheme);
            }
        }
    });

    //店铺拖动改变顺序
    $("#store_selected_list").sortable();
    //店铺修改
    $("[nctype='btn_module_store_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var store_list = object_module_edit.clone();
        store_list.find("li").each(function(){
            $(this).find('dl').append('<dd nctype="btn_store_select" class="handle-button" title="删除"></dd>');
        });
        $('#dialog_module_store_edit').nc_show_dialog({width: 640, title: '编辑店铺'});
        $("#store_selected_list").html(store_list.html());
        $("#store_search_list").html("");
    });
    //店铺搜索
    $("#btn_store_search").click(function(){
        var search_keyword = $("#input_store_search_keyword").val();
        if(search_keyword !== "") {
            $("#div_store_select_list").load("index.php?act=cms_base&op=get_store_list&" + $.param({search_keyword: search_keyword}));
        }
    });
    //店铺选择翻页
    $('#div_store_select_list').on('click', '.demo' ,function(){
        $("#div_store_select_list").load($(this).attr('href'));
        return false;
    });
    //店铺选择
    $('#div_store_select_list').on('click', '[nctype="btn_store_select"]', function(){
        var current_count = $("#article_selected_list li").length;
        var temp = $(this).parents('li').clone();
        temp.find("[nctype='btn_store_select']").attr("title","删除");
        $("#store_selected_list").append($("<div />").append(temp).html());
    });
    //店铺删除
    $('#store_selected_list').on('click', '[nctype="btn_store_select"]', function(){
        $(this).parents('li').remove();
    });
    //店铺列表保存
    $("#btn_module_store_save").click(function(){
        $("#store_selected_list").find("[nctype='btn_store_select']").remove();
        object_module_edit.html("");
        object_module_edit.html($('#store_selected_list').html());
        $('#dialog_module_store_edit').hide();
    });

    //用户拖动改变顺序
    $("#member_selected_list").sortable();
    //用户修改
    $("[nctype='btn_module_member_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        var member_list = object_module_edit.clone();
        member_list.find("li").each(function(){
            $(this).find('dl').append('<dd nctype="btn_member_select" class="handle-button" title="删除"></dd>');
        });
        $('#dialog_module_member_edit').nc_show_dialog({width: 640, title: '编辑会员'});
        $("#member_selected_list").html(member_list.html());
        $("#member_search_list").html("");
    });
    //用户搜索
    $("#btn_member_search").click(function(){
        var search_keyword = $("#input_member_search_keyword").val();
        if(search_keyword !== "") {
            $("#div_member_select_list").load("index.php?act=cms_base&op=get_member_list&" + $.param({search_keyword: search_keyword}));
        }
    });
    //用户选择翻页
    $('#div_member_select_list').on('click', '.demo' ,function(){
        $("#div_member_select_list").load($(this).attr('href'));
        return false;
    });
    //用户选择
    $('#div_member_select_list').on('click', '[nctype="btn_member_select"]', function(){
        var current_count = $("#article_selected_list li").length;
        var temp = $(this).parents('li').clone();
        temp.find("[nctype='btn_member_select']").attr("title","删除");
        $("#member_selected_list").append($("<div />").append(temp).html());
    });
    //用户删除
    $('#member_selected_list').on('click', '[nctype="btn_member_select"]', function(){
        $(this).parent('li').remove();
    });
    //文章列表保存
    $("#btn_module_member_save").click(function(){
        $("#member_selected_list").find("[nctype='btn_member_select']").remove();
        object_module_edit.html("");
        object_module_edit.html($('#member_selected_list').html());
        $('#dialog_module_member_edit').hide();
    });

    //FLASH修改
    $("[nctype='btn_module_flash_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        $('#dialog_module_flash_edit').nc_show_dialog({width: 640, title: '编辑FLASH'});
    });
    //FLASH保存
    $("#btn_module_flash_save").click(function(){
        var data = {};
        data.address = $('#input_flash_address').val();
        data.width = $('#input_flash_width').val();
        data.height = $('#input_flash_height').val();
        object_module_edit.html(template.render('module_assembly_flash_template', data));
        $('#dialog_module_flash_edit').hide();
    });

    //自定义块修改
    $("[nctype='btn_module_html_edit']").click(function(){
        object_module_edit = $(this).parent().parent().find("[nctype='object_module_edit']");
        $('#dialog_module_html_edit').nc_show_dialog({width: 680, title: '编辑自定义块'});
        var KE = KindEditor.create("#textarea_module_html", {
            items : ['source', '|', 'fullscreen', 'undo', 'redo', 'cut', 'copy', 'paste', '|','fontname', 'fontsize', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline','removeformat', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist','insertunorderedlist', '|', 'emoticons', 'image', 'link', '|', 'about'],
            allowImageUpload: false,
            allowFlashUpload: false,
            allowMediaUpload: false,
            allowFileManager: false,
            filterMode: false,
            syncType: "form",
            afterCreate: function() {
                this.sync();
            },
            afterChange: function() {
                this.sync();
            },
            afterBlur: function() {
                this.sync();
            }
        });
        KE.html(object_module_edit.html());
    });
    //自定义块保存
    $("#btn_module_html_save").click(function(){
        object_module_edit.html($('#textarea_module_html').val());
        KindEditor.remove("#textarea_module_html");
        $('#dialog_module_html_edit').hide();
    });

    //保存
    $("#btn_module_save").click(function(){
        $("[nctype='object_module_edit']").each(function(){
            $("#add_form").append('<input name="'+$(this).attr("id")+'" type="hidden" value="" />');
            //去掉无用内容
            $(this).find("[nctype='cms_index_not_save']").remove();
            $("#add_form input").last().val($(this).html());
        });
        $("#add_form").submit();
    });

//提示信息最靠近边界时无法自适应，暂时注释
//提示文字信息居右形式
   // $('.tip-r').poshytip({
		//className: 'tip-yellowsimple',
		//showTimeout: 1,
		//alignTo: 'target',
		//alignX: 'right',
		//alignY: 'center',
		//offsetX: 2,
		//allowTipHover: false
   // });
//提示文字信息居左形式
   // $('.tip-l').poshytip({
		//className: 'tip-yellowsimple',
		//showTimeout: 1,
		//alignTo: 'target',
		//alignX: 'left',
		//alignY: 'center',
		//offsetX: 2,
		//allowTipHover: false
   // });

});

$(window).load(function () {
//比例局中裁切显示图片
	$(".micro-api-personal-list .t-img").VMiddleImg({"width":170,"height":220});
	$(".cms-index-module-article1-1 .t-img").VMiddleImg({"width":380,"height":210});
	$(".cms-index-module-article1-3 .t-img").VMiddleImg({"width":78,"height":78});
	$(".cms-index-module-article2-1 .t-img").VMiddleImg({"width":160,"height":120});
	$(".cms-index-module-article2-2 .t-img").VMiddleImg({"width":160,"height":120});
	$(".cms-index-module-article2-3 .t-img").VMiddleImg({"width":78,"height":38});
	$(".cms-module-assembly-article-image .t-img").VMiddleImg({"width":160,"height":120});
	$(".cms-module-assembly-brand .t-img").VMiddleImg({"width":80,"height":40});
	$("#block1_goods_content .t-img").VMiddleImg({"width":148,"height":148});
	$("#block2_goods_content .t-img").VMiddleImg({"width":148,"height":148});
	$("#block3_goods_content .t-img").VMiddleImg({"width":148,"height":148});
	$("#block4_article_content .t-img").VMiddleImg({"width":148,"height":148});
});


