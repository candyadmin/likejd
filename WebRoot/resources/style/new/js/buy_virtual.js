/**
 * 更改数量
 */
function change_quantity(input){
	var value = Number($(input).val());
	var maxvalue = Number($(input).attr('maxvalue'));
	if (value > maxvalue) {
		$(input).val($(input).attr('maxvalue'));
	}else if (value <= 0) {
		$(input).val(1);
	}

	calc_price();
}

/**
 * 减少商品数量
 */
function decrease_quantity(){
	var input = $('#quantity');
    var orig = Number(input.val());
    if(orig > 1){
    	input.val(orig - 1);
    	input.keyup();
    }
    calc_price();
}

/**
 * 增加商品数量
 */
function add_quantity(){
	var input = $('#quantity');
    var orig = Number(input.val());
    input.val(orig + 1);
    input.keyup();
    calc_price();
}

/**
 * 价格计算
 */
function calc_price() {
	var input = $('#quantity');
	var total = $(input).val()*$(input).attr('price');
	$('#item_subtotal').html(number_format(total,2));
	$('#cartTotal').html(number_format(total,2));
}