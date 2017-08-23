$(document).ready(function(){	
			$("#slider").easySlider({
				auto: false, 
				continuous: true
			});
		});
		$(function(){
			var dex=Math.ceil($("#cat_num").text()/5)*30;
			if(dex>60){
				$(".cat_list").css("height","60px");
			}
			$("#j_moreCat").toggle(function(){
					$(".cat_list").css("height",dex+"px");
				},function(){
					$(".cat_list").css("height","60px");
				});
		});
		
		//检索
		$(function(){
			/*	分类检索	*/
			retrieval("cate",false);
			/*	品质检索	*/
			retrieval("quality",false);
			/*	地区检索	*/
			retrieval("area",false);
			/*	价格检索	*/
			retrieval("price",true);
			/*	价格搜索	*/
			$("#price_submit").click(function(){
				search("start",true);
				search("end",false);
				$("#condition").submit();
			});
			/*	商品名搜索	*/
			$("#search_key").click(function(){
				search("key");
				$("#condition").submit();
			});
			
			
			//有无图片检索
			$("#picc").click(function(){
				//如果选中
				if($(this).val()){
					$(this).attr("keypic",1);
				}
				comb("pic",this);
				$("#condition").submit();
			});
			
			//默认排序
			$("#rank_current").click(function(){
				$("#rank_input").remove();
				$("#condition").submit();
			});
			//价格排序
			$("#rank_price").click(function(){
				if($(this).attr("keyrank")==1){
					$(this).attr("keyrank",2);
				}else{
					$(this).attr("keyrank",1);
				}
				comb("rank",this);
				$("#condition").submit();
			});
			
			//共享函数：多种选一种检索
			function retrieval(arg,sign){
				$("#"+arg).children("li").children("a").click(function(){
					comb(arg,this);
					//执行当前检索的附加函数 rl+参数名
					if(arg&&sign){
						var fc=eval("rl"+arg);
						fc();
					}
					$("#condition").submit();
				});
				
				
			}
			//检索附加函数:	选择搜索范围时删除搜索价格
			function rlprice(){
				if($("#start_input").val())
					$("#start_input").remove();
				if($("#end_input").val())
					$("#end_input").remove();
			}
			//共享函数：搜索
			function search(arg,sign){
				$("#"+arg).attr("key"+arg,$("#"+arg).val());
				comb(arg,$("#"+arg));
				if(arg&&sign){
					var fc=eval("sc"+arg);
					fc();
				}
			}
			//搜索附加函数:主动搜索价格范围时删除价格区域选择
			function scstart(){
				if($("#price_input").val())
					$("#price_input").remove();
			}
			//共享函数：创建/删除表单、表单赋值、提交表单
			function comb(param,mark){
				//如果当前属于未选中状态并且进行选择操作则添加input
				if(!$("#"+param+"_input").val()&&$(mark).attr("key"+param)){
					$("#condition").append("<input type='hidden' id='"+param+"_input' name='"+param+"_input' value='' />");
				}
				//如果当前属于选中状态并且进行选择不限操作则删除input
				else if(!$(mark).attr("key"+param)&&$("#"+param+"_input").val()){
					$("#"+param+"_input").remove();
				}
				
				$("#"+param+"_input").val($(mark).attr("key"+param));
			}
		});
		/*	状态	*/
		$(function(){
			// /*	分类	*/
			var cate=$("#cate_input").val();
			$("a[keycate="+cate+"]").parent().addClass("current");
			/*	品质选择	*/
			var quality=$("#quality_input").val();
			$("a[keyquality="+quality+"]").parent().addClass("current");
			/*	地区选择	*/
			var area=$("#area_input").val();
			$("a[keyarea="+area+"]").parent().addClass("current");
			/*	时间选择	*/
			var price=$("#price_input").val();
			$("a[keyprice="+price+"]").parent().addClass("current");
		});
		/* 点击价格输入框弹出 */
		$(function(){
			$("#float_price").click(function(){
				$("#price_form").show();
			});
			$("#price_form").hover(function(){
				$("#price_form").show();
			},function(){
				$("#price_form").hide();
			});
		});