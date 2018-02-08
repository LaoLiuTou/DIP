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
    $('#dsPart').show();
    $('#entityPart').hide();

    ///初始化数据源数据
    querySys_datasources();

    $('#delete').attr('data-target', '#config-delete-n');
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
    $('#entityUl_show').on('click','li',function(){
        $('#entityUl_show > li').attr('class',' ');
        $(this).attr('class','active');
        entityIndex=$(this).attr('index');
        //window.location.href="entity.html?index="+datasourceIndex+'&eindex='+entityIndex;
        $('#delete').attr('data-target', '#config-delete-e');

        $('#en_nm_t').val(entityData[entityIndex].nm_t);
        $('#desc_t').val(entityData[entityIndex].desc_t);
        $('#en_status').val(entityData[entityIndex].status);
        $('#en_c_dt').val(entityData[entityIndex].c_dt);
        var sys_user=sessionStorage.getItem('sys_user');
        if(sys_user!=null){
            $('#en_mem_id').val(JSON.parse(sys_user)[entityData[entityIndex].mem_id]);
        }
        $('#save').attr('data-target', '#entity-update');

        $('#dsPart').hide();
        $('#entityPart').show();
    });


    //实体列表点击
    $('#entityUl').on('click','li',function(){
        entityIndex=$(this).attr('index');
        window.location.href="entity.html?index="+datasourceIndex+'&eindex='+entityIndex;
    });
    //删除实体
    $('#entityDelBtn').click(function(){
        delSys_entities();
    });
    //新建
    $('#entityAddBtn').click(function(){
        if($('#entity_nm_t').val()==''){
            alert('实体名称不能为空！');
            return false;
        }
        var dat_id=datasourceData[datasourceIndex].id;
        var param='[{"cn":"id","tp":"int","lt":"4","pk":"Y","nn":"Y","ai":"Y","cm":"默认主键"}]';
         addSys_entities(dat_id,param);
     });

    $('#saveEntityBtn').click(function(){
        if($('#en_nm_t').val()==''){
            alert('实体名称不能为空！');
            return false;
        }

        var param='{"nm_t":"'+$('#en_nm_t').val()+'","status":"'+$('#en_status').val()+'","desc_t":"'+$('#desc_t').val()+'"}';
        updateSys_entities(entityData[entityIndex].dat_id,entityData[entityIndex].id,param);


    });
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////数据源操作///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //数据源列表点击
    var ovalue='';
    $('#datasourceDiv > a').click(function(){
        $('#datasourceDiv > a').attr('class','list-group-item');
        $(this).attr('class','list-group-item active');
        datasourceIndex=$(this).attr('index');
        var currentDatasource=datasourceData[datasourceIndex];
        $('#datasourceTitle').text('数据源('+currentDatasource.nm_t+')');
        //初始化实体数据
        querySys_entities();
        $('#ds_nm_t').val(currentDatasource.nm_t);
        $('#ds_status').val(currentDatasource.status);
        $('#ds_c_dt').val(currentDatasource.c_dt);
        var dat_info=JSON.parse(currentDatasource.ds_json);
        $('#update_dbType').val(dat_info['dbType']);
        $('#update_dbHost').val(dat_info['dbHost']);
        $('#update_dbPort').val(dat_info['dbPort']);
        $('#update_dbName').val(dat_info['dbName']);
        $('#update_dbUser').val(dat_info['dbUser']);
        $('#update_dbPassword').val(dat_info['dbPassword']);

        if($('#update_dbType').val()=='redis'){
            $('#update_dbName').parent().parent().hide();
            $('#update_dbUser').parent().parent().hide();
            $('#update_dbPassword').parent().parent().hide();
            $('#ds_add_entity').attr('disabled',true);
            $('#delete').attr('disabled',true);
        }
        else{
            $('#update_dbName').parent().parent().show();
            $('#update_dbUser').parent().parent().show();
            $('#update_dbPassword').parent().parent().show();
            $('#ds_add_entity').attr('disabled',false);
            $('#delete').attr('disabled',false);
        }

        ovalue=$('#ds_nm_t').val()+$('#ds_status').val()+$('#update_dbType').val()+$('#update_dbHost').val()+
            $('#update_dbPort').val()+$('#update_dbName').val()+$('#update_dbUser').val()+$('#update_dbPassword').val();
        var sys_user=sessionStorage.getItem('sys_user');
        if(sys_user!=null){
            $('#ds_mem_id').val(JSON.parse(sys_user)[currentDatasource.mem_id]);
        }

        //删除按钮不可点击
        $('#delete').attr('data-target', '#config-delete-n');
        $('#save').attr('data-target', '#datasources-update');
        $('#dsPart').show();
        $('#entityPart').hide();
    });

    $('#update_dbType').change(function(){
        if($('#update_dbType').val()=='redis'){
            $('#update_dbName').parent().parent().hide();
            $('#update_dbUser').parent().parent().hide();
            $('#update_dbPassword').parent().parent().hide();
        }
        else{
            $('#update_dbName').parent().parent().show();
            $('#update_dbUser').parent().parent().show();
            $('#update_dbPassword').parent().parent().show();
        }

    });
    $('#saveDsBtn').click(function(){
        if($('#ds_nm_t').val()==''){
            alert('数据源名称不能为空！');
            return false;
        }
        if($('#update_dbHost').val()==''){
            alert('数据源地址不能为空！');
            return false;
        }
        if($('#update_dbPort').val()==''){
            alert('数据源端口不能为空！');
            return false;
        }
        if($('#update_dbName').val()==''){
            alert('数据源实例名称不能为空！');
            return false;
        }
        if($('#update_dbUser').val()==''){
            alert('数据源用户名不能为空！');
            return false;
        }
        if($('#update_dbPassword').val()==''){
            alert('数据源用户密码不能为空！');
            return false;
        }

        var cvalue=$('#ds_nm_t').val()+$('#ds_status').val()+$('#update_dbType').val()+$('#update_dbHost').val()+
            $('#update_dbPort').val()+$('#update_dbName').val()+$('#update_dbUser').val()+$('#update_dbPassword').val();
        if(ovalue==cvalue){
            alert('你没有修改任何数据！');
            return false;
        }
        else{
            var info='';
            if($('#update_dbType').val()=='redis'){
                info='{"dbHost":'+$('#update_dbHost').val()+',"dbName":"","dbPort":"'+$('#update_dbPort').val()+'","dbType":"'
                    +$('#update_dbType').val()+'","dbUser":"","dbPassword":""}';
            }
            else{
                info='{"dbHost":'+$('#update_dbHost').val()+',"dbName":"'+$('#update_dbName').val()+'","dbPort":"'+$('#update_dbPort').val()+'","dbType":"'
                    +$('#update_dbType').val()+'","dbUser":"'+$('#update_dbUser').val()+'","dbPassword":"'+$('#update_dbPassword').val()+'"}';
            }
            updateSys_datasources(info,datasourceData[datasourceIndex].id);
        }


    });

    if(jumpIndex!=null){
        $('#datasourceDiv > a').eq(jumpIndex).click();
    }
    else{
        //默认点击第一个接口
        //$('#interfaceDiv > a').eq(0).click();
        //默认点击第一个数据源
        $('#datasourceDiv > a').eq(0).click();
    }




});
