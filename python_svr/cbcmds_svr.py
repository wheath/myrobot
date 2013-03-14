import web
import simplejson as json
import serial

ser = serial.Serial('/dev/tty.iap', 9600, timeout=1)

urls = (
  '/', 'index'
)

class index:
  def GET(self):
    i = web.input()
    cbfn = i.callback
    web.header('Content-Type', 'application/javascript') 
    print "_dbg processing cellbot command %s\n" % i.cmd
    ser.write("%c\n" % input)
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return "%s(%s)" % (cbfn, json.dumps(ret_data)) 

if __name__ == "__main__": 
    print "_dbg starting python svr\n";
    app = web.application(urls, globals())
    app.run()
