(function($) {
	$.fn.F_slider = function(options){
		var defaults = {
			page : 1,
			len : 0,		// 滚动篇幅
			axis : 'y'		// y为上下滚动，x为左右滚动
		}
		var options = $.extend(defaults,options);
		return this.each(function(){
			var $this = $(this);
			var len = options.len;
			var page = options.page;
			if(options.axis == 'y'){
				var Val = $(this).find('.F-center').height();
				var Param = 'top';
			}else if(options.axis == 'x'){
				var Val = $(this).find('.F-center').parent().width();
				var Param = 'left';
			}
			$this.find('.F-prev').click(function(){
				if( page == 1){
					eval("$this.find('.F-center').animate({"+Param+":'-=' + Val*(len-1)},'slow');");
					page=len;
				}else{
					eval("$this.find('.F-center').animate({"+Param+":'+=' + Val},'slow');");
					page--;
				}
			});
			$this.find('.F-next').click(function(){
				if(page == len){
					eval("$this.find('.F-center').animate({"+Param+":0},'slow');");
					page=1;
				}else{
					eval("$this.find('.F-center').animate({"+Param+":'-=' + Val},'show');");
					page++;
				}
			});
		});
	}
})(jQuery);