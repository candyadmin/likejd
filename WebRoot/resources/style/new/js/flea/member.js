$(function(){
    /* 全选 */
    $('.checkall').click(function(){
        var _self = this;
        $('.checkitem').each(function(){
            if (!this.disabled)
            {
                $(this).attr('checked', _self.checked);
            }
        });
        $('.checkall').attr('checked', this.checked);
    });

    /* 批量操作按钮 */
    $('a[nc_type="batchbutton"]').click(function(){
        /* 是否有选择 */
        if($('.checkitem:checked').length == 0){    //没有选择
            return false;
        }
        /* 运行presubmit */
        if($(this).attr('presubmit')){
            if(!eval($(this).attr('presubmit'))){
                return false;
            }
        }
        /* 获取选中的项 */
        var items = '';
        $('.checkitem:checked').each(function(){
            items += this.value + ',';
        });
        items = items.substr(0, (items.length - 1));
        /* 将选中的项通过GET方式提交给指定的URI */
        var uri = $(this).attr('uri');
        window.location = uri + '&' + $(this).attr('name') + '=' + items;
        return false;
    });

    /* 缩小大图片 */
    $('.makesmall').each(function(){
        makesmall(this, $(this).attr('max_width'), $(this).attr('max_height'));
    });

    $('.su_btn').click(function(){
        if($(this).hasClass('close')){
            $(this).parent().next('.su_block').css('display', '');
            $(this).removeClass('close');
        }
        else{
            $(this).addClass('close');
            $(this).parent().next('.su_block').css('display', 'none');
        }
    });

    $('*[nc_type="dialog"]').click(function(){
        var id = $(this).attr('dialog_id');
        var title = $(this).attr('dialog_title') ? $(this).attr('dialog_title') : '';
        var url = $(this).attr('uri');
        var width = $(this).attr('dialog_width');
        ajax_form(id, title, url, width);
        return false;
    });

    var url = window.location.search;
    var params  = url.substr(1).split('&');
    var act = '';
    //找出排序的列和排序的方式及app控制器
    var sort  = '';
    var order = '';
    for(var j=0; j < params.length; j++)
    {
        var param = params[j];
        var arr   = param.split('=');
        if(arr[0] == 'act')
        {
            act = arr[1];
        }
        if(arr[0] == 'sort')
        {
            sort = arr[1];
        }
        if(arr[0] == 'order')
        {
            order = arr[1];
        }
    }
    $('span[nc_type="order_by"]').each(function(){
        if($(this).parent().attr('column') == sort)
        {
            if(order == 'asc')
            {
            $(this).removeClass();
            $(this).addClass("sort_asc");
            }
        else if (order == 'desc')
            {
            $(this).removeClass();
            $(this).addClass("sort_desc");
            }
        }
    });
    $('span[nc_type="order_by"]').click(function(){
        var s_name = $(this).parent().attr('column');
        var found   = false;
        for(var i = 0;i < params.length;i++)
        {
            var param = params[i];
            var arr   = param.split('=');
            if('page' == arr[0])
            {
                params[i] = 'page=1';
            }
            else if('sort' == arr[0])
            {
                params[i] = 'sort'+'='+ s_name;
                found = true;
            }
            else if('order' == arr[0])
            {
                params[i] = 'order'+'='+(arr[1] =='asc' ? 'desc' : 'asc');
            }
        }
        if(!found)
        {
                params.push('sort'+'='+ s_name);
                params.push('order=asc');
        }
        if(location.pathname.indexOf('/admin/')>-1)
        {
                location.assign(SITE_URL + '/admin/index.php?' + params.join('&'));
                return;
        }
        location.assign(SITE_URL + '/index.php?' + params.join('&'));
    });
});