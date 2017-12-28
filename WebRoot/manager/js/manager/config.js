
//后台服务地址
var url = 'http://192.168.1.144/DIP/';
//secret key
var sk = '!QAZXSW@#C';



$(document).ready(function(){
    $('#addDatasourceBtn').click(function(){
        if($('#datasource_nm_t').val()==''){
            alert('数据源名称不能为空！');
            return false;
        }
        if($('#dbHost').val()==''){
            alert('数据源地址不能为空！');
            return false;
        }
        if($('#dbPort').val()==''){
            alert('数据源端口不能为空！');
            return false;
        }
        if($('#dbName').val()==''){
            alert('数据源实例名称不能为空！');
            return false;
        }
        if($('#dbUser').val()==''){
            alert('数据源用户名不能为空！');
            return false;
        }
        if($('#dbPassword').val()==''){
            alert('数据源用户密码不能为空！');
            return false;
        }
        var info='{"dbHost":'+$('#dbHost').val()+',"dbName":"'+$('#dbName').val()+'","dbPort":"'+$('#dbPort').val()+'","dbType":"'
            +$('#dbType').val()+'","dbUser":"'+$('#dbUser').val()+'","dbPassword":"'+$('#dbPassword').val()+'"}';

        addSys_datasources(info);

    });
    $('#deleteDatasourceBtn').click(function () {
        delSys_datasources();
    });
    $('#logoutBtn').click(function () {
        sessionStorage.clear();
        window.location.href='login.html';
    });


    var userinfo=sessionStorage.getItem('userinfo');
    if(userinfo!=null){
        $('#loginName').text(JSON.parse(userinfo)['displayname']);

    }

});


//登录取token
function login() {
    var username =sessionStorage.getItem('username');
    var userpwd =sessionStorage.getItem('userpwd');
    $.ajax({
        url : url+'Members',
        type : 'POST',
        data : {
            'username' : username,
            'userpwd' : userpwd
        },
        success : function(response) {
            console.log(response);
            var obj = JSON.parse(response);
            var token = obj['msg'];
            //var timestamp = Date.parse(new Date());
            //var hash = md5(token + timestamp + sk);

            if(window.sessionStorage){
                sessionStorage.setItem('token',token);
            }else{

            }
        },
        error : function(response) {
            alert('登录失败！');
        }
    });

}

function createHttpR(url,type,dataType,bodyParam){
    this.url = url;
    this.type = type;
    this.dataType = dataType;
    this.bodyParam = bodyParam;
}
createHttpR.prototype.HttpRequest = function(callBack){

    if(sessionStorage.getItem('username')!=null||sessionStorage.getItem('token')!=null){
        var  token = sessionStorage.getItem('token');
        var timestamp = Date.parse(new Date());
        var hash = md5(token+timestamp+sk);
        $.ajax({
            url:this.url,
            type:this.type,
            cache:false,
            timeout:20,
            dataType:this.dataType,
            data :this.bodyParam,
            async:false,
            headers: {
                'token' : token,
                'timesamp' : timestamp,
                'sign' : hash
            },
            success:function(response) {
                var obj = JSON.parse(response);
                var status = obj['status'];
                var msg = obj['msg'];
                if(status=='mismatch'||status=='expire'){
                    console.log(msg);
                    alert('验证信息错误，请重新登录！');
                    //无用户信息，重新登录
                    window.location.href='login.html';
                    //login();
                }
                else if(status=='0'){
                    callBack(response);
                }
                else{
                    alert(msg);
                }
            },
            error:function(response){
                alert('请求失败！');
            }
        });
    }
    else{
        alert('访问权限已过期，请重新登录！');
        //无用户信息，重新登录
        window.location.href='login.html';
    }

}


function GetQueryString(name)
{
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}

////////////////////////////////////////////////////////////////////////////////////
 ///////////////////////////////////用户管理///////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
/**
 * 查询用户数据
 */
function  querySys_members (displayname) {
    var param='';
    if(displayname==''){
        param='{"page":"1","size":"10","order":"order by id desc"}';
    }
    else{
        param='{"page":"1","size":"10","order":"order by id desc","displayname":"'+displayname+'"}';
    }
    var bodyParam={'method':'query','tableName':'sys_members','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //console.log(data);
            var html='';
            for(var o in data){
                var c_name='';
                for(var p in data){
                    if(data[o].mem_id==data[p].id){
                        c_name=data[p].displayname;
                    }
                }
                html+='<tr id='+data[o].id+'>'+
                    '<td>'+data[o].displayname+'</td>'+
                    '<td>'+data[o].username+'</td>'+
                    //'<td>'+data[o].userpwd+'</td>'+
                    '<td>'+data[o].c_dt+'</td>'+
                    '<td>'+c_name+'</td>'+
                    '</tr>';
            }
            $('#data_tbody').html(html);
        }
    });
}

/**
 * 保存用户名和id
 * @param displayname
 */
function  saveSys_members () {
    var param='{"page":"1","size":"10","order":"order by id desc"}';

    var bodyParam={'method':'query','tableName':'sys_members','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //console.log(data);
            var userInfo='{';
            for(var o in data){
                userInfo+='"'+data[o].id+'":"'+data[o].displayname+'",';

            }
            if(userInfo.length>2){
                userInfo=userInfo.substr(0,userInfo.length-1)+'}';
                sessionStorage.setItem('sys_user',userInfo);
            }
            else{

            }
            //alert(sessionStorage.getItem('sys_user'));
        }
    });
}

/**
 * 新建用户数据
 */
function  addSys_members () {
    var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
    var bodyParam={'method':'insert','tableName':'sys_members',
        'param':'{"mem_id":"'+userinfo['id']+'","status":"1","username":"'+$('#username').val()+
        '","userpwd":"'+md5($('#userpwd').val())+'","displayname":"'+$('#displayname').val()+'"}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert("新建成功！");
            window.location.reload();
        }
    });
}

/**
 * 删除用户
 */
function delSys_members(){
    var bodyParam={'method':'delete','tableName':'sys_members',
        'condition':'{"id":"'+selectId+'"}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            window.location.reload();
        }
    });
}


////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////接口管理////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
/**
 * 新建接口
 * @param info
 */
function addSys_interfaces(info){
    var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
    var bodyParam={'method':'insert','tableName':'sys_interfaces',
        'param':'{"mem_id":"'+userinfo['id']+'","status":"1","nm_t":"'+$('#nm_t').val()+
        '","pro_id":"0","type":"数据平台","interface_json":'+info+'}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert("新建成功！");
            window.location.reload();
            //window.location.href="interface.html?index="+interfaceIndex;
        }
    });
}
/**
 * 修改接口
 * @param info
 */
function updateSys_interfaces(info,id){
    var bodyParam={'method':'update','tableName':'sys_interfaces',
        'param':'{"status":"1","nm_t":"'+$('#updateNm_t').val()+
        '","pro_id":"0","type":"数据平台接口","interface_json":'+info+'}','condition':'{"id":"'+id+'"}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            //window.location.reload();
            window.location.href="interface.html?index="+interfaceIndex;
        }
    });
}

/**
 * 删除接口
 */
function delSys_interfaces(id){
    var bodyParam={'method':'delete','tableName':'sys_interfaces',
        'condition':'{"id":"'+id+'"}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            window.location.reload();
            //window.location.href="interface.html?index="+interfaceIndex;
        }
    });
}




/**
 * 查询接口
 * @param info
 */
function  querySys_interfaces () {
    $('#interfaceDiv').html('');
    var param='{"order":"order by id desc","pro_id":"0","type":"数据平台"}';
    var bodyParam={'method':'query','tableName':'sys_interfaces','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //保存数据到变量
            interfaceData=msg['data'];
            var html='';
            for(var o in data){
                html+='<a index='+o+' class="list-group-item"><span class="glyphicon glyphicon-modal-window mr10"></span>'+data[o].nm_t+'</a>';
            }
            $('#interfaceDiv').html(html);
        }
    });
}

////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////数据源管理////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

/**
 * 新建数据源
 */
function addSys_datasources(info){
    var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
    var bodyParam={'method':'create','dbInfo':info,'mem_id':userinfo['id'],'status':$('#datasource_status').val(),
        'nm_t':$('#datasource_nm_t').val(),'pro_id':'0'};
    alert(bodyParam);
    var httpR = new createHttpR(url+'DataSource','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert("新建成功！");
            window.location.reload();
            //window.location.href="datasource.html?index="+datasourceIndex;
        }
    });
}
/**
 * 修改数据源
 */
function updateSys_datasources(info,id){
    var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
    var bodyParam={'method':'update','tableName':'sys_datasources',
        'param':'{"status":"'+$('#ds_status').val()+'","nm_t":"'+$('#ds_nm_t').val()+
        '","ds_json":'+info+'}','condition':'{"id":"'+id+'"}'};

    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert("修改成功！");
            //window.location.reload();
            window.location.href="datasource.html?index="+datasourceIndex;
        }
    });
}
/**
 * 删除数据源
 */
function delSys_datasources(id){
    var currentDatasource=datasourceData[datasourceIndex];
    var bodyParam={'method':'delete','dat_id':currentDatasource.id};
    var httpR = new createHttpR(url+'DataSource','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            window.location.reload();
            //window.location.href="datasource.html?index="+datasourceIndex;
        }
    });
}
/**
 * 数据源
 * @param info
 */
function  querySys_datasources () {
    $('#datasourceDiv').html('');
    var param='{"order":"order by id desc"}';
    var bodyParam={'method':'query','tableName':'sys_datasources','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //保存数据到变量
            datasourceData=msg['data'];
            var html='';
            for(var o in data){
                html+='<a index='+o+' class="list-group-item"><span class="glyphicon glyphicon-signal mr10"></span>'+data[o].nm_t+'<button class="glyphicon glyphicon-trash fr sm-deletebtn" data-toggle="modal" data-target="#config-delete-x"></button></a>';
            }
            $('#datasourceDiv').html(html);
        }
        querySys_interfaces();

    });
}


////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////实体管理////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
/**
 * 实体
 * @param info
 */
function  querySys_entities () {
    var currentDatasource=datasourceData[datasourceIndex];
    $('#entityUl').html('');
    var param='{"order":"order by id desc","dat_id":"'+currentDatasource.id+'"}';
    var bodyParam={'method':'query','tableName':'sys_entities','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //保存数据到变量
            entityData=msg['data'];
            $('#entityTitle').text(' '+currentDatasource.nm_t);
            var html='',html_show='';
            for(var o in data){
                html+='<li index='+o+'><a href="javascript:;">&nbsp;'+data[o].nm_t+'</a></li>';
                html_show+='<li  index='+o+'><a  href="javascript:;">'+data[o].nm_t+'</a></li>';
            }
            $('#entityUl').html(html);
            $('#entityUl_show').html(html_show);
        }
    });
}

/**
 * 新建实体
 */
function addSys_entities(dat_id,param){
    //data : {'method':'create','dat_id':'1','mem_id':'1','status':'1','nm_t':'testst2',
    //'desc_t':'nothing','code_t':'nothing',
    //'param':'[{"cn":"userid","tp":"int","lt":"4","pk":"Y","nn":"Y","ai":"Y","cm":"用户id"},{"cn":"username","tp":"varchar","lt":"20","nn":"Y","cm":"用户名"},{"cn":"password","tp":"varchar","lt":"30","nn":"Y","cm":"密码"}]'},

    var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
    var bodyParam={'method':'create','dat_id':dat_id,'mem_id':userinfo['id'],'status':$('#entity_status').val(),
        'nm_t':$('#entity_nm_t').val(),'desc_t':$('#entity_desc_t').val(),'code_t':$('#entity_code_t').val(),
        'param':param};
    var httpR = new createHttpR(url+'Entity','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert("新建成功！");
            //window.location.reload();
            window.location.href="datasource.html?index="+datasourceIndex;
        }
    });
}
/**
 * 删除实体
 */
function delSys_entities(){
    //data : {'method':'delete','ent_id':'3','tableName':'testst2'},
    var currentEntity=entityData[entityIndex];
    var bodyParam={'method':'delete','ent_id':currentEntity.id,'tableName':currentEntity.nm_t};
    var httpR = new createHttpR(url+'Entity','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            //window.location.reload();
            window.location.href="datasource.html?index="+datasourceIndex;
        }
    });

}
/**
 * 实体属性
 * @param info
 */
function  querySys_entities_col () {
    var currentDatasource=datasourceData[datasourceIndex];
    var currentEntity=entityData[entityIndex];
    //{'method':'column','dat_id':'1','tableName':'user3'},

    $('#columnTbody').html('');
    var bodyParam={'method':'column','tableName':currentEntity.nm_t,'dat_id':currentDatasource.id};
    var httpR = new createHttpR(url+'DbService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg;
            //保存数据到变量
            columnData=msg;

            var html='';
            for(var o in data){
                html+='<tr index='+o+'><td>'+data[o].column_name+'</td><td>'+data[o].column_type+'</td></tr>';
            }
            $('#columnTbody').html(html);
        }
    });
}

////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////日志管理////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
/**
 * 查询日志
 * @param info
 */
function  querySys_sqllogs () {
    $('#interfaceDiv').html('');
    var param='{"order":"order by id desc"}';
    var bodyParam={'method':'query','tableName':'sys_sqllogs','param':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg['data'];
            //保存数据到变量
            interfaceData=msg['data'];
            var html='';
            for(var o in data){
                html+='<a index='+o+' class="list-group-item"><span class="glyphicon glyphicon-modal-window mr10"></span>'+data[o].nm_t+'</a>';
            }
            $('#interfaceDiv').html(html);
        }
    });
}



////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////////sql/////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////
    /**
     * 执行sql
     */
function executeSql(dat_id,type,sql,jumpUrl){
    var bodyParam={'method':'execute','dat_id':dat_id,'type':type,'sql':sql};
    var httpR = new createHttpR(url+'DbService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            //window.location.reload();
            window.location.href=jumpUrl;
        }
    });
}
