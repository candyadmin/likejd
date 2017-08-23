$(document).ready(function() {
    var decoration = {};
    
    //当前block
    decoration.current_block_id = null;
    //当前对话框
    decoration.current_dialog = null;
    //当前编辑按钮
    decoration.current_block_edit_button = null;
    //编辑器
    decoration.editor = null;
    //幻灯图片数限制
    decoration.slide_image_limit = 5;
    //导航菜单默认样式
    decoration.default_nav_style = '.ncs-nav { background-color: #D93600; border: 1px solid #B22D00; height: 38px; overflow: hidden; width: 998px; }';
    decoration.default_nav_style += '.ncs-nav ul { white-space: nowrap; display: block; width: 999px; height: 38px; margin-left: -1px; overflow: hidden;}';
    decoration.default_nav_style += '.ncs-nav li a span { font-size: 14px; font-weight: 600; line-height: 20px; text-overflow: ellipsis; white-space: nowrap; max-width:160px; color: #FFF; float: left; height: 20px; padding: 9px 15px; margin-left: 4px; overflow:hidden; cursor:pointer;}';
    //图片热点图片对象
    decoration.$hot_area_image = null;
    //图片热点序号
    decoration.hot_area_index = 1;

    //封装post提交
    decoration.ajax_post = function(url, post, done, always) {
        $.ajax({
            type: "POST",
            url: url,
            data: post, 
            dataType: "json"
        })
        .done(function(data) {
            if(typeof data.error == 'undefined') {
                done(data);
            } else {
                showError(data.error);
            }
        })
        .fail(function() {
            showError('操作失败');
        })
        .always(always);
    }

    //显示模块
    decoration.show_dialog_module = function(module_type, content, full_width) {
        if(typeof full_width == 'undefined') {
            full_width = false;
        }
        var $dialog = $('#dialog_module_' + module_type);
        if($dialog.length > 0) {
            decoration.current_dialog = $dialog;
            $('#dialog_select_module').hide();
            var function_name = 'show_dialog_module_' + module_type;
            decoration[function_name]($dialog, content, full_width);
        } else {
            showError('模块不存在');
        }
    }

    //显示自定义模块对话框
    decoration.show_dialog_module_html = function($dialog, content) {
        $dialog.nc_show_dialog({width: 1020, title: '自定义模块'});
        if(!decoration.editor) {
            decoration.editor = KindEditor.create('#module_html_editor', {
                items : ['source', '|', 'fullscreen', 'undo', 'redo', 'cut', 'copy', 'paste', '|','fontname', 'fontsize', 'forecolor', 'hilitecolor', 'bold', 'italic', 'underline','removeformat', 'justifyleft', 'justifycenter', 'justifyright', 'insertorderedlist','insertunorderedlist', '|', 'image','flash', 'media',  'link', '|', 'about'],
                allowImageUpload: false,
                allowFlashUpload: false,
                allowMediaUpload: false,
                allowFileManager: false,
                filterMode: false,
            });
        }
        decoration.editor.html(content);
    };

    //显示幻灯模块对话框
    decoration.show_dialog_module_slide = function($dialog, content, full_width) {
        $dialog.nc_show_dialog({width: 1020, title: '图片和幻灯'});
        var html = '';
        $(content).find('li').each(function() { 
            var data = {};
            data.image_url = $(this).attr('data-image-url');
            data.image_name = $(this).attr('data-image-name');
            data.image_link = $(this).attr('data-image-link');
            html += template.render('template_module_slide_image_list', data);
        });
        $('#txt_slide_full_width').attr('checked', full_width);
        $('#module_slide_html ul').html(html);
        $('#div_module_slide_upload').hide();
        $('#btn_add_slide_image').show();
    }

    //显示图片热点模块对话框
    decoration.show_dialog_module_hot_area = function($dialog, content) {
        decoration.hot_area_index = 1;

        //图片
        $('#div_module_hot_area_image').html($(content).find('img'));
        decoration.$hot_area_image = $('#div_module_hot_area_image').find('img');
        decoration.$hot_area_image.imgAreaSelect({ 
            handles: true,
            zIndex: 1200,
            fadeSpeed: 200 
        });

        $('#module_hot_area_url').val('');

        var html = '';
        $('#module_hot_area_select_list').html('');
        $(content).find('area').each(function() { 
            var position = $(this).attr('coords');
            var link = $(this).attr('href');
            decoration.add_hot_area(position, link);
        });

        $dialog.nc_show_dialog({
            width: 1020,
            title: '图片热点模块',
            close_callback: function() {
                decoration.hot_area_cancel_selection();
            }
        });
    }

    //显示店铺商品模块对话框
    decoration.show_dialog_module_goods = function($dialog, content) {
        var html = '';
        $(content).find('[nctype="goods_item"]').each(function() {
            $(this).append('<a class="ncsc-btn-mini" nctype="btn_module_goods_operate" href="javascript:;"><i class="icon-ban-circle"></i>取消选择</a>');
            html += $('<div />').append($(this)).html();
        });
        $('#div_module_goods_list').html(html);
        $dialog.nc_show_dialog({width: 1020, title: '店铺商品模块'});
    };

    //块排序
    decoration.sort_decoration_block = function() {
        var sort_string = '';
        $block_list = $('#store_decoration_area').children();
        $block_list.each(function(index, block) {
            sort_string += $(block).attr('data-block-id') + ',';
        });
        $.post(URL_DECORATION_BLOCK_SORT, {sort_string: sort_string}, function(data) {
            if(typeof data.error != 'undefined') {
                showError(data.error);
            }
        }, 'json');
    };

    //保存块内容
    decoration.save_decoration_block = function(html, module_type, full_width) {
        //是否100%宽度设置
        if(typeof full_width == 'undefined') {
            full_width = 0;
        } else {
            full_width = 1;
        }

        var post = { 
            block_id: decoration.current_block_id,
            module_type: module_type,
            full_width: full_width,
            content: html
        };

        decoration.ajax_post(
            URL_DECORATION_BLOCK_SAVE,
            post,
            function(data) {
                decoration.current_block_edit_button.attr('data-module-type', module_type);
                var $block = $('#block_' + decoration.current_block_id);
                if(full_width) {
                    $block.addClass('store-decoration-block-full-width');
                } else {
                    $block.removeClass('store-decoration-block-full-width');
                }
                if(module_type == 'html') {
                    data.html = data.html.replace(/\\"/g, '"');
                }
                $block.find('[nctype="store_decoration_block_module"]').html(data.html);
                decoration.current_dialog.hide();
            }
        );
    };

    decoration.apply_nav_style = function(nav_display, nav_style) {
        if(nav_display == 'true') {
            $('#decoration_nav').show();
        } else {
            $('#decoration_nav').hide();
        }

        $('#style_nav').remove();

        if(nav_style == '') {
            nav_style = decoration.default_nav_style;
            $('#decoration_nav_style').val(decoration.default_nav_style);
        }

        $('head').append('<style id="style_nav">' + nav_style + '</style>');
    };

    decoration.apply_banner = function(banner_display, banner_image_url) {
        var $decoration_banner = $('#decoration_banner');
        if(banner_display == 'true' && banner_image_url != '') {
            $decoration_banner.show();
        } else {
            $decoration_banner.hide();
        }
        $decoration_banner.html('<img src="' + banner_image_url + '" alt="">');
    };

    //添加热点块
    decoration.add_hot_area = function(position, link) {
        var data = {};
        data.link = link;
        data.position = position; 
        data.index = decoration.hot_area_index;
        var html = template.render('template_module_hot_area_list', data);
        $('#module_hot_area_select_list').append(html);

        var position_array = position.split(',');
        var display = {};
        display.width = position_array[2] - position_array[0];
        display.height = position_array[3] - position_array[1];
        display.left = position_array[0];
        display.top = position_array[1];
        display.index = decoration.hot_area_index;
        var display_html = template.render('template_module_hot_area_display', display);
        $('#div_module_hot_area_image').append(display_html);

        decoration.hot_area_index = decoration.hot_area_index + 1;
    };

    //取消热点块选区
    decoration.hot_area_cancel_selection = function() {
        var ias = decoration.$hot_area_image.imgAreaSelect({ instance: true });
        if(typeof ias != 'undefined') {
            ias.cancelSelection();
        }
    };

    //初始化banner
    decoration.apply_banner(
        $("input[name='decoration_banner_display']:checked").val(),
        $('#img_banner_image').attr('src')
    );

    //初始化导航样式
    decoration.apply_nav_style(
        $("input[name='decoration_nav_display']:checked").val(),
        $('#decoration_nav_style').val()
    );

    //编辑背景
    $('#btn_edit_background').on('click', function() {
        $('#dialog_edit_background').nc_show_dialog({width: 640, title: '编辑背景'});
    });

    //上传背景图
    $('#file_background_image').fileupload({
        dataType: 'json',
        url: URL_DECORATION_ALBUM_UPLOAD, 
        add: function (e, data) {
            $('#img_background_image').attr('src', LOADING_IMAGE);
            $('#img_background_image').addClass('loading');
            $('#div_background_image').show();
            data.submit();
        },
        done: function (e, data) {
            var result = data.result;
            $('#img_background_image').removeClass('loading');
            if(typeof result.error == 'undefined') {
                $('#img_background_image').attr('src', result.image_url);
                $('#txt_background_image').val(result.image_name);
                $('#div_background_image').show();
            } else {
                $('#div_background_image').hide();
                showError(result.error);
            }
        }
    });

    //删除背景图
    $('#btn_del_background_image').on('click', function() {
        $('#img_background_image').attr('src', '');
        $('#txt_background_image').val('');
        $('#div_background_image').hide();
    });

    //保存背景
    $('#btn_save_background').on('click', function() {
        var post = { 
            decoration_id: DECORATION_ID,
            background_color: $('#txt_background_color').val(),
            background_image: $('#txt_background_image').val(),
            background_image_repeat: $("input[name='background_repeat']:checked").val(),
            background_position_x: $('#txt_background_position_x').val(),
            background_position_y: $('#txt_background_position_y').val(),
            background_attachment: $('#txt_background_attachment').val()
        };

        decoration.ajax_post(
            URL_DECORATION_BACKGROUND_SETTING_SAVE,
            post,
            function(data) {
                $('#store_decoration_content').attr('style', data.decoration_background_style);
            },
            function() {
                $('#dialog_edit_background').hide();
            }
        );
    });

    //编辑头部
    $('#btn_edit_head').on('click', function() {
        $('#dialog_edit_head').nc_show_dialog({width: 640, title: '编辑头部'});
    });

    //编辑头部弹出窗口tabs
    $('#dialog_edit_head_tabs').tabs();

    //恢复默认导航样式
    $('#btn_default_nav_style').on('click', function() {
        $('#decoration_nav_style').val(decoration.default_nav_style);
    });

    //保存导航样式
    $('#btn_save_decoration_nav').on('click', function() {
        var nav_display = $("input[name='decoration_nav_display']:checked").val();
        var nav_style = $('#decoration_nav_style').val();

        var post = {
            decoration_id: DECORATION_ID,
            nav_display: nav_display,
            content: nav_style
        };
       
        decoration.ajax_post(
            URL_DECORATION_NAV_SAVE,
            post,
            function(data) {
                decoration.apply_nav_style(nav_display, nav_style);
                $('#dialog_edit_head').hide();
            }
        );
    });

    //上传banner图
    $('#file_decoration_banner').fileupload({
        dataType: 'json',
        url: URL_DECORATION_ALBUM_UPLOAD, 
        add: function (e, data) {
            $('#img_banner_image').attr('src', LOADING_IMAGE);
            $('#img_banner_image').addClass('loading');
            $('#div_banner_image').show();
            data.submit();
        },
        done: function (e, data) {
            var result = data.result;
            $('#img_banner_image').removeClass('loading');
            if(typeof result.error == 'undefined') {
                $('#img_banner_image').attr('src', result.image_url);
                $('#txt_banner_image').val(result.image_name);
                $('#div_banner_image').show();
            } else {
                $('#div_banner_image').hide();
                showError(result.error);
            }
        }
    });

    //删除banner图
    $('#btn_del_banner_image').on('click', function() {
        $('#txt_banner_image').val('');
        $('#div_banner_image').hide();
    });

    //保存装修banner设置
    $('#btn_save_decoration_banner').on('click', function() {
        var banner_display = $("input[name='decoration_banner_display']:checked").val();
        var banner_image = $('#txt_banner_image').val();

        var post = {
            decoration_id: DECORATION_ID,
            banner_display: banner_display,
            content: banner_image
        };
       
        decoration.ajax_post(
            URL_DECORATION_BANNER_SAVE,
            post,
            function(data) {
                decoration.apply_banner(banner_display, data.image_url);
                $('#dialog_edit_head').hide();
            }
        );
    });

    //添加块
    $('#btn_add_block').on('click', function() {
        var post = { 
            decoration_id: DECORATION_ID,
            block_layout: 'block_1'
        };

        decoration.ajax_post(
            URL_DECORATION_BLOCK_ADD,
            post,
            function(data) {
                $('#store_decoration_area').append(data.html);

                //title提示
                $('.tip').poshytip(POSHYTIP);

                //滚动到底部
                $("html, body").animate({ scrollTop: $(document).height() }, 1000);

                //块排序
                decoration.sort_decoration_block();
            }
        );
    });

    //删除块
    $('#store_decoration_area').on('click', '[nctype="btn_del_block"]', function() {
        $this = $(this);
        if(confirm('确认删除？')) {
            var post = { 
                block_id: $(this).attr('data-block-id')
            };

            decoration.ajax_post(
                URL_DECORATION_BLOCK_DEL,
                post,
                function(data) {
                    $this.parents('[nctype="store_decoration_block"]').hide();
                }
            );
        }
    });

    //装修块拖拽排序
    $( "#store_decoration_area" ).sortable({
        update: function(event, ui) {
            decoration.sort_decoration_block();
        }
    });

    //添加模块
    $('#store_decoration_area').on('click', '[nctype="btn_edit_module"]', function() {
        var module_type = $(this).attr('data-module-type');
        decoration.current_block_id = $(this).attr('data-block-id');
        decoration.current_block_edit_button = $(this);
        if(module_type == '') {
            //新模块弹出模块选择对话框
            $('#dialog_select_module').nc_show_dialog({width: 480, title: '选择模块'});
        } else {
            //已有模块直接编辑
            var $block = $('#block_' + decoration.current_block_id);
            var content = $block.find('[nctype="store_decoration_block_module"]').html();
            var full_width = $block.hasClass('store-decoration-block-full-width');
            decoration.show_dialog_module(module_type, content, full_width);
        }
    });

    //模块选择对话框选择模块类型后打开对应的模块编辑对话框
    $('[nctype="btn_show_module_dialog"]').on('click', function() {
        var module_type = $(this).attr('data-module-type');
        decoration.show_dialog_module(module_type);
    });

    //自定义模块保存
    $('#btn_save_module_html').on('click', function() {
        decoration.editor.sync();
        var html = $('#module_html_editor').val();

        decoration.save_decoration_block(html, 'html');
    });

    //添加幻灯图片
    $('#btn_add_slide_image').on('click', function() {
        var image_count = $('#module_slide_html ul').children().length;
        if(image_count >= decoration.slide_image_limit) {
            showError('每个幻灯片最多只能上传' + decoration.slide_image_limit + '张图片');
            return;
        }
        $('#div_module_slide_image').html('');
        $('#module_slide_url').val('');
        $('#div_module_slide_upload').show();
        $('#btn_add_slide_image').hide();
    });

    //幻灯图片上传
    $('[nctype="btn_module_slide_upload"]').fileupload({
        dataType: 'json',
        url: URL_DECORATION_ALBUM_UPLOAD, 
        add: function (e, data) {
            $('#div_module_slide_image').html('<img class="loading" src="' + LOADING_IMAGE + '">');
            data.submit();
        },
        done: function (e, data) {
            var result = data.result;
            if(typeof result.error == 'undefined') {
                $('#div_module_slide_image').html('<img src="' + result.image_url + '" data-image-name="' + result.image_name + '">');
            } else {
                $('#div_module_slide_image').html('');
                showError(result.error);
            }
        }
    });

    //保存添加的幻灯图片
    $('#btn_save_add_slide_image').on('click', function() {
        var data = {};
        $image = $('#div_module_slide_image img');
        if($image.length > 0) {
            data.image_url = $image.attr('src');
            data.image_name = $image.attr('data-image-name');
            data.image_link = $('#module_slide_url').val();

            var html = template.render('template_module_slide_image_list', data);
            $('#module_slide_html ul').append(html);
            $('#div_module_slide_upload').hide();
            $('#btn_add_slide_image').show();
        } else {
            showError('请上传图片');
        }
    });

    //幻灯片模块图片删除
    $('#module_slide_html').on('click', '[nctype="btn_del_slide_image"]', function() {
        $(this).parents('li').remove();
    });

    //取消添加幻灯图片
    $('#btn_cancel_add_slide_image').on('click', function() {
        $('#div_module_slide_upload').hide();
        $('#btn_add_slide_image').show();
    });

    //幻灯模块保存
    $('#btn_save_module_slide').on('click', function() {
        var data = {};
        var i = 0;
        data.height = parseInt($('#txt_slide_height').val(), 10);

        //验证高度
        if(isNaN(data.height)) {
            showError('请输入正确的显示高度');
            return;
        }

        data.images = [];
        $('#module_slide_html li').each(function() { 
            var image = {};
            image.image_name = $(this).attr('data-image-name');
            image.image_link = $(this).attr('data-image-link');
            data.images[i] = image;
            i++;
        });
        decoration.save_decoration_block(data, 'slide', $('#txt_slide_full_width').attr('checked'));
    });

    //热点图片上传
    $('[nctype="btn_module_hot_area_upload"]').fileupload({
        dataType: 'json',
        url: URL_DECORATION_ALBUM_UPLOAD, 
        add: function (e, data) {
            $('#div_module_hot_area_image').html('<img class="loading" src="' + LOADING_IMAGE + '">');
            data.submit();
        },
        done: function (e, data) {
            var result = data.result;
            if(typeof result.error == 'undefined') {
                $('#div_module_hot_area_image').html('<img src="' + result.image_url + '" data-image-name="' + result.image_name + '">');
                decoration.$hot_area_image = $('#div_module_hot_area_image').find('img');
                decoration.$hot_area_image.imgAreaSelect({ 
                    handles: true,
                    zIndex: 1200,
                    fadeSpeed: 200 
                });
            } else {
                $('#div_module_hot_area_image').html('');
                showError(result.error);
            }
        }
    });

    //添加热点区域
    $('#btn_module_hot_area_add').on('click', function() {
        var ias = decoration.$hot_area_image.imgAreaSelect({ instance: true });
        var selection = ias.getSelection();
        if (!selection.width || !selection.height) {
            showError('请选择热点区域');
            return;
        }

        //添加热点块
        var position = selection.x1 + ',' + selection.y1 + ',' + selection.x2 + ',' + selection.y2; 
        var link = $('#module_hot_area_url').val();
        decoration.add_hot_area(position, link);

        decoration.hot_area_cancel_selection();
    });

    //选择图片热点块
    $('#dialog_module_hot_area').on('click', '[nctype="btn_module_hot_area_select"]', function() {
        var position = $(this).attr('data-hot-area-position').split(',');
        var ias = decoration.$hot_area_image.imgAreaSelect({ instance: true });
        ias.setSelection(position[0], position[1], position[2], position[3], true);
        ias.setOptions({ show: true });
        ias.update();
    });

    //删除图片热点块
    $('#dialog_module_hot_area').on('click', '[nctype="btn_module_hot_area_del"]', function() {
        var display_id = $(this).attr('data-index');
        $('#hot_area_display_' + display_id).remove();
        $(this).parents('li').remove();
    });

    //图片热点模块保存
    $('#btn_save_module_hot_area').on('click', function() {
        var data = {};
        var i = 0;
        data.image = decoration.$hot_area_image.attr('data-image-name');
        if(data.image == '') {
            showError('请首先上传图片并添加热点');
            return;
        }

        data.areas = [];
        $('#module_hot_area_select_list li').each(function() { 
            var area = {};
            var position = $(this).attr('data-hot-area-position').split(',');
            area.x1 = position[0];
            area.y1 = position[1];
            area.x2 = position[2];
            area.y2 = position[3];
            area.link= $(this).attr('data-hot-area-link');
            data.areas[i] = area;
            i++;
        });

        decoration.hot_area_cancel_selection();

        decoration.save_decoration_block(data, 'hot_area');
    });

    //商品模块搜索
    $('#btn_module_goods_search').on('click', function() {
        var param = '&' + $.param({keyword: $('#txt_goods_search_keyword').val()});
        $('#div_module_goods_search_list').load(URL_DECORATION_GOODS_SEARCH + param);
    });

    //商品模块搜索结果翻页
    $('#div_module_goods_search_list').on('click', 'a.demo', function() {
        $('#div_module_goods_search_list').load($(this).attr('href'));
        return false;
    });

    //商品添加
    $('#div_module_goods_search_list').on('click', '[nctype="btn_module_goods_operate"]', function() {
        var $goods = $(this).parents('[nctype="goods_item"]').clone();
        $goods.find('[nctype="btn_module_goods_operate"]').html('<i class="icon-ban-circle"></i>取消选择');
        $('#div_module_goods_list').append($goods);
    });

    //商品删除
    $('#div_module_goods_list').on('click', '[nctype="btn_module_goods_operate"]', function() {
        $(this).parents('[nctype="goods_item"]').remove();
    });

    //商品模块保存
    $('#btn_save_module_goods').on('click', function() {
        var data = [];
        var i = 0;

        $('#div_module_goods_list').find('[nctype="goods_item"]').each(function() { 
            var goods = {};
            goods.goods_id = $(this).attr('data-goods-id');
            goods.goods_name = $(this).attr('data-goods-name');
            goods.goods_price = $(this).attr('data-goods-price');
            goods.goods_image = $(this).attr('data-goods-image');
            data[i] = goods;
            i++;
        });

        decoration.save_decoration_block(data, 'goods');
    });

    //关闭窗口
    $('#btn_close').on('click', function() {
        window.close();
    });
});
