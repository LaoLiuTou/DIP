//接收参数
var jumpIndex=GetQueryString("index");
//选中的接口id
var interfaceIndex;
var interfaceData;
//选中的数据源id
var datasourceIndex;
var datasourceData;
//选中的实体id
var entityIndex;
var entityData;
//选中的id
$(document).ready(function() {
    ////初始化接口数据
    //querySys_interfaces();
    ///初始化数据源数据
    querySys_datasources();

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////接口操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //接口参数数量变化
    $('#paramNum').change(function(){
        $('#paramDiv').html('');
        var paramNum=$('#paramNum').val();
        var html='';
        for(var i=0;i<paramNum;i++){
            html+='<div class="form-group inptext-out">\n' +
                '<input type="text" class="form-control inptext-box50 fl"  placeholder="参数1">\n' +
                '<input type="text" class="form-control inptext-box50 fl"  placeholder="/*参数说明*/">\n' +
                '</div>';
        }
        $('#paramDiv').append(html);
    });

    //接口参数数量变化
    $('#updateParamNum').change(function(){
        $('#updateParamDiv').html('');
        var paramNum=$('#updateParamNum').val();
        var html='';
        for(var i=0;i<paramNum;i++){
            html+='<div class="form-group inptext-out">\n' +
                '<input type="text" class="form-control inptext-box50 fl"  placeholder="参数1">\n' +
                '<input type="text" class="form-control inptext-box50 fl"  placeholder="/*参数说明*/">\n' +
                '</div>';
        }
        $('#updateParamDiv').append(html);
    });
    //接口新建按钮
    $('#addBtn').click(function(){
        var param= '{';
        if($('#nm_t').val()==''){
            alert('接口名称不能为空！');
            return false;
        }
        $('#paramDiv div').each(function(){
            param+='"'+$(this).find('input').eq(0).val()+'":"'+$(this).find('input').eq(1).val()+'",';
        });
        var info='';
        if(param.length>2){
            param=param.substr(0,param.length-1)+'}';
            info='{"param":'+param+',"des":"'+$('#des').val()+'"}';
        }
        else{
            info='{"param":"","des":"'+$('#des').val()+'"}';
        }
        addSys_interfaces(JSON.stringify(info));
    });
    //接口删除
    $('#delBtn').click(function(){
        var currentInterface=interfaceData[interfaceIndex];
        delSys_interfaces(currentInterface.id);
    });
    //接口列表点击
    $('#interfaceDiv > a').click(function(){
        $('#interfaceDiv > a').attr('class','list-group-item');
        $(this).attr('class','list-group-item active');
        interfaceIndex=$(this).attr('index');
        var currentInterface=interfaceData[interfaceIndex];
        $('#projectName').text(currentInterface.type);
        $('#interfaceName').text(currentInterface.nm_t);
        var html='';
        var info=JSON.parse(currentInterface.interface_json);
        var params=info['param'];
        var des=info['des'];
        //获取参数数量 并触发change
        var index=0;
        for(var p in params){
            index++;
        }
        $('#updateParamNum').val(index);
        $('#updateParamNum').trigger('change');
        index=0;
        for(var p in params){
            html+='<dd>'+p+'<span class="text-warning ml20">'+params[p]+'</span></dd>';
            $('#updateParamDiv div').eq(index).find('input').eq(0).val(p);
            $('#updateParamDiv div').eq(index).find('input').eq(1).val(params[p]);
            index++;
        }
        //显示默认值
        $('#interfaceParam').html('<dt>接口参数：</dt><dd>'+index+'个参数</dd>'+html);
        $('#interfaceDes').text(des);
        //修改默认值
        $('#updateNm_t').val(currentInterface.nm_t);
        $('#updateDes').val(des);
        //参数设置
        $('#if_nm_t').val(currentInterface.nm_t);
        $('#if_status').val(currentInterface.status);
        $('#if_c_dt').val(currentInterface.c_dt);
        var sys_user=sessionStorage.getItem('sys_user');
        if(sys_user!=null){
            $('#if_mem_id').val(JSON.parse(sys_user)[currentInterface.mem_id]);
        }
        //title
        //$('#interfaceTitle').text('项目('+currentInterface.type+')');
        $('#interfaceTitle').text('接口('+currentInterface.nm_t+')');
    });

    //接口修改按钮
    $('#updateBtn').click(function(){
        var param= '{';
        if($('#updateNm_t').val()==''){
            alert('接口名称不能为空！');
            return false;
        }
        $('#updateParamDiv div').each(function(){
            param+='"'+$(this).find('input').eq(0).val()+'":"'+$(this).find('input').eq(1).val()+'",';
        });
        param=param.substr(0,param.length-1)+'}';
        var info='{"param":'+param+',"des":"'+$('#updateDes').val()+'"}';
        var currentInterface=interfaceData[interfaceIndex];
        updateSys_interfaces(JSON.stringify(info),currentInterface.id);
    });

    $('#saveBtn').click(function(){
        var currentInterface=interfaceData[interfaceIndex];
        updateSys_interfacesStatus(currentInterface.id,$('#if_status').val());
    });
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////数据源操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //数据源列表点击
    $('#datasourceDiv > a').click(function(){
        datasourceIndex=$(this).attr('index');
        window.location.href="datasource.html?index="+datasourceIndex;
    });

    if(jumpIndex!=null){
        $('#interfaceDiv > a').eq(jumpIndex).click();
    }
    else{
        //默认点击第一个接口
        $('#interfaceDiv > a').eq(0).click();
        //默认点击第一个数据源
        //$('#datasourceDiv > a').eq(0).click();
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////实体操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

    //数据源列表点击
    $('#entityUl > li').click(function(){
        entityIndex=$(this).attr('index');
        window.location.href="entity.html?index="+datasourceIndex+'&eindex='+entityIndex;
    });


});
