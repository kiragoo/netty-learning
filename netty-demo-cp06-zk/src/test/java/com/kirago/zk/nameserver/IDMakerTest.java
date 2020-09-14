package com.kirago.zk.nameserver;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class IDMakerTest {

    @Test
    public void testMakeId() {
        IDMaker idMaker = new IDMaker();
        idMaker.init();
        String nodeName = "/test/IDMaker/ID-";
        for(int i=0;i<10;i++){
            String id = idMaker.makeId(nodeName);
            log.info(id);
        }
        idMaker.destroy();
    }
}