$(function(){
    // 添加店铺分类
    $("#add_sgcategory").click(function(){
        $(".sgcategory:last").after($(".sgcategory:last").clone().val(0));
    });

    // 商品分类
    gcategoryInit("gcategory");
});