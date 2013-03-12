import web
import simplejson as json

urls = (
  '/', 'index'
)

class index:
  def GET(self):
    i = web.input()
    cbfn = i.callback
    web.header('Content-Type', 'application/javascript') 
    print "_dbg processing cellbot command %s\n" % i.cmd
    ret_data = getattr(self, i.cmd)(i)
    return "%s(%s)" % (cbfn, json.dumps(ret_data)) 

  def left(self, i):
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return ret_data;

  def right(self, i):
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return ret_data;

  def backward(self, i):
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return ret_data;

  def forward(self, i):
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return ret_data;

if __name__ == "__main__": 
    app = web.application(urls, globals())
    app.run()
