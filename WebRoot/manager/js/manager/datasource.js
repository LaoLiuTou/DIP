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

    $('#delete').attr('data-target', '');
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
        $('#delete').attr('data-target', '#config-delete-x');
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

        ovalue=$('#ds_nm_t').val()+$('#ds_status').val()+$('#update_dbType').val()+$('#update_dbHost').val()+
            $('#update_dbPort').val()+$('#update_dbName').val()+$('#update_dbUser').val()+$('#update_dbPassword').val();
        var sys_user=sessionStorage.getItem('sys_user');
        if(sys_user!=null){
            $('#ds_mem_id').val(JSON.parse(sys_user)[currentDatasource.mem_id]);
        }

        //删除按钮不可点击
        $('#delete').attr('data-target', '');

    });

    $('#saveBtn').click(function(){
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
            var info='{"dbHost":'+$('#update_dbHost').val()+',"dbName":"'+$('#update_dbName').val()+'","dbPort":"'+$('#update_dbPort').val()+'","dbType":"'
                +$('#update_dbType').val()+'","dbUser":"'+$('#update_dbUser').val()+'","dbPassword":"'+$('#update_dbPassword').val()+'"}';
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
