package netty.demo.cp01.tomcat;

import netty.demo.cp01.tomcat.common.GPRequest;
import netty.demo.cp01.tomcat.common.GPResponse;
import netty.demo.cp01.tomcat.common.GPServlet;

public class FirstServlet extends GPServlet {

    @Override
    public void doGet(GPRequest request, GPResponse response) throws Exception{
        this.doGet(request, response);
    }

    @Override
    public void doPost(GPRequest request, GPResponse response) throws Exception {
        this.doPost(request, response);
    }
}
