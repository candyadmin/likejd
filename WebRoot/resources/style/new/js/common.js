function drop_confirm(msg, url){
    if(confirm(msg)){
        window.location = url;
    }
}
function go(url){
    window.location = url;
}
/* 格式化金额 */
function price_format(price){
    if(typeof(PRICE_FORMAT) == 'undefined'){
        PRICE_FORMAT = '&yen;%s';
    }
    price = number_format(price, 2);

    return PRICE_FORMAT.replace('%s', price);
}
function number_format(num, ext){
    if(ext < 0){
        return num;
    }
    num = Number(num);
    if(isNaN(num)){
        num = 0;
    }
    var _str = num.toString();
    var _arr = _str.split('.');
    var _int = _arr[0];
    var _flt = _arr[1];
    if(_str.indexOf('.') == -1){
        /* 找不到小数点，则添加 */
        if(ext == 0){
            return _str;
        }
        var _tmp = '';
        for(var i = 0; i < ext; i++){
            _tmp += '0';
        }
        _str = _str + '.' + _tmp;
    }else{
        if(_flt.length == ext){
            return _str;
        }
        /* 找得到小数点，则截取 */
        if(_flt.length > ext){
            _str = _str.substr(0, _str.length - (_flt.length - ext));
            if(ext == 0){
                _str = _int;
            }
        }else{
            for(var i = 0; i < ext - _flt.length; i++){
                _str += '0';
            }
        }
    }

    return _str;
}
/* 火狐下取本地全路径 */
function getFullPath(obj)
{
    if(obj)
    {
        //ie
        if (window.navigator.userAgent.indexOf("MSIE")>=1)
        {
            obj.select();
            if(window.navigator.userAgent.indexOf("MSIE") == 25){
                obj.blur();
            }
            return document.selection.createRange().text;
        }
        //firefox
        else if(window.navigator.userAgent.indexOf("Firefox")>=1)
        {
            if(obj.files)
            {
                //return obj.files.item(0).getAsDataURL();
                return window.URL.createObjectURL(obj.files.item(0));
            }
            return obj.value;
        }
        return obj.value;
    }
}
/* 转化JS跳转中的 ＆ */
function transform_char(str)
{
    if(str.indexOf('&'))
    {
        str = str.replace(/&/g, "%26");
    }
    return str;
}
//图片垂直水平缩放裁切显示
(function($){
    $.fn.VMiddleImg = function(options) {
        var defaults={
            "width":null,
            "height":null
        };
        var opts = $.extend({},defaults,options);
        return $(this).each(function() {
            var $this = $(this);
            var objHeight = $this.height(); //图片高度
            var objWidth = $this.width(); //图片宽度
            var parentHeight = opts.height||$this.parent().height(); //图片父容器高度
            var parentWidth = opts.width||$this.parent().width(); //图片父容器宽度
            var ratio = objHeight / objWidth;
            if (objHeight > parentHeight && objWidth > parentWidth) {
                if (objHeight > objWidth) { //赋值宽高
                    $this.width(parentWidth);
                    $this.height(parentWidth * ratio);
                } else {
                    $this.height(parentHeight);
                    $this.width(parentHeight / ratio);
                }
                objHeight = $this.height(); //重新获取宽高
                objWidth = $this.width();
                if (objHeight > objWidth) {
                    $this.css("top", (parentHeight - objHeight) / 2);
                    //定义top属性
                } else {
                    //定义left属性
                    $this.css("left", (parentWidth - objWidth) / 2);
                }
            }
            else {
                if (objWidth > parentWidth) {
                    $this.css("left", (parentWidth - objWidth) / 2);
                }
                $this.css("top", (parentHeight - objHeight) / 2);
            }
        });
    };
})(jQuery);
function DrawImage(ImgD,FitWidth,FitHeight){
    var image=new Image();
    image.src=ImgD.src;
    if(image.width>0 && image.height>0)
    {
        if(image.width/image.height>= FitWidth/FitHeight)
        {
            if(image.width>FitWidth)
            {
                ImgD.width=FitWidth;
                ImgD.height=(image.height*FitWidth)/image.width;
            }
            else
            {
                ImgD.width=image.width;
                ImgD.height=image.height;
            }
        }
        else
        {
            if(image.height>FitHeight)
            {
                ImgD.height=FitHeight;
                ImgD.width=(image.width*FitHeight)/image.height;
            }
            else
            {
                ImgD.width=image.width;
                ImgD.height=image.height;
            }
        }
    }
}

/**
 * 浮动DIV定时显示提示信息,如操作成功, 失败等
 * @param string tips (提示的内容)
 * @param int height 显示的信息距离浏览器顶部的高度
 * @param int time 显示的时间(按秒算), time > 0
 * @sample <a href="javascript:void(0);" onclick="showTips( '操作成功', 100, 3 );">点击</a>
 * @sample 上面代码表示点击后显示操作成功3秒钟, 距离顶部100px
 * @copyright ZhouHr 2010-08-27
 */
function showTips( tips, height, time ){
    var windowWidth = document.documentElement.clientWidth;
    var tipsDiv = '<div class="tipsClass">' + tips + '</div>';

    $( 'body' ).append( tipsDiv );
    $( 'div.tipsClass' ).css({
        'top' : 200 + 'px',
        'left' : ( windowWidth / 2 ) - ( tips.length * 13 / 2 ) + 'px',
        'position' : 'fixed',
        'padding' : '20px 50px',
        'background': '#EAF2FB',
        'font-size' : 14 + 'px',
        'margin' : '0 auto',
        'text-align': 'center',
        'width' : 'auto',
        'color' : '#333',
        'border' : 'solid 1px #A8CAED',
        'opacity' : '0.90',
        'z-index' : '9999'
    }).show();
    setTimeout( function(){$( 'div.tipsClass' ).fadeOut().remove();}, ( time * 1000 ) );
}

function trim(str) {
    return (str + '').replace(/(\s+)$/g, '').replace(/^\s+/g, '');
}

/* 显示Ajax表单 */
function ajax_form(id, title, url, width, model)
{
    if (!width)	width = 480;
    if (!model) model = 1;
    var d = DialogManager.create(id);
    d.setTitle(title);
    d.setContents('ajax', url);
    d.setWidth(width);
    d.show('center',model);
    return d;
}
//显示一个内容为自定义HTML内容的消息
function html_form(id, title, _html, width, model) {
    if (!width)	width = 480;
    if (!model) model = 0;
    var d = DialogManager.create(id);
    d.setTitle(title);
    d.setContents(_html);
    d.setWidth(width);
    d.show('center',model);
    return d;
}

/*
 * 为低版本IE添加placeholder效果
 *
 * 使用范例：
 * [html]
 * <input id="captcha" name="captcha" type="text" placeholder="验证码" value="" >
 * [javascrpt]
 * $("#captcha").nc_placeholder();
 *
 * 生效后提交表单时，placeholder的内容会被提交到服务器，提交前需要把值清空
 * 范例：
 * $('[data-placeholder="placeholder"]').val("");
 * $("#form").submit();
 *
 */
(function($) {
    $.fn.nc_placeholder = function() {
        var isPlaceholder = 'placeholder' in document.createElement('input');
        return this.each(function() {
            if(!isPlaceholder) {
                $el = $(this);
                $el.focus(function() {
                    if($el.attr("placeholder") === $el.val()) {
                        $el.val("");
                        $el.attr("data-placeholder", "");
                    }
                }).blur(function() {
                    if($el.val() === "") {
                        $el.val($el.attr("placeholder"));
                        $el.attr("data-placeholder", "placeholder");
                    }
                }).blur();
            }
        });
    };
})(jQuery);

/*
 * 弹出窗口
 */
(function($) {
    $.fn.nc_show_dialog = function(options) {

        var that = $(this);
        var settings = $.extend({}, {width: 480, title: ''}, options);

        var init_dialog = function(title) {
            var _div = that;
            that.addClass("dialog_wrapper");
            that.wrapInner(function(){
                return '<div class="dialog_content">';
            });
            that.wrapInner(function(){
                return '<div class="dialog_body" style="position: relative;">';
            });
            that.find('.dialog_body').prepend('<h3 class="dialog_head" style="cursor: move;"><span class="dialog_title"><span class="dialog_title_icon">'+settings.title+'</span></span><span class="dialog_close_button">X</span></h3>');
            that.append('<div style="clear:both;"></div>');

            $(".dialog_close_button").click(function(){
                _div.hide();
            });

            that.draggable();
        };

        if(!$(this).hasClass("dialog_wrapper")) {
            init_dialog(settings.title);
        }
        settings.left = $(window).scrollLeft() + ($(window).width() - settings.width) / 2;
        settings.top  = ($(window).height() - $(this).height()) / 2;
        $(this).attr("style","display:none; z-index: 1100; position: fixed; width: "+settings.width+"px; left: "+settings.left+"px; top: "+settings.top+"px;");
        $(this).show();

    };
})(jQuery);

/*
 * 地址联动选择
 * input不为空时出现编辑按钮，点击按钮进行联动选择
 *
 * 使用范例：
 * [html]
 * <input id="region" name="region" type="hidden" value="" >
 * [javascrpt]
 * $("#region").nc_region();
 *
 */

(function($) {
    $.fn.nc_region = function(options) {
        var $region = $(this);
        var settings = $.extend({}, {area_id: 0, region_span_class: "_region_value"}, options);

        return this.each(function() {
            var $inputArea = $(this);
            if($inputArea.val() === '') {
                initArea($inputArea);
            } else {
                var $region_span = $('<span class="' + settings.region_span_class + '">' + $inputArea.val() + '</span>');
                var $region_btn = $('<input type="button" value="编辑" />');
                $inputArea.after($region_span);
                $region_span.after($region_btn);
                $region_btn.on("click", function() {
                    $region_span.hide();
                    $region_btn.hide();
                    initArea($inputArea);
                });
            }
        });

        function initArea($inputArea) {
            settings.$area = $('<select></select>');
            $inputArea.after(settings.$area);
            loadAreaArray(function() {
                loadArea(settings.$area, settings.area_id);
            });
        }

        function loadArea($area, area_id){
            if($area && nc_a[area_id].length > 0){
                var areas = [];
                areas = nc_a[area_id];
                $area.append("<option>-请选择-</option>");
                for (i = 0; i <areas.length; i++){
                    $area.append("<option value='" + areas[i][0] + "'>" + areas[i][1] + "</option>");
                }
            }

            $area.on('change', function() {
                $(this).nextAll("select").remove();

                var region_value = '';
                $region.nextAll("select").each(function() {
                    region_value += $(this).find("option:selected").text() + ' ';
                });
                $region.val(region_value);

                var area_id = $(this).val();
                if(area_id > 0) {
                    if(nc_a[area_id] && nc_a[area_id].length > 0) {
                        var $newArea = $('<select></select>');
                        $(this).after($newArea);
                        loadArea($newArea, area_id);
                    }
                }
            });
        }

        function loadAreaArray(callback) {
            if(typeof nc_a === 'undefined') {
                //取JS目录的地址
                var area_scripts_src = '';
                area_scripts_src = $("script[src*='jquery.js']").attr("src");
                area_scripts_src = area_scripts_src.replace('jquery.js', 'area_array.js');
                $.ajax({
                    url: area_scripts_src,
                    async: false,
                    dataType: "script"
                }).done(function(){
                    callback();
                });
            } else {
                callback();
            }
        }
    };
})(jQuery);

