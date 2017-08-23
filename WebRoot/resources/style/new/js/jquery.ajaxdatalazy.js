(function($) {
	var pageNo = 1;   //延迟加载翻页，默认设为第 1 页
	$.fn.lazyinit = function(options){
		var dataContainer = $(this);
		//实现好友动态分页		
		$(this).find('.pagination').find('.demo').live('click',function(){
			var url = $(this).attr('nc_url');
			$(dataContainer).lazyshow({url:url,iIntervalId:true});
		});
		$(this).find('.lazymore').find('a').live('click',function(){
			var url = $(this).attr('nc_url');
			$(dataContainer).lazyshow({url:url,iIntervalId:false});
		});
	}
	$.fn.lazyshow = function(options) {
		var settings = {
            iIntervalId:true
        };
		//异步加载的页面url
		var loadurl = options.url;
		//初始化时
		settings.iIntervalId = options.iIntervalId;
		//列表容器
		var dataContainer = $(this);
		//如果临时列表模块不存在，则追加一个该模块
		if(!$(dataContainer).next("#lazytmp")[0]){
			$(dataContainer).after('<div id="lazytmp"></div>');
		}
		//初始化时延时分页为1
		if(settings.iIntervalId){
			pageNo = 1;
		}
		//删除加载更多的连接
		$("#lazymore").remove();
		//加载好友动态
		$("#lazytmp").load(loadurl+'&delaypage='+pageNo,'',function(){
			//获取load的html追加到列表中，同时清除临时列表
			var html = '';
			html += $("#lazymodule").html();
			if(settings.iIntervalId === false){
				$(dataContainer).append(html);
			}else{
				$(dataContainer).html(html);
			}
			$("#lazytmp").html('');
			
			//修改加载更多的html
			$(dataContainer).find("#lazymore").html('<div class="lazymore"><a href="javascript:void(0);" nc_url="'+loadurl+'">查看更多动态</a></div>');
			//修改分页连接为onclick事件
			$(dataContainer).find('.pagination').find('.demo').each(function(){
				var a_url = $(this).attr('href');
				if(a_url != '' && a_url != undefined){
					$(this).attr('nc_url',a_url);	
				}
				$(this).attr('href','javascript:void(0);');
			});
			
			// Membership card
			$(dataContainer).find('[nctype="mcard"]').membershipCard({type:'shop'});
		});
		pageNo++;//延时分页自增一
    };
})(jQuery);