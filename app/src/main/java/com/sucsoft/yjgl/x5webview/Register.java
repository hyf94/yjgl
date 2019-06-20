package com.sucsoft.yjgl.x5webview;


import com.sucsoft.yjgl.core.jsbridge.JSBridge;
import com.sucsoft.yjgl.util.TestImpl;
import com.sucsoft.yjgl.util.gqt.GQTImpl;

public class Register {

    public static void regist () {

        /***
         * 注册JSBridge暴露给网页的名称和类
         * exposedName不能为JS的关键字，全局方法名，window. 方法名
         */
        JSBridge.register("test", TestImpl.class);
        JSBridge.register("gqt", GQTImpl.class);
    }

}
