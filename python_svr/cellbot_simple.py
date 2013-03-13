# Licensed under the Apache License, Version 2.0 (the "License"); you may not
# use this file except in compliance with the License. You may obtain a copy of
# the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
# WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
# License for the specific language governing permissions and limitations under
# the License.
#
# See http://www.cellbots.com for more information

__author__ = 'Ryan Hickman <rhickman@gmail.com>'
#Contributers = Glen Arrowsmith glen@cellbots.com
__license__ = 'Apache License, Version 2.0'

import os
import time
import datetime
import socket
import select
import sys
import math
#import netip
import string
import re
from threading import Thread

import serial

# Listen for incoming serial responses. If this thread stops working, try rebooting. 
  
# Command input via open telnet port
def commandByTelnet():
  rs = []
  global svr_sock  # Fixing crash after exit command
  svr_sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
  print "Firing up telnet socket..."
  try:
    svr_sock.bind(('', 7777))
    svr_sock.listen(3)
    svr_sock.setblocking(0)
    #print "Ready to accept telnet. Use %s on port %s\n" % (phoneIP, telnetPort)
  except socket.error, (value,message):
    print "Could not open socket: " + message
    #print "You can try using %s on port %s\n" % (phoneIP, telnetPort)

  while 1:
    r,w,_ = select.select([svr_sock] + rs, [], [])

    for cli in r:
      if cli == svr_sock:
        new_cli,addr = svr_sock.accept()
        rs = [new_cli]
      else:  
        input = cli.recv(1024)
        input = input.replace('\r','')
        input = input.replace('\n','')
        if input != '':
          print "Received: '%s'" % input
          cli.sendall("ok\r\n")   # Send OK after every command recieved via telnet
          commandParse(input)


def commandParse(input):
  print "_dbg input: %c" % input
  ser.write("%c\n" % input)
  #ser.close()

#Non-configurable settings
cardinals = {}
cardinals['n']=('North','0')
cardinals['e']=('East','90')
cardinals['w']=('West','270')
cardinals['s']=('South','180')
previousMsg = ""
audioRecordingOn = False
#phoneIP = netip.displayNoLo()
ser = serial.Serial('/dev/tty.iap', 9600, timeout=1)

# Raise the sails and fire the cannons
def main():
  commandByTelnet()
  
if __name__ == '__main__':
    main()
