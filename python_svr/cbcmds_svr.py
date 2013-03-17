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
    print "_dbg processing command %s\n" % i.cmd
    #if(i.cmd == 'go_available'):
    ret_data = {}
    ret_data['status'] = 'success'
    ret_data['message'] = 'cellbot svr executed command: %s' % i.cmd  
    print ret_data['message'] + '\n'
    return "%s(%s)" % (cbfn, json.dumps(ret_data)) 

if __name__ == "__main__": 
    print "_dbg starting python svr\n";
    app = web.application(urls, globals())
    app.run()
