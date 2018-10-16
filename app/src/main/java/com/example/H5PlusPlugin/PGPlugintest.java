package com.example.H5PlusPlugin;
import android_serialport_api.SerialPortUtils;
import com.UTFTool.functionClient.OnDataReceiveListener;
import com.UTFTool.functionClient.SerialSDK_Client;
import com.UTFTool.functionUtils.SerialSDK_Commons;
import com.iflytek.thirdparty.P;
//import com.wbl.utils.GetSerialPortReturnData;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.DHInterface.StandardFeature;
import io.dcloud.common.util.JSUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 5+ SDK 扩展插件示例
 * 5+ 扩扎插件在使用时需要以下两个地方进行配置
 * 		1  WebApp的mainfest.json文件的permissions节点下添加JS标识
 * 		2  assets/data/properties.xml文件添加JS标识和原生类的对应关系
 * 本插件对应的JS文件在 assets/apps/H5Plugin/js/test.js
 * 本插件对应的使用的HTML assest/apps/H5plugin/index.html
 * 
 * 更详细说明请参考文档http://ask.dcloud.net.cn/article/66
 * **/
public class PGPlugintest extends StandardFeature implements OnDataReceiveListener
{

    private SerialSDK_Client sdk;
    private SerialSDK_Commons comm = new SerialSDK_Commons();
    public void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                sdk = new SerialSDK_Client();
                sdk.onCreate();
            }
        }).start();
    }

    public void functionRFID(){
        try {
            sdk.setOnDataReceiveListener(this);
            sdk.setStartInventory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    int a=0;
    String zt;
    List<String> EPC=new ArrayList<>();
    public void onStart(Context pContext, Bundle pSavedInstanceState, String[] pRuntimeArgs) {
        
        /**
         * 如果需要在应用启动时进行初始化，可以继承这个方法，并在properties.xml文件的service节点添加扩展插件的注册即可触发onStart方法
         * */
    }
    public void LinkFunction(IWebview pWebview, JSONArray array)
    {
        String CallBackID = array.optString(0);
        JSONArray newArray1 = new JSONArray();
        newArray1.put(array.optString(1));
        newArray1.put(array.optString(2));
        newArray1.put(array.optString(3));
        newArray1.put(array.optString(4));
        System.out.println("45156416514");
        test();
        System.out.println("45156416514");
        JSUtil.execCallback(pWebview, CallBackID, newArray1, JSUtil.OK, false);
    }
    public void PluginTestFunction(IWebview pWebview, JSONArray array)//开锁
    {
        int num=8;
        a=0;
        SerialPortUtils utils = new SerialPortUtils();
//        GetSerialPortReturnData data = new GetSerialPortReturnData();
        utils.setOnDataReceiveListener(this);
        utils.onCreate();
//        int num=Integer.valueOf(array.optString(4));
        try {
            utils.sendSerialPort();
            Thread.sleep(1000);
            utils.sendSerialPort(num);
            Thread.sleep(1000);
            String CallBackID = array.optString(0);
            JSONArray newArray = new JSONArray();
            newArray.put(array.optString(1));
            newArray.put(array.optString(2));
            newArray.put(array.optString(3));
            newArray.put(a);
            // 调用方法将原生代码的执行结果返回给js层并触发相应的JS层回调函数
            JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // 原生代码中获取JS层传递的参数，
    	// 参数的获取顺序与JS层传递的顺序一致

//        Integer.valueOf(array.optString(4))
    }

    public void CloseFunction(IWebview pWebview, JSONArray array){//关锁
        SerialPortUtils utils = new SerialPortUtils();
//        GetSerialPortReturnData data = new GetSerialPortReturnData();
        utils.setOnDataReceiveListener(this);
        utils.onCreate();
        Boolean result=false;
        zt="";
        while(!result) {
            try {
                Thread.sleep(1000); //设置暂停的时间 1 秒
                utils.sendSerialPort();
                System.out.println(zt);
                if (zt.equals("00000000")) {
                    result=true;
                    a=4;
                    String CallBackID = array.optString(0);
                    JSONArray newArray1 = new JSONArray();
                    newArray1.put(array.optString(1));
                    newArray1.put(array.optString(2));
                    newArray1.put(array.optString(3));
                    newArray1.put(a);
                    System.out.println("99999999999999999999999999999999");
                    System.out.println(a);
                    EPC = new ArrayList<>();
                    Thread.sleep(5000);
                    functionRFID();
                    Thread.sleep(1000);
                    JSONArray newArray = new JSONArray();
                    for( int i = 0 ; i < EPC.size() ; i++) {
                        newArray.put(EPC.get(i));
                    }
                    System.out.println("4544641654");
                    System.out.println(newArray);
                    JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);
//                    JSUtil.execCallback(pWebview, CallBackID, newArray1, JSUtil.OK, false);
                    break ;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void CountingFunction(IWebview pWebview, JSONArray array){//盘点
        try {
            EPC = new ArrayList<>();
            Thread.sleep(5000);
            functionRFID();
            Thread.sleep(1000);
            String CallBackID = array.optString(0);
            Thread.sleep(3000);
            JSONArray newArray = new JSONArray();
            for( int i = 0 ; i < EPC.size() ; i++) {
                newArray.put(EPC.get(i));
            }
            System.out.println("4544641654");
            System.out.println(newArray);
            JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void PluginTestFunctionArrayArgu(IWebview pWebview, JSONArray array)
    {
        String ReturnString = null;
        String CallBackID =  array.optString(0);
        JSONArray newArray = null;
        try {

            newArray = new JSONArray( array.optString(1));          
            String inValue1 = newArray.getString(0);
            String inValue2 = newArray.getString(1);
            String inValue3 = newArray.getString(2);
            String inValue4 = newArray.getString(3);
            ReturnString = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        JSUtil.execCallback(pWebview, CallBackID, ReturnString, JSUtil.OK, false);
    }

    public String PluginTestFunctionSyncArrayArgu(IWebview pWebview, JSONArray array)
    {
//
        SerialPortUtils utils = new SerialPortUtils();
//        GetSerialPortReturnData data = new GetSerialPortReturnData();
        utils.setOnDataReceiveListener(this);
        utils.onCreate();
        Boolean result=false;
        zt="";
        JSONArray newArray = new JSONArray();
        while(!result) {
            try {
                Thread.sleep(1000); //设置暂停的时间 1 秒
                utils.sendSerialPort();
                System.out.println(zt);
                if (zt.equals("00000000")) {
                    result = true;
                    a = 4;
                    String CallBackID = array.optString(0);
                    JSONArray newArray1 = new JSONArray();
                    newArray1.put(array.optString(1));
                    newArray1.put(array.optString(2));
                    newArray1.put(array.optString(3));
                    newArray1.put(a);
                    System.out.println("99999999999999999999999999999999");
                    System.out.println(a);
                    EPC = new ArrayList<>();
                    Thread.sleep(5000);
                    functionRFID();
                    Thread.sleep(1000);
                    Thread.sleep(3000);
                    for (int i = 0; i < EPC.size(); i++) {
                        newArray.put(EPC.get(i));
                    }
                    System.out.println("4544641654");
                    System.out.println(newArray);
                    JSUtil.execCallback(pWebview, CallBackID, newArray, JSUtil.OK, false);
//                    JSUtil.execCallback(pWebview, CallBackID, newArray1, JSUtil.OK, false);
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return JSUtil.wrapJsVar(newArray);
    }

    public String PluginTestFunctionSync(IWebview pWebview, JSONArray array)
    {

        String inValue1 = array.optString(0);
        String inValue2 = array.optString(1);
        String inValue3 = array.optString(2);
        String inValue4 = array.optString(3);

        String ReturnValue = inValue1 + "-" + inValue2 + "-" + inValue3 + "-" + inValue4;
        // 只能返回String类型到JS层。
        return JSUtil.wrapJsVar(ReturnValue,true);
    }

    //是否连接上读写器
    @Override
    public void clientUTF(boolean flag) {
        System.out.println("进来");
//        try {
//            sdk.setStartInventory();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    //读写器返回
    @Override
    public void onDataReceive(byte[] buffer, int size) {
        String strRFID = comm.BytesToHexString(buffer);
        byte RFIDlen [] = new byte [2];
        System.arraycopy(buffer, 29, RFIDlen, 0, 2);

        int num = Integer.parseInt(String.valueOf(RFIDlen[1]));
        byte RFIDNumber [] = new byte[num];
        System.arraycopy(buffer, 31, RFIDNumber, 0, num);
        String aa= comm.BytesToHexString(RFIDNumber);
        System.out.println("uii:"+ comm.BytesToHexString(RFIDNumber));
        System.out.println("aaaaaaaaa:"+ a);
        a++;
        if (!EPC.contains(aa)) {
            EPC.add(aa);
        }
        try{
            Thread.sleep(1000);
            sdk.setCanceInventory();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    //锁返回
    @Override
    public void LockonDataReceive(byte[] buffer, int size) {
        System.out.println("接口返回数据："+ Arrays.toString(buffer));
        byte [] newBuffer = new byte[buffer[5] + 2];
        System.arraycopy(buffer, 0, newBuffer, 0, 13);
        if(newBuffer[7] == 0x02 ){
            byte [] lockState = new byte [4];
            System.arraycopy(newBuffer,8,lockState,0,4);
            String strByte = BytesToHexString(lockState);
            zt=strByte;
            System.out.println("返回锁状态: " + strByte);
            if(strByte.equals("00000000")){
                System.out.println("全部关闭");
            }else{
                short s = Short.parseShort(strByte,16);
                String hexResult = get32BitBinString(s);
                System.out.println(hexResult);
                Matcher mat= Pattern.compile("1").matcher(hexResult);
                int count=0;
                while(mat.find()){
                    count++;
                }
                System.out.println("出现次数："+ count);
                String result =new StringBuilder(hexResult).reverse().toString();
                int [] array = new int[count];
                if(count == 1){
                    array[0] = result.indexOf("1") + 1;
                }else{
                    for(int i = 0; i< count; i++){
                        array[i] = result.indexOf("1",result.indexOf("1")+i) + 1;
                    }
                }
                zt=Arrays.toString(array);
                System.out.println("最终未关闭锁状态编号："+ Arrays.toString(array));
            }
        }
        if(newBuffer[7] == 0x04 ){
            System.out.println("回复开门指令");
            if(newBuffer[8] == 0x00 && newBuffer[9] == 0x00 && newBuffer[10] == 0x00 && newBuffer[11] == 0x00){
                a=1;
                System.out.println("开锁成功");
            }else{
                a=2;
                System.out.println("开锁失败");
            }
        }
        System.out.println(a);
    }



    public static  String get32BitBinString(short number) {
        StringBuilder sBuilder = new StringBuilder();
        for (int i = 0; i < 16; i++){
            sBuilder.append(number & 1);
            number = (short) (number >>> 1);
        }
        return sBuilder.reverse().toString();
    }

    public String BytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
    public static byte []  getLock (int number) {
        String str = "0000000000000000";
        StringBuilder sb = new StringBuilder(str);
        sb.replace(number-1, number, "1");
        String result = sb.reverse().toString();
        return hexStringToByte16(result);
    }
    public static byte [] hexStringToByte16(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }
        char[] achar = str.toCharArray();
        byte[] bytes = new byte[str.length() / 4];
        for(int i = 0; i < str.length() / 4; i++) {
            String subStr = str.substring(i * 4, i * 4 + 4);
            bytes[i] = (byte) Integer.parseInt(subStr, 2);
        }
        return bytes;
    }

    private byte [] getLockID(int number){
        byte [] lock = new byte [] {};
        switch (number){
            case 1:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 2:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 3:
                return lock = new byte [] { 0x00,0x00,0x00,0x05 };
            case 4:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 5:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 6:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 7:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 8:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 9:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 10:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 11:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 12:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 13:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 14:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 15:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            case 16:
                return lock = new byte [] { 0x00,0x00,0x00,0x01 };
            default:
                return lock;
        }
    }
}