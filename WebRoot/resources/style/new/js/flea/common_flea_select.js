/* 多级选择相关函数，如地区选择，分类选择
 * common_select
 */

/* 地区选择函数 */
function regionInit(divId)
{
    $("#" + divId + " > select").change(regionChange); // select的onchange事件
    $("#" + divId + " > input:button[class='edit_region']").click(regionEdit); // 编辑按钮的onclick事件
}

function regionChange()
{
    // 删除后面的select
    $(this).nextAll("select").remove();

    // 计算当前选中到id和拼起来的name
    var selects = $(this).siblings("select").andSelf();
    //var id = 0;
    var id = '';
    var names = new Array();
    for (i = 0; i < selects.length; i++)
    {
        sel = selects[i];
        if (sel.value > 0)
        {
            id = sel.value;
            name = sel.options[sel.selectedIndex].text;
            names.push(name);
        }
    }
    $(".area_ids").val(id);
    $(".area_name").val(name);
    $(".area_names").val(names.join("\t"));

    // ajax请求下级地区
    if (this.value > 0)
    {
        var _self = this;
        var url = SITE_URL + '/index.php?act=index&op=flea_area';
        $.getJSON(url, {'area_id':this.value}, function(data){
            if (data)
            {
                if (data.length > 0)
                {
                    $("<select><option>请选择</option></select>").change(regionChange).insertAfter(_self);
                    var data  = data;
                    for (i = 0; i < data.length; i++)
                    {
                        $(_self).next("select").append("<option value='" + data[i].flea_area_id + "'>" + data[i].flea_area_name + "</option>");
                    }
                }
            }
            else
            {
                alert(data.msg);
            }
        });
    }
}

function regionEdit()
{
    $(this).siblings("select").show();
    $(this).siblings("span").andSelf().hide();
}

/* 商品分类选择函数 */
function gcategoryInit(divId)
{
    $("#" + divId + " > select").get(0).onchange = gcategoryChange; // select的onchange事件
    window.onerror = function(){return true;}; //屏蔽jquery报错
    $("#" + divId + " .edit_gcategory").click(gcategoryEdit); // 编辑按钮的onclick事件
}

function gcategoryChange()
{
    // 删除后面的select
    $(this).nextAll("select").remove();

    // 计算当前选中到id和拼起来的name
    var selects = $(this).siblings("select").andSelf();
    var id = 0;
    var names = new Array();
    for (i = 0; i < selects.length; i++)
    {
        sel = selects[i];
        if (sel.value > 0)
        {
            id = sel.value;
            name = sel.options[sel.selectedIndex].text;
            names.push(name);
        }
    }
    $(".mls_id").val(id);
    $(".mls_name").val(name);
    $(".mls_names").val(names.join("\t"));

    // ajax请求下级分类
    if (this.value > 0)
    {
        var _self = this;
        var url = SITE_URL + '/index.php?act=index&op=josn_flea_class';
        $.getJSON(url, {'gc_id':this.value}, function(data){
            if (data)
            {
                if (data.length > 0)
                {
                    $("<select><option>请选择</option></select>").change(gcategoryChange).insertAfter(_self);
                    var data  = data;
                    for (i = 0; i < data.length; i++)
                    {
                        $(_self).next("select").append("<option value='" + data[i].gc_id + "'>" + data[i].gc_name + "</option>");
                    }
                }
            }
            else
            {
                alert(data.msg);
            }
        });
    }
}

function gcategoryEdit()
{
    $(this).siblings("select").show();
    $(this).siblings("span").andSelf().remove();
}
