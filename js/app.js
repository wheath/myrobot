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
        alert("success");
        var drupal_user = data.user;
        if (drupal_user.uid == 0) { // user is not logged in, show the login button, hide the logout button
          alert("not logged in");
          //$('#logon_button').show();
          //$('#logout_button').hide();
        }
        else { // user is logged in, hide the login button, show the logout button
          alert("logged in");
          //$('#logon_button').hide();
          //$('#logout_button').show();
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
        alert("success you have been logged out");
      }
    });
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
        alert("success creating node");
      }
  });
  // END: drupal services node create
  });

}
