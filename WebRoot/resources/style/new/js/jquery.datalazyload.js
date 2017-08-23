/*
* 数据延迟加载jQuery插件
* Copyright: Leepy
* Update: 2010-05-15
* Home:   http://www.cnblogs.com/liping13599168/
*/

(function($) {
    //包装延迟加载容器
    $.fn.wraplazyload = function(value) {
        this.html('<textarea class="text-lazyload">' + value + '</textarea>');
    };

    //开始进行延迟加载
    $.fn.datalazyload = function(options) {
        var settings = {
            dataContainer: '.text-lazyload',
            dataItem: '.item-lazyload',
            loadType: 'item',                   //可以为item（条目加载）,img（图片加载）
            container: window,
            event: 'scroll',
            effect: 'normal',                   //效果类型
            effectTime: 1000,                   //效果持续时间
            position: 'vertical'                //可以为vertical（垂直加载），horizontal（水平加载）
        };

        if (options) {
            $.extend(settings, options);
        }

        var dataContainer = this.find(settings.dataContainer);
        if (dataContainer.length == 0) {
            alert('请先包装延迟加载的容器.');
        }

        dataContainer.each(
            function() {
                var content = $('<div>' + $(this).val().replace(/src=/gi, 'dynamic=') + '</div>');
                $(this).after(content);

                var element = settings.loadType == 'item' ? content.find(settings.dataItem) : (settings.loadType == 'image' ? content.find('img') : null);
                element.each(function() { $(this).css('visibility', 'hidden'); }); //为了兼容非IE浏览器对visibilty的识别
                if (element == null) {
                    alert('未引用对象实例.');
                    return;
                }

                $(settings.container).bind(settings.event, function(event) {

                    var pixel = 0;
                    if (settings.position == 'vertical') {
                        pixel = $(settings.container).height() + $(settings.container).scrollTop();
                    }
                    else if (settings.position == 'horizontal') {
                        pixel = $(settings.container).width() + $(settings.container).scrollLeft();
                    }

                    element.each(function() {
                        if ($(this).css('visibility') != 'visible' &&
                            (settings.position == 'vertical' && pixel >= $(this).offset().top
                                || settings.position == 'horizontal' && pixel >= $(this).offset().left)) {

                            $(this).css('visibility', 'visible');

                            if (settings.effect == 'fadeIn') {
                                $(this).hide();
                                $(this)[settings.effect](settings.effectTime);
                            }
                            
                            if (settings.loadType == 'item')
                                $(this).html($(this).html().replace(/dynamic=/gi, 'src='));
                            else if (settings.loadType == 'image')
                                $(this).attr('src', $(this).attr('dynamic')).removeAttr('dynamic');
                        }
                    });
                });
                //初次加载数据触发事件
                $(settings.container).trigger(settings.event);
            }
        );
        dataContainer.remove();
    };

})(jQuery);