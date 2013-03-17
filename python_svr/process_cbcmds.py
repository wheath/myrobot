#!/usr/bin/python

import httplib, urllib, sys
import simplejson as json
from time import sleep
import serial

ser = serial.Serial('/dev/tty.iap', 9600, timeout=1)

def login_to_drupal(username, password):
  params = urllib.urlencode({'username': username, 'password': password})
  headers = {
              "Accept": "application/json"}
  conn = httplib.HTTPConnection("www.myrobot.com:80")
  conn.request("POST", "/drupal/rest/user/login.json", params, headers)
  response = conn.getresponse()
  print response.status, response.reason
  login_data =  response.read()
  print "response.read: %s\n\n\n" % login_data
  login_data = json.loads(login_data)

def change_cb_cmd_to_processed(cb_cmd_nid): 
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

def get_cb_cmds(data, cb_nid):
  cb_nid_to_cb_cmd = {}
  print "_dbg total # of cellbot commands: %d\n" % len(data)
  for i in range(len(data)):
    cb_cmd = data[i];
    print "_dbg cb_cmd num: %d cb_cmd['title']: %s\n" % (i, cb_cmd['title'])
    print "_dbg cb_cmd num: %d cb_cmd['nid']: %s\n" % (i, cb_cmd['nid'])
    cb_node_id = cb_cmd['field_cb_node_id']['und'][0]['value']
    print "_dbg cb_cmd num: %d cb_node_id: %s\n" % (i, cb_node_id)
    if(cb_node_id == cb_nid): 
      cmd = cb_cmd['body']['und'][0]['value']
      cb_nid_to_cb_cmd[cb_cmd['nid']] = cmd
  return cb_nid_to_cb_cmd

def get_unprocessed_cb_cmds():
  print "newcbcmds:\n\n";
  params = urllib.urlencode({'filters[cb_node_id]': '16'})
  #headers = { 'Cookie':"sessid=%s" % login_data['sessid'],
  headers = { 
	      "Accept": "application/json"}
  conn = httplib.HTTPConnection("www.myrobot.com:80")
  conn.request("GET", "/drupal/rest/view/newcbcmds.json", params, headers)
  response = conn.getresponse()
  print response.status, response.reason
  cb_cmds = response.read()
  print "response.read: %s" % cb_cmds
  return cb_cmds

def main():
  print "_dbg sys.argv: %s" % str(sys.argv)
  login_to_drupal(sys.argv[1], sys.argv[2])
  while True:
    all_cb_cmds = json.loads(get_unprocessed_cb_cmds())
    print "newcbcmds:\n\n";
    filtered_cb_cmds = get_cb_cmds(all_cb_cmds, '16')
    print "filtered_cb_cmds: %s\n" % json.dumps(filtered_cb_cmds)
    for cb_cmd_nid in filtered_cb_cmds:
      print "_dbg cb_cmd_nid %s command: %s\n" % (cb_cmd_nid, filtered_cb_cmds[cb_cmd_nid])
      print "_dbg sending cellbot command %s to arduino\n" % i.cmd
      ser.write("%c\n" % i.cmd)
      change_cb_cmd_to_processed(cb_cmd_nid) 
    print "sleeping 15 seconds\n"
    sleep(15)
if __name__ == '__main__':
    main()

