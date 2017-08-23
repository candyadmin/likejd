$(function(){

	$("#show_area").click(function(){
		$("#j_headercitylist").slideToggle();
	});
	$("#j_headercitylist").children("li").hover(function(){
		
		//清空二级地区
		$("#citylist").empty();
		//鼠标移入样式
		$(this).css({
			"background":"#89ccf0",
			"color":"#fff"
		});
		//获取当前子地区
		var id=$(this).attr("id")
		var data=$("#hidden_"+id).text();
		//转换成json对象
		var dataObj=eval("("+data+")");
		var html='';
		/* 判断是否有子类 */
			if(dataObj.length>0){
				$.each(dataObj,function(idv,item){
					html+="<li value="+item.flea_area_id+"><a onclick=\"areaGo("+item.flea_area_id+",'"+encodeURI(item.flea_area_name)+"');return false;\">"+item.flea_area_name+"</a></li>";
				});
				$("#citylist").append("<div id='citylb1'>◆</div><div id='citylb2'>◆</div><ul>"+html+"</ul>");
			}
		//输出子地区样式
		var local=$(this).position();
		var elementwidth=$(this).width();
		$("#citylist").show();
		$("#citylist").css({
			"top":local.top+30,
			"left":local.left+elementwidth+21
		});
		
	},function(){
		//鼠标移出样式
		mark=this;
		$(mark).css({
			"background":"#fff",
			"color":"#555"
		});
		$("#citylist").hide();
		
		//鼠标移入二级地区
		$("#citylist").hover(function(){
			$("#citylist").show();
			$(mark).css({
				"background":"#89ccf0",
				"color":"#fff"
			});
		},function(){
			$(mark).css({
				"background":"#fff",
				"color":"#555"
			});
			$("#citylist").hide();
			
		});
	});
});

function areaGo(id,name){
		$.cookie('flea_area',decodeURI(name));
		window.location.href='index.php?act=flea_class&area_input='+id;
}