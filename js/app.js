function onDeviceReady() {
  console.log('MyRobot app started with moved js dir');
  $('#logon_button').click(function() { 

    alert('logon button clicked with js dir moved'); 
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
          $('#logon_button').show();
          $('#logout_button').hide();
        }
        else { // user is logged in, hide the login button, show the logout button
          alert("logged in");
          $('#logon_button').hide();
          $('#logout_button').show();
        }
      }
    });
});

}
