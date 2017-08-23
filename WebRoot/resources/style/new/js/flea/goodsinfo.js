
function slideUp_fn()
{
    $('.ware_cen').slideUp('slow');
}
jQuery(function($) {
$(function(){
	// 默认图片尺寸，开启放大镜。
	var dwidth	= 600;
	var dheight	= 600;  

    //放大镜效果/
    if ($(".jqzoom img").attr('jqimg'))
    {
        $(".jqzoom").jqueryzoom({ xzoom: 430, yzoom: 300, defaultwidth:dwidth, defaultheight:dheight });
    }
    
    // 图片替换效果
    $('.ware_box li').mouseover(function(){
        $('.ware_box li').removeClass();
        $(this).addClass('ware_pic_hover');
        var big_pic = $(this).children('img:first').attr('src');
        $('.big_pic img').attr('src', $(this).attr('bigimg'));
        $('.big_pic img').attr('jqimg', $(this).attr('bigimg'));
        $('.zoomdiv .bigmig').attr('src',$(this).attr('bigimg'));
        $("div.zoomdiv").show();
    	bigwidth = $(".bigimg").get(0).offsetWidth;
        bigheight = $(".bigimg").get(0).offsetHeight;
    	if( bigwidth < dwidth || bigheight < dheight ){
    		$(".ico").hide();
        }else{
        	$("div.zoomdiv").show();
        	$(".ico").show();
        }
    });
    $("div.zoomdiv").show();
    
	bigwidth = $(".bigimg").get(0).offsetWidth;
    bigheight = $(".bigimg").get(0).offsetHeight;
    
	if( bigwidth < dwidth || bigheight < dheight ){
		$(".ico").hide();
    }else{
    	$(".ico").show();
    }
});
});