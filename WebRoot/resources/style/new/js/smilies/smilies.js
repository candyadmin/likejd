$(function(){
	//表情模块
	//IE9下第一次弹出是位置总是定位错误，经过测试，发现找不到相对定位层，只要在正确的定位之前，先随意设置一个位置，再次正确点击的时候位置就正确了。所以添加了以下代码。
	$("#smilies_div").position({
		of: $("body"),
		at: "left bottom",
		offset: "10 10"
	});
    $("[nc_type='smiliesbtn']").live('click',function(){
    	//光标处插入代码功能
        $("[nc_type='contenttxt']").setCaret();
        var data = $(this).attr('data-param');
        eval( "data = "+data);
        smiliesshowdiv(data.txtid,this);        
    });
});
//显示和隐藏表情模块
function smiliesshowdiv(txtid,btnobj){
	if($('#smilies_div').css("display")=='none'){
		if($('#smilies_div').html() == ''){
			smilies_show('smiliesdiv', 8, 'e_',$("#content_"+txtid));
		}
		$('#smilies_div').show();
		smiliesposition(btnobj);
	}else{
		$('#smilies_div').hide();
	}
}
//弹出层位置控制
function smiliesposition(btnobj){
	$("#smilies_div").position({
		of: btnobj,
		at: "left bottom",
		offset: "105 57"
	});
}