//**************************************************************
// jQZoom allows you to realize a small magnifier window,close
// to the image or images on your web page easily.
//
// jqZoom version 2.1
// Author Doc. Ing. Renzi Marco(www.mind-projects.it)
// First Release on Dec 05 2007
// i'm searching for a job,pick me up!!!
// mail: renzi.mrc@gmail.com
//**************************************************************

(function($){

        $.fn.jqueryzoom = function(options){

        var settings = {
                xzoom: 200,        //zoomed width default width
                yzoom: 200,        //zoomed div default width
                offset: 10,        //zoomed div default offset
                position: "right" ,//zoomed div default position,offset position is to the right of the image
                lens:1, //zooming lens over the image,by default is 1;
                preload: 1,
                defaultwidth:600,
        		defaultheight:600
            };

            if(options) {
                $.extend(settings, options);
            }

            var noalt='';

            $(this).hover(function(){

            // remove parents obj within 'relative'
            var parents = $(this).parents();
            parents.each(function(){
                if($(this).css('position') == 'relative'){
                    $(this).css('position', 'static');
                    $(this).attr('nc_type', 'jqzoom_relative');
                }
            });

            var imageLeft = this.offsetLeft;
            var imageRight = this.offsetRight;
            var imageTop =  $(this).get(0).offsetTop;
            var imageWidth = $(this).children('img').get(0).offsetWidth;
            var imageHeight = $(this).children('img').get(0).offsetHeight;


            noalt= $(this).children("img").attr("alt");

            var bigimage = $(this).children("img").attr("jqimg");

            $(this).children("img").attr("alt",'');

            if(settings.position == "right"){

            if(imageLeft + imageWidth + settings.offset + settings.xzoom > screen.width){

            leftpos = imageLeft  - settings.offset - settings.xzoom;

            }else{

            leftpos = imageLeft + imageWidth + settings.offset;
            }
            }else{
            leftpos = imageLeft - settings.xzoom - settings.offset;
            if(leftpos < 0){

            leftpos = imageLeft + imageWidth  + settings.offset;

            }

            }
            
            $("div.zoomdiv").show();
            
            if(!settings.lens){
              $(this).css('cursor','crosshair');
            }




                   $(document.body).mousemove(function(e){

                	   $("div.zoomdiv").css({ top: imageTop,left: leftpos });

                       $("div.zoomdiv").width(settings.xzoom);

                       $("div.zoomdiv").height(settings.yzoom);	

                   mouse = new MouseEvent(e);

                   /*$("div.jqZoomPup").hide();*/


                    var bigwidth = $(".bigimg").get(0).offsetWidth;

                    var bigheight = $(".bigimg").get(0).offsetHeight;
                    if( bigwidth < settings.defaultwidth || bigheight < settings.defaultheight ){
                    	$("div.jqZoomPup").hide();
                    	$("div.zoomdiv").hide();
                    }else{
                    	$("div.jqZoomPup").show();
                    	$("div.zoomdiv").show();
                    }

                    var scaley ='x';

                    var scalex= 'y';


                    if(isNaN(scalex)|isNaN(scaley)){
                    var scalex = (bigwidth/imageWidth);

                    var scaley = (bigheight/imageHeight);


                    var pupx = scalex <= 1 ? imageWidth : (settings.xzoom)/scalex;
                    var pupy = scaley <= 1 ? imageHeight : (settings.yzoom)/scaley;

                    $("div.jqZoomPup").width(pupx);

                    $("div.jqZoomPup").height(pupy);

                    if(settings.lens){
                    $("div.jqZoomPup").css('visibility','visible');
                    }

                   }



                    xpos = mouse.x - $("div.jqZoomPup").width()/2 - imageLeft;

                    ypos = mouse.y - $("div.jqZoomPup").height()/2 - imageTop ;

                    if(settings.lens){

                    xpos = (mouse.x - $("div.jqZoomPup").width()/2 < imageLeft ) ? 0 : (mouse.x + $("div.jqZoomPup").width()/2 > imageWidth + imageLeft ) ?  (imageWidth -$("div.jqZoomPup").width() -2)  : xpos;

                    ypos = (mouse.y - $("div.jqZoomPup").height()/2 < imageTop ) ? 0 : (mouse.y + $("div.jqZoomPup").height()/2  > imageHeight + imageTop ) ?  (imageHeight - $("div.jqZoomPup").height() -2 ) : ypos;

                    }


                    if(settings.lens){

                    $("div.jqZoomPup").css({ top: ypos,left: xpos });

                    }



                    scrolly = ypos;

                    $("div.zoomdiv").get(0).scrollTop = scrolly * scaley;

                    scrollx = xpos;

                    $("div.zoomdiv").get(0).scrollLeft = (scrollx) * scalex ;


                    });
            },function(){

               // assign for parents obj within 'relative'
                var parents = $(this).parents();
                parents.each(function(){
                    if($(this).attr('nc_type') == 'jqzoom_relative'){
                        $(this).css('position', 'relative');
                    }
                });

               $(this).children("img").attr("alt",noalt);
               $(document.body).unbind("mousemove");
               if(settings.lens){
               $("div.jqZoomPup").hide();
               }
               $("div.zoomdiv").hide().css({top:'-9999em'});
               
            });

        count = 0;

        if(settings.preload){

        $('#content').append("<div style='display:none;' class='jqPreload"+count+"'></div>");

        $(this).each(function(){

        var imagetopreload= $(this).children("img").attr("jqimg");

        var content = jQuery('div.jqPreload'+count+'').html();

        jQuery('div.jqPreload'+count+'').html(content+'<img src=\"'+imagetopreload+'\">');

        });

        }

        }

})(jQuery);

function MouseEvent(e) {
this.x = e.pageX
this.y = e.pageY


}


