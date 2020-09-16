package com.kirago.netty.im.common.entity.PT;

import com.kirago.netty.im.common.protocol.Proto3Msg;
import lombok.Data;

import java.io.Serializable;

/**
 * protobuf traction entity
 */
@Data
public class UserPT implements Serializable {
    
    private static final long serialVersionUID = -5055505421922032773L;
    
    private String userId;
    
    private String username;
    
    private String token;
    
    private String devId;
    
    private String sessionId;
    
    Platform platform = Platform.OTHER;
    
    public enum Platform{
        WINDOWS, MAC, ANDROID, IOS, WEB, OTHER;
    }
    
    public void setPlatform(int platform){
        Platform[] platforms = Platform.values();
        for(int i=0;i<platforms.length;i++){
            if(platforms[i].ordinal() == platform){
                this.platform = platforms[i];
            }
        }
    }


    @Override
    public String toString() {
        return "User{" +
                "uid='" + userId + '\'' +
                ", devId='" + devId + '\'' +
                ", token='" + token + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
    
    public static UserPT fromMsg(Proto3Msg.ProtoMsg.LoginRequest info) {
        UserPT user = new UserPT();
        user.userId = info.getUid();
        user.devId = info.getDeviceId();
        user.token = info.getToken();
        user.setPlatform(info.getPlatform());
        return user;
    }
    
}
