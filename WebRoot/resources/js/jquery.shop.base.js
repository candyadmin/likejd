var pagestyle=function(){    
   jQuery("#workspace .left").css("width","10%")
   jQuery("#workspace .content").css({"width":"90%","height":jQuery(window).height()});
   try{ 
        var iframe = jQuery("#main_workspace");
		var h = jQuery(window).height() - iframe.offset().top;
		var w = jQuery(window).width() - iframe.offset().left;
		var cw=jQuery("#workspace .content").css("width").replace("px","");
		if(h < 300) h = 300;
		if(w >= cw) w = cw-3;
		iframe.height(h);
		iframe.width(w);
		jQuery(".statement").css("top",jQuery(window).height()-60);
     }catch (ex){} 
	
}; 
//
String.prototype.replaceAll  = function(s1,s2){   
   return this.replace(new RegExp(s1,"gm"),s2);   
}  
function openURL(args){
 var type=arguments[0];
 var item_id="";
 var url="";
 if(type=="show"){
  item_id=arguments[1];	 
 }
 if(type=="url"){
   url=arguments[1];
 }
 var content_id=arguments[2];
 var url_id=arguments[3];
 var parent_id=arguments[4];
 //item_id不为空，显示数据
 if(type=="show"){
   jQuery(".ulleft").hide();
   jQuery("#"+item_id).show();
   var current_css=jQuery("#"+item_id+"_menu").attr("class");
    //修改样式
   jQuery(".nav a").removeClass("this");
   jQuery(".nav a").removeClass("home");
   jQuery("#"+item_id+"_menu").addClass("this"); 
   //打开默认的第一个url
   var first_li=jQuery("#"+item_id+" li").first();
   var first_a=jQuery("#"+item_id+" a").first();
   jQuery(".ulleft a").removeClass("this");
   first_a.addClass("this");
   jQuery("#top_nav_info").html(first_a.html());
   var html=first_li.html().match(/openURL\('(.*)'\)/ig);
   if(html!=null){
   var arg = html[0].split(",");
    jQuery("#main_workspace",parent.document.body).attr("src",arg[1].replaceAll("'",""));
   }else{
	   
   }
 }
 //url不为空加载请求数据
 if(type=="url"){
  if(parent_id!=undefined){
   jQuery(".nav a").removeClass("this");
   jQuery(".nav a").removeClass("home");
   jQuery("#"+parent_id+"_menu").addClass("this"); 	 
   jQuery(".ulleft").hide();
   jQuery("#"+parent_id).show();
   jQuery(".webmap_box").fadeOut('normal');
  }
   jQuery(".ulleft a").removeClass("this");
   jQuery("#"+url_id).addClass("this");
   if(url!=undefined){
     jQuery("#"+content_id,parent.document.body).attr("src",url) 
   }
   jQuery("#top_nav_info").html(jQuery("#"+url_id).html());
 }
 //
 pagestyle();
}

//默认执行的初始化
jQuery(document).ready(function(){
  document.body.parentNode.style.overflow="hidden";	
  jQuery(window).resize(pagestyle);
});