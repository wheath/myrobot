#!/usr/bin/python

import httplib, urllib
import simplejson as json

params = urllib.urlencode({'username': 'nyb', 'password': 'nyb'})
headers = {
            "Accept": "application/json"}
conn = httplib.HTTPConnection("www.myrobot.com:80")
conn.request("POST", "/drupal/rest/user/login.json", params, headers)
response = conn.getresponse()
print response.status, response.reason
login_data =  response.read()
print "response.read: %s\n\n\n" % login_data

login_data = json.loads(login_data)

print "newcbcmds:\n\n";
params = urllib.urlencode({'filters[cb_node_id]': '16'})
#headers = { 'Cookie':"sessid=%s" % login_data['sessid'],
headers = { 
            "Accept": "application/json"}
conn = httplib.HTTPConnection("www.myrobot.com:80")
conn.request("GET", "/drupal/rest/view/newcbcmds.json", params, headers)
response = conn.getresponse()
print response.status, response.reason
print "response.read: %s" % response.read()

print "update cellbot command node:\n\n";
params = '{"field_state":{"und":[{"value":"processed"}]}}'
#headers = { 'Cookie':"sessid=%s" % login_data['sessid'],
headers = { "Content-Type": "application/json",
            "Accept": "application/json"}
conn = httplib.HTTPConnection("www.myrobot.com:80")
conn.request("PUT", "/drupal/rest/node/89.json", params, headers)
response = conn.getresponse()
print response.status, response.reason
print "response.read: %s" % response.read()
