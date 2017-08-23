/* 行间编辑类 */
InlineEdit = function(editable_objects){
    this.init(editable_objects);
};

InlineEdit.prototype = {
    /* 行间编辑对象 */
    'object'    : null,

    'enabled'   : true,

    /* 提交方式 */
    'method'    : 'get',

    /* 提交网关 */
    'server'    : '',

    /* 列名 */
    'column'    : '',
    'idName'    : 'id',

    'hoverClass': null,
    'inputWidth'     : null,
    'inputClass' : null,
    'inputtype'	: null,

    /* 初始化 */
    'init'      : function(object){
        var _self = this;
        this.object = object;
        $(this.object).mouseover(function(){        // 鼠标移入
            if (!_self.enabled){return;}

            if (_self.hoverClass !== null)
            {
                $(this).addClass(_self.hoverClass);
            }
        }).mouseout(function(){                     // 鼠标移出
            if (!_self.enabled){return;}

            if (_self.hoverClass !== null)
            {
                $(this).removeClass(_self.hoverClass);
            }
        }).click(function(){                        // 点击时
        	if($(this).attr('inputtype') == 'textarea'){
        		_self.inputtype = 'textarea';
        	}
            if (!_self.enabled){return;}

            // 切换编辑模式
            _self.enableEdit(this);
        });
    },

    /* 启用 */
    'enable'    : function(){
        this.enabled = true;
    },

    /* 禁用 */
    'disable'   : function(){
        this.enabled = false;
    },

    /* 修改失败时 */
    'onFailed'  : function(rzt){
        alert(rzt.msg);
    },

    /* 提交前的验证，可通过return false阻止提交 */
    'onSubmit'  : function(v){
        return true;
    },

    /* 点击对象时触发的事件 */
    'enableEdit'   : function(editObject){
        var _self = this;
        if (_self.inputtype == 'textarea')
        {
        	/* 输入框 */
            var _input = $('<textarea></textarea>');
        }else{
        	/* 输入框 */
            var _input = $('<input type="text" class="text"/>');
        }
        
        var _old_data = $(editObject).html();
        if (_self.inputClass !== null)
        {
            _input.addClass(_self.inputClass);
        }
        if (_self.inputWidth !== null)
        {
            _input.css('width', _self.inputWidth);
        }
        _input.val(_old_data);
        var endEdit = function(){
            /* 结束编辑 */
            $(_input).remove();
            $(editObject).show();
        };
        _input.blur(function(){
            var _v = this.value;
            if (_old_data != _v)
            {
                if (_self.onSubmit(_v))
                {
                    var _qstring =  $(editObject).attr('querystring') ? '&' + $(editObject).attr('querystring') : '';
                    var _data = {};
                    _data.column = _self.column;
                    _data.value  = _v;
                    _data.ajax  = '';
                    if (_self.idName !== null)
                    {
                        _data[_self.idName] = $(editObject).attr('idvalue');
                    }
                    $.ajax({
                        'type' : _self.method,
                        'url'    : _self.server,
                        'data'   : _data,
                        'dataType' : 'json',
                        'success' : function(rzt){
                            if (rzt.done)
                            {
                                /* 修改成功 */
                                $(editObject).html(rzt.retval);
                            }
                            else
                            {
                                /* 修改失败，提示信息 */
                                _self.onFailed(rzt);
                            }
                            endEdit();
                        },
                        'error' : function(xhr, tS, eT){
                            alert(tS);
                            endEdit();
                        }
                    });
                }
                else
                {
                    endEdit();
                }
            }
            else
            {
                endEdit();
            }
        });

        /* 隐藏原标签 */
        $(editObject).hide();
        $(editObject).after(_input); // 显示输入框
        $(_input).select();  // 聚焦
    }
};

/* 状态切换类 */
ToggleEdit = function(objects){
    this.init(objects);
};
ToggleEdit.prototype = {
    'object'    : null,

    'enabled'   : true,

    /* 提交方式 */
    'method'    : 'get',

    /* 提交网关 */
    'server'    : '',

    /* 列名 */
    'column'    : '',
    'idName'    : 'id',

    'hoverClass': null,
    'toggleData' : {
        'on'    : 'toggle_on',      // value=1
        'off'   : 'toggle_off'     // value=0
    },
    /* 初始化 */
    'init'      : function(object){
        var _self = this;
        this.object = object;
        $(this.object).mouseover(function(){        // 鼠标移入
            if (!_self.enabled){return;}

            if (_self.hoverClass !== null)
            {
                $(this).addClass(_self.hoverClass);
            }
        }).mouseout(function(){                     // 鼠标移出
            if (!_self.enabled){return;}

            if (_self.hoverClass !== null)
            {
                $(this).removeClass(_self.hoverClass);
            }
        }).click(function(){                        // 点击时
            if (!_self.enabled){return;}

            // 切换
            _self.toggleStatus(this);
        });
    },

    /* 启用 */
    'enable'    : function(){
        this.enabled = true;
    },

    /* 禁用 */
    'disable'   : function(){
        this.enabled = false;
    },
    'toggleStatus' : function(toggleObject){
        var _self = this;
        if (!_self.onSubmit(toggleObject))
        {
            return;
        }
        var _data = this.getSetData($(toggleObject).attr('status'));
        var _qstring =  $(toggleObject).attr('querystring') ? '&' + $(toggleObject).attr('querystring') : '';
        var _idstring = _self.idName === null ? '' : '&' + _self.idName + '=' + $(toggleObject).attr('idvalue');
        $.ajax({
            'type' : _self.method,
            'url'  : _self.server,
            'data' : 'column=' + _self.column + '&value=' + _data.value + _idstring + _qstring,
            'dataType' : 'json',
            'success': function(rzt){
                if (typeof(rzt) != 'object')
                {
                    alert('result error:' + rzt);

                    return;
                }
                if (!rzt.done)
                {
                    _self.onFailed(rzt);

                    return;
                }
                _self.toggleClass(toggleObject);
                $(toggleObject).attr('status', _data.status);
            },
            'error' : function(xhr, tS, eT){
                alert(tS);
            }
        });
    },
    'getToggleData': function(){
        var _td = {'on':{'class':'toggle_on', 'value':1}, 'off':{'class':'toggle_off', 'value':0}};
        if (typeof(this.toggleData.on) == 'string')
        {
            _td.on['class'] = this.toggleData.on;
        }
        else
        {
            _td.on = this.toggleData.on;
        }
        if (typeof(this.toggleData.off) == 'string')
        {
            _td.off['class'] = this.toggleData.off;
        }
        else
        {
            _td.off = this.toggleData.off;
        }

        return _td;

    },
    'getSetData'    : function(current){
        var _td = this.getToggleData(), set;
        if (current == 'on')
        {
            set = _td.off;
            set.status = 'off';
        }
        else
        {
            set = _td.on;
            set.status = 'on';
        }

        return set;
    },
    'toggleClass'   : function(toggleObject){
        var _td = this.getToggleData();
        $(toggleObject).toggleClass(_td.on['class']);
        $(toggleObject).toggleClass(_td.off['class']);
    },
    'onFailed'      : function(rzt){
        alert(rzt.msg);
    },
    'onSubmit'      : function(toggleObject){
        return true;
    }
};

EditableTable = function(table){
    this.init(table);
};
EditableTable.prototype = {
    'table'     : null,
    'columns'   : {},
    'init'      : function(table){
        this.table = table;
        /* 初始化列信息 */
        this.columns = this.getColumnsInfo();
        for (k in this.columns)
        {
            /* 初始化可编辑列 */
            this.initColumn(this.columns[k]);
        }
    },
    'getColumnsInfo'    : function(){
        var _self = this, columns = {}, table_header, tds, all_col;
        table_header = $(_self.table).find('tr[nc_type="table_header"]');
        tds          = table_header.find('[column]');
        all_col = table_header.find('th, td');
        tds.each(function(){
            columns[$(this).attr('column')] = {
                'name'      : $(this).attr('column'),
                'index'     : all_col.index(this),
                'coltype'   : $(this).attr('coltype'),
                'expr'      : $(this).attr('expr') ? $(this).attr('expr') : '[nc_type="editobj"]',
                'checker'   : $(this).attr('checker') ? eval($(this).attr('checker')) : null,
                'editor'    : null,
                'dom'       : this,
                'colhover'  : $(this).attr('colhover') ? $(this).attr('colhover') : 'utils_default_colhover'
            };
        });

        return columns;
    },
    'initColumn'        : function(column_info){
        if (!column_info.coltype)
        {
            return;
        }
        this.columnEffect(column_info);
        this.columnEditable(column_info);
    },
    'columnEffect'      : function(column_info){
        var _col_items = this.getColItems(column_info);

        /* 设定主键值 */
        _col_items.each(function(){
            var _idvalue = $(this).parent().attr('idvalue');
            $(this).find(column_info.expr).attr('idvalue', _idvalue);
        });

        /* 效果 */
        _col_items.hover(function(){
            _col_items.addClass(column_info.colhover);
        }, function(){
            _col_items.removeClass(column_info.colhover);
        });
    },
    'columnEditable'    : function(column_info){
        var _col_items = this.getColItems(column_info);
        var _editor;
        var _edit_object = $(_col_items).find(column_info.expr).get();
        if (column_info.coltype == 'switchable')
        {
            var _on_class, _on_value, _off_class, _off_value;
            _on_class =  $(column_info.dom).attr('onclass') ?  $(column_info.dom).attr('onclass') : 'utils_default_toggle_on';
            _on_value =  $(column_info.dom).attr('onvalue') ?  $(column_info.dom).attr('onvalue') : 1;
            _off_class =  $(column_info.dom).attr('offclass') ?  $(column_info.dom).attr('offclass') : 'utils_default_toggle_off';
            _off_value =  $(column_info.dom).attr('offvalue') ?  $(column_info.dom).attr('offvalue') : 0;

            _editor = new ToggleEdit(_edit_object);
            _editor.toggleData = {
                'on' : {'class' : _on_class, 'value':_on_value},
                'off': {'class' : _off_class, 'value':_off_value}
            };
        }
        else if (column_info.coltype == 'editable')
        {
            var _input_width = $(column_info.dom).attr('inputwidth'), _input_class = $(column_info.dom).attr('inputclass');
            _editor = new InlineEdit(_edit_object);
            if (_input_width)
            {
                _editor.inputWidth = _input_width;
            }
            _editor.inputClass = _input_class ? _input_class : 'utils_default_input_class';
            if (column_info.checker)
            {
                _editor.onSubmit = column_info.checker;
            }
        }
        _editor.hoverClass = $(column_info.dom).attr('hoverclass') ? $(column_info.dom).attr('hoverclass') : null;
        _editor.server = $(this.table).attr('server');
        _editor.column = column_info.name;

        this.columns[column_info.name].editor = _editor;
    },
    'getColItems'       : function(column_info){
        var _self = this, cols, table_items, tds;
        /* 所有的表格项 */
        table_items = $(_self.table).find('tr[nc_type="table_item"]');
        /* 所有本列的项 */
        cols = table_items.find('td:eq(' + column_info.index + ')');

        return cols;
    }
};
