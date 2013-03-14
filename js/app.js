var timeoutID;

function processCommands() {
    jQuery.ajax({
            url : "http://www.myrobot.com/drupal/rest/view/newcbcmds.json",
            type : 'get',
            data: 'filters[cb_node_id]=16',
            dataType : 'json',
            error : function(data) {
                //error code
                console.log('Error: ' + JSON.stringify(data));
                },
            success : function(data) {
              //success code
              console.log('retrieved cellbot commands from newcbcmds view: ' + JSON.stringify(data));
              var cb_cmds = get_cellbot_cmds(16, data); 
              console.log('cb_cmds for cellbot node id 16: ' + JSON.stringify(cb_cmds));
              for(var cb_cmd_nid in cb_cmds) {
                cb_cmd = cb_cmds[cb_cmd_nid];
                console.log('processing cellbot command with nid: ' + cb_cmd_nid + ' with command vaue: ' + cb_cmd);
                send_cbcmd_to_pythonsvr(cb_cmd);
                change_cb_cmd_to_processed(cb_cmd_nid); 
              }
            }
   });

  if($("#toggle_available").text() == "Go Unavailable") {
    timeoutID = setTimeout(processCommands, 15000);
  }
}

function change_cb_cmd_to_processed(cb_cmd_nid) {
  console.log('updating field_state field of cellbot command with nid: ' + cb_cmd_nid + ' with command value: ' + cb_cmd_nid + ' to processed');

  //var updateObj = {"type":"cellbot_command","field_state":{"und":"processed"}};
  $.ajax({
      url: "http://www.myrobot.com/drupal/rest/node/" + cb_cmd_nid + '.json',
      type: 'PUT',
      data: '{"field_state":{"und":[{"value":"processed"}]}}',
      dataType: 'json',
      contentType: 'application/json',
      error: function(XMLHttpRequest, textStatus, errorThrown) {
        console.log('failed to updated cellbot command to processed');
        console.log(JSON.stringify(XMLHttpRequest));
        console.log(JSON.stringify(textStatus));
        console.log(JSON.stringify(errorThrown));
      },
      success: function (data) {
        console.log("success updating node to processed");
      }
  });
}

function get_cellbot_cmds(cb_node_id, obj) {
    var cb_nid_to_cb_cmd = {};
   for(var i=0;i < obj.length;i++) {
    
    console.log("_dbg obj[i].nid: " +  obj[i].nid);
    console.log("_dbg obj[i].title: " +  obj[i].title);
    console.log("_dbg obj[i].field_cb_node_id['und'][0]['value']: " +  obj[i].field_cb_node_id['und'][0]['value']);
    console.log("_dbg obj[i].body['und'][0]['value']: " +  obj[i].body['und'][0]['value']);
       if(obj[i].field_cb_node_id['und'][0]['value'] == cb_node_id) {
          cb_nid_to_cb_cmd[obj[i].nid] = obj[i].body['und'][0]['value'];
       }
    
   } 
 return cb_nid_to_cb_cmd;
}



function onDeviceReady() {
  console.log('MyRobot app started with moved js dir');
  $('#logon_button').click(function() { 

    var name = $('#page_login_name').val();
  if (!name) { alert('Please enter your user name.'); return false; }
  var pass = $('#page_login_pass').val();
  if (!pass) { alert('Please enter your password.'); return false; }
      //url: "http://www.myrobot.com/drupal/drupalgap/system/connect.json",
    $.ajax({
      url: "http://www.myrobot.com/drupal/rest/user/login.json",
      type: 'post',
      data: 'username=' + encodeURIComponent(name) + '&password=' + encodeURIComponent(pass),
      dataType: 'json',
      error: function (XMLHttpRequest, textStatus, errorThrown) {
        alert('page_dashboard - failed to system connect');
        console.log(JSON.stringify(XMLHttpRequest));
        console.log(JSON.stringify(textStatus));
        console.log(JSON.stringify(errorThrown));
      },
      success: function (data) {
        console.log("success logging in");
        var drupal_user = data.user;
        if (drupal_user.uid == 0) { // user is not logged in, show the login button, hide the logout button
          console.log("not logged in");
          //$('#logon_button').show();
          //$('#logout_button').hide();
        }
        else { // user is logged in, hide the login button, show the logout button
          console.log("logged in");
          //$('#logon_button').hide();
          //$('#logout_button').show();
          var sessid = data.sessid;
          var session_name = data.session_name;
          var user = data.user;       
          console.log('_dbg session_name',session_name);
          console.log('_dbg sessid',sessid); 
          jQuery.cookie(session_name, sessid);
        }
      }
    });
});

  $('#logoff_button').click(function() { 
    $.ajax({
      url: "http://www.myrobot.com/drupal/rest/user/logout.json",
      type: 'post',
      dataType: 'json',
      error: function (XMLHttpRequest, textStatus, errorThrown) {
        alert('page_dashboard - failed to system connect');
        console.log(JSON.stringify(XMLHttpRequest));
        console.log(JSON.stringify(textStatus));
        console.log(JSON.stringify(errorThrown));
      },
      success: function (data) {
        console.log("success you have been logged out");
      }
    });
});

$('#toggle_available').live('click',function(){
  if($(this).text() == "Go Available") {
    $(this).text("Go Unavailable");
    processCommands();
  } else {
    clearTimeout(timeoutID);
    $(this).text("Go Available");
  }
});

$('#page_node_create_submit').live('click',function(){

  var title = 'test cellbot create from myrobot proper';
  var body = 'test body from myrobot proper';

  // BEGIN: drupal services node create login (warning: don't use https if you don't have ssl setup)
  $.ajax({
      url: "http://www.myrobot.com/drupal/rest/node.json",
      type: 'post',
      data: 'node[type]=cellbot&node[title]=' + encodeURIComponent(title) + '&node[language]=und&node[body][und][0][value]=' + encodeURIComponent(body),
      dataType: 'json',
      error: function(XMLHttpRequest, textStatus, errorThrown) {
        alert('page_node_create_submit - failed to login');
        console.log(JSON.stringify(XMLHttpRequest));
        console.log(JSON.stringify(textStatus));
        console.log(JSON.stringify(errorThrown));
      },
      success: function (data) {
        console.log("success creating node");
      }
  });
  // END: drupal services node create
  });

  function send_cbcmd_to_pythonsvr(cb_cmd) {
    $("#status").text("status: " + cb_cmd);
    $.getJSON("http://localhost:8080/?callback=?&cmd=" + cb_cmd, 
    function(rtndata) {
        console.log('json returned' + JSON.stringify(rtndata));
        console.log('python server json returned: ' + JSON.stringify(rtndata));
    }); 
  }

  $('#leftcbcmd_to_python').live('click',function() {
    console.log("_dbg sending left to python svr");
    send_cbcmd_to_pythonsvr('l');
  });

  $('#process_cmds').live('click',function(){
    processCommands(); 
  });
}
