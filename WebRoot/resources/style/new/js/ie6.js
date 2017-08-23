$(function(){
	if ($.browser.msie && $.browser.version=="6.0"){
		var content='<div style="line-height: 24px; font-weight: 600; color: #F60; text-align:center; background: #FFC; width: 100%; border-bottom: 1px solid #F60;">';
		content+='<div>';
		content+='<span style="text-align:center;color:#FF6600;">';
		content+='<b>提示：您正在使用IE6内核浏览器，为了您更好的使用网站功能，建议您升级IE浏览器或改用其他内核浏览器。</b>';
		content+='</span>';
		content+='</div>';
		content+='</div>';
		$("body").prepend(content);
	}
});