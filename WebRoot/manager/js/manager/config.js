
//后台服务地址
var url = 'http://192.168.1.144/DIP/';
//secret key
var sk = '!QAZXSW@#C';



$(document).ready(function(){

    $('#dbType').change(function(){
        if($('#dbType').val()=='redis'){
            $('#dbName').parent().parent().parent().parent().hide();
            $('#dbName').val('');
            $('#dbUser').parent().parent().parent().parent().hide();
            $('#dbUser').val('');
            $('#dbPassword').parent().parent().parent().parent().hide();
            $('#dbPassword').val('');
        }
        else{
            $('#dbName').parent().parent().parent().parent().show();
            $('#dbName').val('');
            $('#dbUser').parent().parent().parent().parent().show();
            $('#dbUser').val('');
            $('#dbPassword').parent().parent().parent().parent().show();
            $('#dbPassword').val('');
        }

    });
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
        if($('#dbType').val()!='redis'&&$('#dbName').val()==''){
            alert('数据源实例名称不能为空！');
            return false;
        }
        if($('#dbType').val()!='redis'&&$('#dbUser').val()==''){
            alert('数据源用户名不能为空！');
            return false;
        }
        if($('#dbType').val()!='redis'&&$('#dbPassword').val()==''){
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

/**
 * 登录
 */
function login() {
    $.ajax({
        url : url+'Members',
        type : 'POST',
        data : {
            'username' : $('#username').val(),
            'userpwd' : $('#password').val()
        },
        success : function(response) {
            //console.log(response);
            var obj = JSON.parse(response);
            var token = obj['msg'];
            var userinfo = JSON.stringify(obj['info']);
            //var timestamp = Date.parse(new Date());
            //var hash = md5(token + timestamp + sk);
            sessionStorage.setItem('username',$('#username').val());
            sessionStorage.setItem('userpwd',$('#password').val());
            sessionStorage.setItem('userinfo',userinfo);
            sessionStorage.setItem('token',token);
            saveSys_members();
            //window.location.href='default-page.html?backurl='+window.location.href;
            window.location.href='default-page.html';
        },
        error : function(response) {
            alert('登录失败！');
        }
    });

}
//登录取token
/*function login() {
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

}*/

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
            },
            beforeSend:function(){
                //alert('before');
            },
            complete:function(){
                //alert('complete');
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
    /*var bodyParam={'method':'create','dat_id':dat_id,'mem_id':userinfo['id'],'status':$('#entity_status').val(),
        'nm_t':$('#entity_nm_t').val(),'desc_t':$('#entity_desc_t').val(),'code_t':$('#entity_code_t').val(),
        'param':param};*/
    var bodyParam={'method':'create','dat_id':dat_id,'mem_id':userinfo['id'],'status':'1',
        'nm_t':$('#entity_nm_t').val(),'desc_t':$('#entity_desc_t').val(),
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
    $('#r_c_li').html('');
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

            $('#r_e_title').text(currentEntity.nm_t);
            var html='',r_html='';
            for(var o in data){
                html+='<tr index='+o+'><td>'+data[o].column_name+'</td><td>'+data[o].column_type+'</td></tr>';
                r_html+='<li index='+o+' column_name='+data[o].column_name+'><a href="#">'+data[o].column_name+' ('+data[o].column_type+')</a></li>';
            }
            $('#columnTbody').html(html);
            $('#r_c_li').html(r_html);
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
function  querySys_sqllogs (currentPage,pageSize) {

    //分页显示的页码数  必须为奇数
    var showPage=7;
    $('#logTbody').html('');
    var param='{"page":"'+currentPage+'","size":"'+pageSize+'","order":"order by id desc"';
    if($('#searchDate').val()!=''){
        param+=',"op_dt":"date|'+$('#searchDate').val()+'"';
    }

    if($('#objects').val()!=''){
        param+=',"objects":"'+$('#objects').val()+'"';
    }

    param+='}';

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
            var userinfo = JSON.parse(sessionStorage.getItem('sys_user'));
            for(var o in data){
                html+='<tr index='+o+'>\n' +
                    '<td>'+data[o].planame+'</td>\n' +
                    '<td>'+userinfo[data[o].mem_id]+'</td>\n' +
                    '<td>'+data[o].objects+'</td>\n' +
                    '<td>'+data[o].operations+'</td>\n' +
                    '<td>'+data[o].op_dt+'</td>\n' +
                    '</tr>';
            }
            $('#logTbody').html(html);


            var num=msg['num'];
            var pageHtml='';
            var totalPage=0;
            if(num%pageSize==0){
                totalPage=num/pageSize;
            }
            else{
                totalPage=Math.ceil(num/pageSize);
            }
            //alert(num%pageSize+'---'+num/pageSize+'---'+totalPage);
            if(currentPage==1){
                pageHtml+='<li class="disabled"><a href="#">首页</a></li>';
                pageHtml+='<li class="disabled"><a href="#">上一页</a></li>';
            }
            else{
                pageHtml+='<li ><a href="#" class="pageBtn" index="1">首页</a></li>';
                pageHtml+='<li ><a href="#" class="prevBtn" index="">上一页</a></li>';
            }
            if(totalPage<=showPage){
                for(var i=1;i<Number(totalPage)+1;i++){
                    if(currentPage==i){
                        pageHtml+='<li class="active"><a href="#" >'+i+'</a></li>';
                    }
                    else{
                        pageHtml+='<li><a href="#" class="pageBtn" index="'+i+'">'+i+'</a></li>';
                    }
                }
            }
            else{
                if(currentPage<=(showPage-1)/2){
                    for(var i=1;i<=showPage;i++){
                        if(currentPage==i){
                            pageHtml+='<li class="active"><a href="#" >'+i+'</a></li>';
                        }
                        else{
                            pageHtml+='<li><a href="#" class="pageBtn" index="'+i+'">'+i+'</a></li>';
                        }
                    }
                }
                else if(totalPage-currentPage<(showPage-1)/2){
                    for(var i=Number(totalPage)-showPage;i<=totalPage;i++){
                        if(currentPage==i){
                            pageHtml+='<li class="active"><a href="#" >'+i+'</a></li>';
                        }
                        else{
                            pageHtml+='<li><a href="#" class="pageBtn" index="'+i+'">'+i+'</a></li>';
                        }
                    }
                }
                else{
                    for(var i=Number(currentPage)-(showPage-1)/2;i<=Number(currentPage)+(showPage-1)/2;i++){
                        if(currentPage==i){
                            pageHtml+='<li class="active"><a href="#" >'+i+'</a></li>';
                        }
                        else{
                            pageHtml+='<li><a href="#" class="pageBtn" index="'+i+'">'+i+'</a></li>';
                        }
                    }
                }


            }

            if(currentPage==totalPage){
                pageHtml+='<li class="disabled"><a href="#">下一页</a></li>';
                pageHtml+='<li class="disabled"><a href="#">尾页</a></li>';
            }
            else{
                pageHtml+='<li class="nextBtn" index=""><a href="#">下一页</a></li>';
                pageHtml+='<li class="pageBtn" index="'+totalPage+'"><a href="#">尾页</a></li>';
            }
            pageHtml+='<li><input type="text" id="jumpPageText" class="paging-inpbox form-control"></li>\n' +
                '<li><button type="button" id="jumpBtn" class="paging-btnbox btn btn-primary">跳转</button></li>\n' +
                '<li><span class="number-of-pages">共'+totalPage+'页</span></li>';

            $('#pageUl').html(pageHtml);



        }
    });
}

////////////////////////////////////////////////////////////////////////////////////
///////////////////////////////////关系管理////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////

/**
 * 查询实体关系
 * @param e_id
 */
function  relation_querySys_entities (e_id) {
    var currentDatasource=datasourceData[datasourceIndex];
    $('#r_e_select').html('');
    $('#r_c_select').html('');
    $('#r_t_name').html('');

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
            //entityData=msg['data'];
            var html='<option value="">请选择实体</option>';
            var rel_html='<option value="">请选择中间表</option>';

            for(var o in data){
                if(e_id!=data[o].id){
                    html+='<option value="'+data[o].id+'">'+data[o].nm_t+'</option>';
                    rel_html+='<option value="'+data[o].id+'">'+data[o].nm_t+'</option>';
                }
            }

            $('#r_e_select').html(html);
            $('#r_t_name').html(rel_html);

        }
    });
}
/**
 * 实体属性
 * @param info
 */
function   relation_querySys_entities_col (e_nm_t,c_name) {
    var currentDatasource=datasourceData[datasourceIndex];

    $('#r_c_select').html('');
    var bodyParam={'method':'column','tableName':e_nm_t,'dat_id':currentDatasource.id};
    var httpR = new createHttpR(url+'DbService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg;
            //保存数据到变量
            //columnData=msg;
            var html='';
            for(var o in data){
                if(c_name==data[o].column_name){
                    html+='<option value="'+o+'" selected="selected">'+data[o].column_name+'</option>';
                }
                else{
                    html+='<option value="'+o+'">'+data[o].column_name+'</option>';
                }
            }
            $('#r_c_select').html(html);



        }
    });
}

/**
 * 查询关系
 */
function  querySys_relations (e_id) {
    $('#r_tbody').html('');
    $('#relation_tbody').html('');
    //var param='{"order":"order by id desc","ent1_id":"'+e_id+'"}';
    //var bodyParam={'method':'query','tableName':'sys_relations','param':param};
    var param="select * from  sys_relations where (ENT1_ID='"+e_id+"' or ENT2_ID='"+e_id+"')";
    var bodyParam={'method':'execute','type':'select','sql':param};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            var data=msg;
            //保存数据到变量
            relData=msg;

            var html='',r_html='';
            for(var o in data){
                var r_json=JSON.parse(data[o].relation_json);
                if(data[o].ent1_id==entityData[entityIndex].id){
                    html+='<tr index='+o+'>\n' +
                        '<td>'+getEntityNameById(data[o].ent1_id)+'<span class="blue ml20">'+r_json[data[o].ent1_id]+'</span></td>\n' +
                        '<td><i class="glyphicon glyphicon-link"></i></td>\n' +
                        '<td>'+getEntityNameById(data[o].ent2_id)+'<span class="blue ml20">'+r_json[data[o].ent2_id]+'</span></td>\n' +
                        '<td>'+r_json['rel']+'</td>\n' +
                        '<td>\n' +
                        '<button title="删除" index='+o+' class="glyphicon glyphicon-trash fr mr10 titlebox-btn3" data-toggle="modal" data-target="#entity-rela-del"></button>\n' +
                        '</td>\n' +
                        '</tr>';
                    r_html+='<tr>\n' +
                        '<td>'+getEntityNameById(data[o].ent1_id)+'</td>\n' +
                        '<td><i class="glyphicon glyphicon-link"></i></td>\n' +
                        '<td>'+getEntityNameById(data[o].ent2_id)+'</td>\n' +
                        '<td>'+r_json['rel']+'</td>\n' +
                        '</tr>';
                }
                else{
                    html+='<tr index='+o+'>\n' +
                        '<td>'+getEntityNameById(data[o].ent2_id)+'<span class="blue ml20">'+r_json[data[o].ent2_id]+'</span></td>\n' +
                        '<td><i class="glyphicon glyphicon-link"></i></td>\n' +
                        '<td>'+getEntityNameById(data[o].ent1_id)+'<span class="blue ml20">'+r_json[data[o].ent1_id]+'</span></td>\n' +
                        '<td>'+r_json['rel'].split('').reverse().join('')+'</td>\n' +
                        '<td>\n' +
                        '<button title="删除" index='+o+' class="glyphicon glyphicon-trash fr mr10 titlebox-btn3" data-toggle="modal" data-target="#entity-rela-del"></button>\n' +
                        '</td>\n' +
                        '</tr>';
                    r_html+='<tr>\n' +
                        '<td>'+getEntityNameById(data[o].ent2_id)+'</td>\n' +
                        '<td><i class="glyphicon glyphicon-link"></i></td>\n' +
                        '<td>'+getEntityNameById(data[o].ent1_id)+'</td>\n' +
                        '<td>'+r_json['rel'].split('').reverse().join('')+'</td>\n' +
                        '</tr>';
                }

            }
            $('#r_tbody').html(html);
            $('#relation_tbody').html(r_html);
        }


    });
}

/**
 * 新建关系
 * @param info
 */
function addSys_relations(ent1_id,col1,ent2_id,col2,info,rel_table,fk){



    if(fk=='n'){
        var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
        var bodyParam={'method':'insert','tableName':'sys_relations',
            'param':'{"mem_id":"'+userinfo['id']+'","status":"1","ent1_id":"'+ent1_id+
            '","ent2_id":"'+ent2_id+'","relation_json":{"'+ent1_id+'":"'+col1+'","'+ent2_id+'":"'+
            col2+'","rel":"'+info+'","rel_table":"'+rel_table+'","fk":"'+fk+'"}}'};

        var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
        httpR.HttpRequest(function(response){
            var obj = JSON.parse(response);
            var status = obj['status'];
            var msg = obj['msg'];
            if(status=='0'){
                alert("新建成功！");
                //window.location.reload();
                //window.location.href="interface.html?index="+interfaceIndex;
                window.location.href="entity.html?index="+datasourceIndex+"&eindex="+entityIndex;
            }
        });
    }
    else{
        var bodyParam={'method':'fkey','dat_id':datasourceData[datasourceIndex].id,'tableNameS':getEntityNameById(ent1_id),'colS':col1,
            'tableNameP':getEntityNameById(ent2_id),'colP':col2};

        var httpR = new createHttpR(url+'DbService','post','text',bodyParam,'callBack');
        httpR.HttpRequest(function(response){
            var obj = JSON.parse(response);
            var status = obj['status'];
            var msg = obj['msg'];
            if(status=='0'){
                var userinfo = JSON.parse(sessionStorage.getItem('userinfo'));
                var bodyParam={'method':'insert','tableName':'sys_relations',
                    'param':'{"mem_id":"'+userinfo['id']+'","status":"1","ent1_id":"'+ent1_id+
                    '","ent2_id":"'+ent2_id+'","relation_json":{"'+ent1_id+'":"'+col1+'","'+ent2_id+'":"'+
                    col2+'","rel":"'+info+'","rel_table":"'+rel_table+'","fk":"'+msg+'"}}'};

                var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
                httpR.HttpRequest(function(response){
                     obj = JSON.parse(response);
                     status = obj['status'];
                     msg = obj['msg'];
                    if(status=='0'){
                        alert("新建成功！");
                        //window.location.reload();
                        window.location.href="entity.html?index="+datasourceIndex+"&eindex="+entityIndex;
                        //window.location.href="interface.html?index="+interfaceIndex;
                    }
                });
            }
        });
    }



}
/**
 * 删除关系
 * @param info
 */

function delSys_relations(id){
    var bodyParam={'method':'delete','tableName':'sys_relations',
        'condition':'{"id":"'+id+'"}'};
    var httpR = new createHttpR(url+'DipService','post','text',bodyParam,'callBack');
    httpR.HttpRequest(function(response){
        var obj = JSON.parse(response);
        var status = obj['status'];
        var msg = obj['msg'];
        if(status=='0'){
            alert(msg);
            //window.location.reload();
            //window.location.href="interface.html?index="+interfaceIndex;
            window.location.href="entity.html?index="+datasourceIndex+"&eindex="+entityIndex;
        }
    });
}

function getEntityNameById(e_id) {
    var nm_t = '';
    for (var o in entityData) {
        if (entityData[o].id == e_id) {
            nm_t = entityData[o].nm_t;
        }
    }
    return nm_t;
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
