package cp02.echoserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class NioDemoConfig {

    private FileInputStream fis;
    private Properties properties = new Properties();

    public NioDemoConfig() throws IOException {
        this.fis = new FileInputStream("/system.properties");
        loadProperties(fis);
    }

    private  void loadProperties(FileInputStream fis) throws IOException {
        properties.load(fis);
    }

    public String getServerIp(){
        return properties.getProperty("socket.server.ip");
    }

    public String getServerPort(){
        return properties.getProperty("socket.server.port");
    }
}
