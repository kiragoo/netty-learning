package com.kirago.imClient.command;

import java.util.Scanner;

/**
* @Description:    命令行接口行为约束
* @Author:         kirago
* @CreateDate:     2020/8/7 10:32 下午
* @UpdateRemark:   修改内容
* @Version:        1.0
*/
public interface BaseCommand {
    
    /**
    * @Description: java方法描述
    * @Param: Scanner
    * @return: void
    **/
    void exec(Scanner scanner);
    
    /**
    * @Description: java方法描述
    * @Param: null
    * @return: String
    **/
    String getKey();
    
    /**
    * @Description: Tip 提示
    * @Param: null
    * @return: String
    **/
    String getTip();
}
