<?php
// REST Server URL
$request_url = 'http://www.graysonconsulting.biz/myrobot/?q=rest/user/login.xml';

// User data
$user_data = array(
  'username' => 'myrobotadmin',
  'password' => 'robot45er',
);

// cURL
$curl = curl_init();
curl_setopt($curl, CURLOPT_URL, $request_url);
curl_setopt($curl, CURLOPT_POST, 1); // Do a regular HTTP POST
curl_setopt($curl, CURLOPT_HTTPHEADER, array('Content-Type: application/json'));
curl_setopt($curl, CURLOPT_POSTFIELDS, json_encode($user_data)); // Set POST data
curl_setopt($curl, CURLOPT_RETURNTRANSFER, TRUE);

$response = curl_exec($curl);

print $response;

$http_code = curl_getinfo($curl, CURLINFO_HTTP_CODE);
// Check if login was successful
if ($http_code == 200) {
  // Convert json response as array
  $logged_user = json_decode($response);
}
else {
  // Get error msg
  $http_message = curl_error($curl);
  die($http_message);
}

print_r($logged_user);
?>
