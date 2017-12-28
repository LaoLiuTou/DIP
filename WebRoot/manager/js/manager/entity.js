//接收参数
var jumpIndex=GetQueryString("index");
var eIndex=GetQueryString("eindex");
//选中的接口id
var interfaceIndex;
var interfaceData;
//选中的数据源id
var datasourceIndex;
var datasourceData;
//选中的实体id
var entityIndex;
var entityData;
//选中的实体column
var columnIndex;
var columnData;
//选中的id
$(document).ready(function() {
    //切换参数和关系
    $('#relationDiv').hide();
    $('#paramForm').hide();
    $('#save').attr('data-target', '');
    $('#delete').attr('data-target', '');
    ////初始化接口数据
    //querySys_interfaces();
    ///初始化数据源数据
    querySys_datasources();

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////接口操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    //接口列表点击
    $('#interfaceDiv > a').click(function(){

        interfaceIndex=$(this).attr('index');
        window.location.href="interface.html?index="+interfaceIndex;

    });

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////实体操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    //实体列表点击
    $('#entityUl').on('click','li',function(){
        entityIndex=$(this).attr('index');
        querySys_entities_col();
        $('#relationDiv').show();
        $('#paramForm').hide();
        $('#save').attr('data-target', '');
        $('#delete').attr('data-target', '')
        $('#e_title').text('实体('+entityData[entityIndex].nm_t+')');
    });

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////实体列操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    //缓存修改的值
    var ovalue='';
    $('#columnTbody').on('click','tr',function(){

        $('#columnTbody > tr').attr('class','');
        $(this).attr('class','info');
        columnIndex=$(this).attr('index');

        $('#relationDiv').hide();
        $('#paramForm').show();
        $('#save').attr('data-target', '#entity-update');
        $('#delete').attr('data-target', '#config-delete-x');

        //默认值
        //$('input[name="rd"]:checked').val();
        $('#column_name').val(columnData[columnIndex].column_name);
        $('#column_comment').val(columnData[columnIndex].column_comment);
        var col_type=columnData[columnIndex].column_type;
        $('#column_type').val(col_type.split('(')[0]);
        $('#column_length').val(col_type.split('(')[1].substr(0,col_type.split('(')[1].length-1));
        $('#column_default').val(columnData[columnIndex].column_default);

        $('#is_nullable_n').prop("checked",true);
        $('#column_key_n').prop("checked",'checked');
        $('#extra_n').prop("checked",'checked');

        if(columnData[columnIndex].is_nullable=='NO'){
            $('#is_nullable_y').prop("checked",'checked');
        }
        if(columnData[columnIndex].column_key=='PRI'){
            $('#column_key_y').prop("checked",'checked');
        }
        if(columnData[columnIndex].extra=='auto_increment'){
            $('#extra_y').prop("checked",'checked');
        }

        var userinfo = JSON.parse(sessionStorage.getItem('sys_user'));
        $('#creater').val(userinfo[entityData[entityIndex].id]);

        ovalue=$('#column_name').val()+$('#column_comment').val()+$('#column_length').val()+
            $('#column_type').val()+$('#column_default').val()+$('input[name="is_nullable"]:checked').val()+
            $('input[name="column_key"]:checked').val()+$('input[name="extra"]:checked').val();

    });

    $('input[name="extra"]').change(function(){
        if($('input[name="extra"]:checked').val()=='Y'){
            if($('#column_type').val()!='smallint'&&$('#column_type').val()!='int'&&
                $('#column_type').val()!='bigint'&&$('#column_type').val()!='float'&&$('#column_type').val()!='double'){
                alert('属性字段类型不允许自增！');
                $('#extra_n').prop("checked",'checked');

            }
        }

    });
    $('input[name="add_extra"]').change(function(){
        if($('input[name="add_extra"]:checked').val()=='Y'){
            if($('#add_column_type').val()!='smallint'&&$('#add_column_type').val()!='int'&&
                $('#add_column_type').val()!='bigint'&&$('#add_column_type').val()!='float'&&$('#add_column_type').val()!='double'){
                alert('属性字段类型不允许自增！');
                $('#add_extra_n').prop("checked",'checked');

            }
        }

    });
    //修改
    $('#saveBtn').click(function(){

        if($('#column_name').val()==''){
            alert('属性名不能为空！');
            return false;
        }
        var cvalue=$('#column_name').val()+$('#column_comment').val()+$('#column_length').val()+
            $('#column_type').val()+$('#column_default').val()+$('input[name="is_nullable"]:checked').val()+
            $('input[name="column_key"]:checked').val()+$('input[name="extra"]:checked').val();

        if(ovalue==cvalue){
            alert('您没有做任何修改！')
        }
        else{
            var sql='alter table '+entityData[entityIndex].nm_t+' change  column '+columnData[columnIndex].column_name+' '+$('#column_name').val()+
                ' '+$('#column_type').val()+'('+$('#column_length').val()+') ';
            if($('input[name="is_nullable"]:checked').val()=='Y'){
                sql+=' not null ';
            }
            if($('#column_default').val()!=''){
                sql+=' DEFAULT '+$('#column_default').val()+' ';
            }
            if($('#column_comment').val()!=''){
                sql+=' COMMENT \''+$('#column_default').val()+'\' ';
            }
            if($('input[name="extra"]:checked').val()=='Y'){
                sql+=' auto_increment ';
            }
            executeSql(datasourceData[datasourceIndex].id,'alter',sql,'entity.html?index='+datasourceIndex+'&eindex='+entityIndex);
        }

    });

    //新建
    $('#columnAddBtn').click(function(){
        if($('#add_column_name').val()==''){
            alert('属性名不能为空！');
            return false;
        }
        var sql='alter table '+entityData[entityIndex].nm_t+' add  column '+$('#add_column_name').val()+
            ' '+$('#add_column_type').val()+'('+$('#add_column_length').val()+') ';
        if($('input[name="add_is_nullable"]:checked').val()=='Y'){
            sql+=' not null ';
        }
        if($('#add_column_default').val()!=''){
            sql+=' DEFAULT '+$('#add_column_default').val()+' ';
        }
        if($('#add_column_comment').val()!=''){
            sql+=' COMMENT \''+$('#add_column_comment').val()+'\' ';
        }
        if($('input[name="add_extra"]:checked').val()=='Y'){
            sql+=' auto_increment ';
        }
        executeSql(datasourceData[datasourceIndex].id,'alter',sql,'entity.html?index='+datasourceIndex+'&eindex='+entityIndex);
    });
    //删除
    $('#deleteColBtn').click(function(){
        var sql='alter table '+entityData[entityIndex].nm_t+' drop   column '+columnData[columnIndex].column_name+' ';
        executeSql(datasourceData[datasourceIndex].id,'alter',sql,'entity.html?index='+datasourceIndex+'&eindex='+entityIndex);
    });

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////数据源操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //数据源列表点击
    $('#datasourceDiv > a').click(function(){
        datasourceIndex=$(this).attr('index');
        window.location.href="datasource.html?index="+datasourceIndex;

    });

    //选择的数据源
    if(jumpIndex!=null){
        datasourceIndex=jumpIndex;
        $('#datasourceDiv > a').attr('class','list-group-item');
        $('#datasourceDiv > a').eq(jumpIndex).attr('class','list-group-item active');
        //初始化实体数据
        querySys_entities();
    }
    else{
        datasourceIndex=0;
        $('#datasourceDiv > a').attr('class','list-group-item');
        $('#datasourceDiv > a').eq(0).attr('class','list-group-item active');
        //初始化实体数据
        querySys_entities();

    }
    //选择的实体
    if(eIndex!=null){
        $('#entityUl > li').eq(eIndex).click();
    }
    else{
        $('#entityUl > li').eq(0).click();
    }


});
