// JavaScript Document

$(function(){
	$(".tooltip").simpletooltip();
});

(function($){

	$.fn.simpletooltip = function(){
    return this.each(function() {
        var text = $(this).attr("title");
        $(this).attr("title", "");
        if(text != undefined) {
            $(this).hover(function(e){
                var tipX = e.pageX + 12;
                var tipY = e.pageY + 12;
                $(this).attr("title", "");
                $("body").append("<div id='simpleTooltip'>"+ text + "</div>" + "<div id='tooltipShadow'>"+text+"</div>");
                if($.browser.msie) var tipWidth = $("#simpleTooltip,#tooltipShadow").outerWidth(true)
                else var tipWidth = $("#simpleTooltip,#tooltipShadow").width()
                $("#simpleTooltip,#tooltipShadow").width(tipWidth);
                $("#simpleTooltip").css("left", tipX).css("top", tipY).fadeIn("medium");
                $("#tooltipShadow").css("left", tipX+2).css("top", tipY+2).fadeIn("medium");
            }, function(){
                $("#simpleTooltip,#tooltipShadow").remove();
                $(this).attr("title", text);
            });
            $(this).mousemove(function(e){
                var tipX = e.pageX + 12;
                var tipY = e.pageY + 12;
                var tipWidth = $("#simpleTooltip,#tooltipShadow").outerWidth(true);
                var tipHeight = $("#simpleTooltip,#tooltipShadow").outerHeight(true);
                if(tipX + tipWidth > $(window).scrollLeft() + $(window).width()) tipX = e.pageX - tipWidth;
                if($(window).height()+$(window).scrollTop() < tipY + tipHeight) tipY = e.pageY - tipHeight;
                $("#simpleTooltip").css("left", tipX).css("top", tipY).fadeIn("medium");
                $("#tooltipShadow").css("left", tipX+2).css("top", tipY+2).fadeIn("medium");
            });
        }
    });
}})(jQuery);