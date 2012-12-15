<?php
// REST Server URL
$request_url = 'http://www.graysonconsulting.biz/myrobot/?q=rest/user/login.json';

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

print 'logged_user\n';
print_r($logged_user);

// REST Server URL
$request_url = 'http://www.graysonconsulting.biz/myrobot/?q=rest/node.json';

// Node data

$node_data = array(
  'title' => 'test embedded video 1',
  'type' => 'cellbot',
  'body[und][0][value]' => '<p>Body</p>',
  'field_embedvid[0][embed]' =>'http://www.ustream.tv/recorded/25026138',
);


$node_data = http_build_query($node_data);

// Define cookie session
$cookie_session = $logged_user->session_name . '=' . $logged_user->sessid;

// cURL
$curl = curl_init($request_url);
curl_setopt($curl, CURLOPT_HTTPHEADER, array('Accept: application/json')); // Accept JSON response
curl_setopt($curl, CURLOPT_POST, 1); // Do a regular HTTP POST
curl_setopt($curl, CURLOPT_POSTFIELDS, $node_data); // Set POST data
curl_setopt($curl, CURLOPT_HEADER, FALSE);  // Ask to not return Header
curl_setopt($curl, CURLOPT_COOKIE, "$cookie_session"); // use the previously saved session
curl_setopt($curl, CURLOPT_RETURNTRANSFER, TRUE);
curl_setopt($curl, CURLOPT_FAILONERROR, TRUE);

$response = curl_exec($curl);
$http_code = curl_getinfo($curl, CURLINFO_HTTP_CODE);

// Check if login was successful
if ($http_code == 200) {
  // Convert json response as array
  $node = json_decode($response);
}
else {
  // Get error msg
  $http_message = curl_error($curl);
  die($http_message);
}

print_r($node);

?>
