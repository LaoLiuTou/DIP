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
//选中的实体关系rel
var relIndex;
var relData;
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
        var c_e_id=entityData[entityIndex].id;
        //alert(c_e_id);
        relation_querySys_entities(c_e_id);
        querySys_relations(c_e_id);
    });

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////实体关系///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //实体关系切换
    $('#r_e_select').change(function(){
        relation_querySys_entities_col($(this).find('option:selected').text(),null);
        $('#r_t_name').html($('#r_e_select').html());
        $('#r_t_name option').each(function(){
            if($('#r_e_select').val()!=''&&$(this).text()==$('#r_e_select').find('option:selected').text()){
                $(this).remove();
            }
        });

    });

    $('#rel_Setting_Btn').click(function(){
        $('#rel_addBtn').show();
        $('#rel_SaveBtn').hide();
        $('#r_tbody>tr').attr('class','');
        $('#r_tbody button').each(function(){
            if($(this).attr('title')=='保存'){
                $(this).attr('data-target','');
                $(this).attr('class','glyphicon glyphicon-floppy-disk fr  titlebox-btn5');
            }

        });

        var c_e_id=entityData[entityIndex].id;
        querySys_entities_col();
        relation_querySys_entities(c_e_id);


    });
    //
    $('#r_tbody').on('click','tr',function(){
        $('#r_tbody>tr').attr('class','');
        $(this).attr('class','info');
        relIndex=$(this).attr('index');
        $('#rel_addBtn').show();
        $('#rel_SaveBtn').hide();

        var rel_json=JSON.parse(relData[$(this).attr('index')].relation_json);
        $('#r_tbody button').each(function(){
            if($(this).attr('title')=='保存'){
                if($(this).attr('index')==relIndex){
                    $(this).attr('id','rel_UpdateBtn');
                    $(this).attr('class','glyphicon glyphicon-floppy-disk fr  titlebox-btn4');
                }
                else{
                    $(this).attr('id','');
                    $(this).attr('class','glyphicon glyphicon-floppy-disk fr  titlebox-btn5');
                }

            }

        });
        $('#r_c_li>li').each(function(){
            if($(this).attr('column_name')==rel_json[entityData[entityIndex].id]){
                $(this).click();
            }
        });
        if(entityData[entityIndex].id==relData[$(this).attr('index')].ent1_id){
            $('#r_e_select').val(relData[$(this).attr('index')].ent2_id);

            $('#select_rel').val(rel_json['rel']);
        }
        else{
            $('#r_e_select').val(relData[$(this).attr('index')].ent1_id);
            $('#select_rel').val(rel_json['rel'].split('').reverse().join(''));

        }

        //中间表
        $('#r_t_name').val(rel_json['rel_table']);
        //外键
        if(rel_json['fk']=='n'){
            $('#f_k_cb').prop('checked',false);
        }
        else{
            $('#f_k_cb').prop('checked',true);
        }
        if(relData[$(this).attr('index')].ent1_id==entityData[entityIndex].id){

            relation_querySys_entities_col(getEntityNameById(relData[$(this).attr('index')].ent2_id),rel_json[relData[$(this).attr('index')].ent2_id]);
        }
        else{
            relation_querySys_entities_col(getEntityNameById(relData[$(this).attr('index')].ent1_id),rel_json[relData[$(this).attr('index')].ent1_id]);

        }

        $('#select_rel').trigger("change");



    });
    $('#rel_addBtn').click(function(){
        $('#r_tbody>tr').attr('class','');
        $('#r_tbody button').each(function(){
            if($(this).attr('title')=='保存'){
                $(this).attr('data-target','');
                $(this).attr('class','glyphicon glyphicon-floppy-disk fr  titlebox-btn5');
            }

        });

        $('#rel_addBtn').hide();
        $('#rel_SaveBtn').show();
        var c_e_id=entityData[entityIndex].id;
        querySys_entities_col();
        relation_querySys_entities(c_e_id);

    });

    $('#r_c_li').on('click','li',function(){
        $('#r_c_li').find('a').css('color','#119cfa');
        $(this).find('a').css('color','#23527c');
        $('#select_col').val(columnData[$(this).attr('index')].column_name);
    });
    $('#select_rel').change(function(){

        if($('#select_rel').val()=="n:n"){
            $('#r_t_dl').show();
            //$('#r_t_name').val(entityData[entityIndex].nm_t+'_'+$('#r_e_select').find('option:selected').text()+"_rel");
            //$('#f_k_label').hide();
        }
        else if($('#select_rel').val()=="1:n"||$('#select_rel').val()=="1:1"){
            $('#r_t_dl').hide();
            $('#r_t_name').val('');
            //$('#f_k_label').show();
        }
        else{
            $('#r_t_dl').hide();
            $('#r_t_name').val('');
            //$('#f_k_label').hide();
        }
    });
    $('#rel_SaveBtn').click(function(){
        if($('#select_col').val()==''){
            alert('请选择当前实体属性');
            return false;
        }
        if($('#r_e_select').val()==''){
            alert('请选择其他实体');
            return false;
        }
        var fkFlag='n';
        if ($('#f_k_cb').is(':checked')) {
            fkFlag='y';
        }

        /*var relJson='{"'+entityData[entityIndex].id+'":"'+$('#select_col').val()+'", "'+$('#r_e_select').val()+'":"'+
            $('#r_c_select').find('option:selected').text()+'", "fk":"'+fkFlag+'", "rel":"'+$('#select_rel').val()+'", "rel_table":"'+$('#r_t_name').val()+'"}';
        var relJsonReverse='{"'+$('#r_e_select').val()+'":"'+$('#r_c_select').find('option:selected').text()+'", "'+
            entityData[entityIndex].id+'":"'+$('#select_col').val()+'", "fk":"'+fkFlag+'", "rel":"'+$('#select_rel').val().split('').reverse().join('')+'", "rel_table":"'+$('#r_t_name').val()+'"}';
        var relJson2='{"'+$('#r_e_select').val()+'":"'+$('#r_c_select').find('option:selected').text()+'", "'+entityData[entityIndex].id+'":"'+$('#select_col').val()+'", "fk":"'+fkFlag+'", "rel":"'+$('#select_rel').val()+'", "rel_table":"'+$('#r_t_name').val()+'"}';
        var relJson2Reverse='{"'+entityData[entityIndex].id+'":"'+$('#select_col').val()+'","'+$('#r_e_select').val()+'":"'+$('#r_c_select').find('option:selected').text()+'",  "fk":"'+fkFlag+'", "rel":"'+$('#select_rel').val().split('').reverse().join('')+'", "rel_table":"'+$('#r_t_name').val()+'"}';
        */
        var createFlag=true;
        for(var o in relData){

            var r_json=JSON.parse(relData[o].relation_json);

            if(entityData[entityIndex].id==relData[o].ent1_id&&$('#r_e_select').val()==relData[o].ent2_id&&
                r_json[entityData[entityIndex].id]==$('#select_col').val()&&r_json[$('#r_e_select').val()]==$('#r_c_select').find('option:selected').text()){
                createFlag=false;
            }
            else if(entityData[entityIndex].id==relData[o].ent2_id&&$('#r_e_select').val()==relData[o].ent1_id&&
                r_json[entityData[entityIndex].id]==$('#select_col').val()&&r_json[$('#r_e_select').val()]==$('#r_c_select').find('option:selected').text()){
                createFlag=false;
            }
            else{
            }
            /*
            if(entityData[entityIndex].id==relData[o].ent1_id&&$('#r_e_select').val()==relData[o].ent2_id&&relData[o].relation_json.replace(/\s+/g,"")==relJson.replace(/\s+/g,"")){
                createFlag=false;
            }
            else if(entityData[entityIndex].id==relData[o].ent1_id&&$('#r_e_select').val()==relData[o].ent2_id&&relData[o].relation_json.replace(/\s+/g,"")==relJson2.replace(/\s+/g,"")){
                createFlag=false;
            }
            else if(entityData[entityIndex].id==relData[o].ent2_id&&$('#r_e_select').val()==relData[o].ent1_id&&relData[o].relation_json.replace(/\s+/g,"")==relJsonReverse.replace(/\s+/g,"")){
                createFlag=false;
            }
            else if(entityData[entityIndex].id==relData[o].ent2_id&&$('#r_e_select').val()==relData[o].ent1_id&&relData[o].relation_json.replace(/\s+/g,"")==relJson2Reverse.replace(/\s+/g,"")){
                createFlag=false;
            }
            else{
            }
            */

        }

        if(createFlag){
            addSys_relations(entityData[entityIndex].id,$('#select_col').val(),$('#r_e_select').val(),
               $('#r_c_select').find('option:selected').text(),$('#select_rel').val(),$('#r_t_name').val(),fkFlag);
        }
        else{
            alert("该关系已经存在，不能重复创建！");
        }



    });
    $('#r_tbody').on('click','#rel_UpdateBtn',function(e){
    //$('#rel_UpdateBtn').click(function(e){
        //取消冒泡
        e.cancelBubble = true;

        if($('#select_col').val()==''){
            alert('请选择当前实体属性');
            return false;
        }
        if($('#r_e_select').val()==''){
            alert('请选择其他实体');
            return false;
        }
        var fkFlag='n';
        if ($('#f_k_cb').is(':checked')) {
            fkFlag='y';
        }


        var createFlag=true;
        for(var o in relData){

            var r_json=JSON.parse(relData[o].relation_json);

            if(entityData[entityIndex].id==relData[o].ent1_id&&$('#r_e_select').val()==relData[o].ent2_id&&
                r_json[entityData[entityIndex].id]==$('#select_col').val()&&r_json[$('#r_e_select').val()]==$('#r_c_select').find('option:selected').text()){
                createFlag=false;
            }
            else if(entityData[entityIndex].id==relData[o].ent2_id&&$('#r_e_select').val()==relData[o].ent1_id&&
                r_json[entityData[entityIndex].id]==$('#select_col').val()&&r_json[$('#r_e_select').val()]==$('#r_c_select').find('option:selected').text()){
                createFlag=false;
            }
            else{
            }
        }

        if(createFlag){
            updateSys_relations(entityData[entityIndex].id,$('#select_col').val(),$('#r_e_select').val(),
                $('#r_c_select').find('option:selected').text(),$('#select_rel').val(),$('#r_t_name').val(),fkFlag,relData[relIndex].id);
        }
        else{
            alert("该关系已经存在，不能重复创建！");
        }



    });
    $('#rel_delBtn').click(function(){
        delSys_relations(relData[relIndex].id);

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
                sql+=' COMMENT \''+$('#column_comment').val()+'\' ';
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
