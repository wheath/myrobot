//$(document).bind('deviceready', function(){
function onDeviceReady() {
  console.log('MyRobot app started with moved js dir');
  $('#logon_button').click(function() { 
    alert('logon button clicked with js dir moved'); 
    try {
    $.ajax({
      url: "http://www.myrobot.com/drupal/drupalgap/system/connect.json",
      type: 'post',
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
  }
  catch (error) { 
    alert("page_dashboard - " + error); }
  }
}
