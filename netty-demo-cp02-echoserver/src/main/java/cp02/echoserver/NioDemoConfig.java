package cp02.echoserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class NioDemoConfig {

    private InputStream fis;
    private Properties properties = new Properties();

    public NioDemoConfig() throws IOException {
        this.fis = this.getClass().getResourceAsStream ("/system.properties");
        loadProperties(fis);
    }

    private  void loadProperties(InputStream fis) throws IOException {
        properties.load(fis);
    }

    public String getServerIp(){
        return properties.getProperty("socket.server.ip");
    }

    public String getServerPort(){
        return properties.getProperty("socket.server.port");
    }

    public String getSendBufferSize(){
        return properties.getProperty("send.buffer.size");
    }
}
