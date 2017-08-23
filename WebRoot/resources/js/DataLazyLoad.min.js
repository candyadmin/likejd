/*
 * Data Lazy Load - jQuery plugin for lazy loading data
 * Copyright (c) 2007-2013 logbird
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 * Project home:
 *   https://github.com/logbird/DataLazyLoad
 * Version:  1.0.0
 */
(function($, window, undefined) {
	$.fn.DataLazyLoad = function(options) {
		var elements = $(this);
		var allheight = 0;
		var settings = {
			//Data Load Offset
			offset : 200,
			//Load data callback
			load : function () {},
			//Which page to load
			page : 2
		}
		if (options)
		{
			$.extend(settings, options);
		}
		//The height of the browser window
		var winHeight = $(window).height();
		var locked = false;
		var unLock = function (nextPage) {
			//Next load page, 0 is end
			if (nextPage > 0)
			{
				allheight += $(elements[settings.page]).height();
				settings.page = nextPage;
				locked = false;

				init();
			}
		}

		var init=function() {
			//由于屏幕高度过大，把预加载的内容露出来
			var scrollTop = $(window).scrollTop();
			var ttop= $(elements).offset().top;
			var offset = ttop + allheight  - (scrollTop + winHeight);
			if(offset < settings.offset && !locked){
				locked = true;
				settings.load(settings.page, unLock);
			}
			//end
		}

		init();

		$(window).scroll(function () {
			var scrollTop = $(window).scrollTop();
			//elements height + elements top - (scrollbar top + browser window height)
			var offset = $(elements).offset().top + $(elements).height() - (scrollTop + winHeight);
			if(offset < settings.offset && !locked){
				locked = true;
				settings.load(settings.page, unLock);
			}
		});
	}
})(jQuery, window);
