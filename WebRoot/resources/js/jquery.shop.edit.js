jQuery(document).ready(function(){ 
   jQuery(":input[type='text']").mouseover(function(e){
	    var top =e.pageY;
		var left = e.pageX; 
	   jQuery('body').append( '<p id="vtip" style="display:none;">可编辑</p>' );
		jQuery('p#vtip').css("top", top+"px").css("left", left+"px");
	    jQuery('p#vtip').bgiframe();
		jQuery("p#vtip").show();
	}).mouseout(function(){
		jQuery("p#vtip").remove();
	}).focusin(function(){
		jQuery("p#vtip").remove();
    });;						
});