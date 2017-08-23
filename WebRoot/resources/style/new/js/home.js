$(function(){
	//代金券兑换功能
    $("[nc_type='exchangebtn']").live('click',function(){
    	var data_str = $(this).attr('data-param');
	    eval( "data_str = "+data_str);
	    ajaxget('index.php?act=pointvoucher&op=voucherexchange&dialog=1&vid='+data_str.vid);
	    return false;
    });
});