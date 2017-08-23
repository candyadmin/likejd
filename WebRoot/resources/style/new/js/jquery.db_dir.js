$(document).ready(function(){
	//列表下拉
	$('img[nc_type="flex"]').click(function(){
		var status = $(this).attr('status');
		if(status == 'open'){
			var pr = $(this).parent('td').parent('tr');
			var id = $(this).attr('fieldid');
			var obj = $(this);
			$(this).attr('status','none');
			//ajax
			$.ajax({
				url: 'index.php?act=db&op=ajax&branch=db_file&dir_name='+id,
				dataType: 'json',
				success: function(data){
					if(data != 'false'){
						for(var i = 0; i < data.length; i++){
							var src='';
							var tmp_vertline = "<img class='preimg' src='templates/images/vertline.gif'/>";
							src += "<tr class='"+pr.attr('class')+" row"+id+"'>";
							src += "<td class='firstCell'></td>";
							//图片
							src += "<td width='350' align='left'>";
							src += tmp_vertline;
							src += "<img status='none' nc_type='flex' src='"+ADMIN_TEMPLATES_URL+"/images/tv-item.gif' />";
							//名称
							src += data[i].name;
							src += "</td>";
							//备份时间
							src += "<td>"+data[i].make_time+"</td>";
							//备份大小
							src += "<td>";
							src += data[i].size;
							src += "</td>";
							//卷数
							src += "<td>";
							src += "</td>";
							//操作
							//src += "<td class='handler'>";
							//src += "<span><a href='index.php?act=website&op=db_down&gc_id="+data[i].gc_id+"'>下载</a>";
							//src += "</td>";
							//src += "</tr>";
							//插入
							pr.after(src);
						}
						obj.attr('status','close');
						obj.attr('src',obj.attr('src').replace("tv-expandable","tv-collapsable"));
						$('img[nc_type="flex"]').unbind('click');
						//重现初始化页面
						$.getScript(RESOURCE_SITE_URL+"/js/jquery.edit.js");
						$.getScript(RESOURCE_SITE_URL+"/js/jquery.goods_class.js");	
					}
				},
				error: function(){
					alert('获取信息失败');
				}
			});
		}
		if(status == 'close'){
			$(".row"+$(this).attr('fieldid')).remove();
			$(this).attr('src',$(this).attr('src').replace("tv-collapsable","tv-expandable"));
			$(this).attr('status','open');
		}
	})
});