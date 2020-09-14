package com.kirago.netty.im.server.session;

import com.kirago.netty.im.common.entity.ImNode;
import com.kirago.netty.im.server.distributed.PeerManager;
import com.kirago.netty.im.server.distributed.PeerSender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class RemoteSession implements ServerSession, Serializable {

    private static final long serialVersionUID = 8072529593455810988L;
    
    private String userId;
    private String sessionId;
    private ImNode imNode;
    private boolean valid= true;

    public RemoteSession() {
    }

    public RemoteSession(
            String sessionId, String userId, ImNode imNode) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.imNode = imNode;
    }

    @Override
    public void writeAndFlush(Object pkg) {

        long nodeId = imNode.getId();
        PeerSender sender =
                PeerManager.getInst().getPeerSender(nodeId);

        sender.writeAndFlush(pkg);
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

}


