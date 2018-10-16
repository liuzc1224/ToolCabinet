//package com.wbl.utils;
//
//import android_serialport_api.SerialPortUtils;
//
//import java.util.Arrays;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class GetSerialPortReturnData implements SerialPortUtils.OnDataReceiveListener {
//
//    @Override
//    public void onDataReceive(byte[] buffer, int size) {
//        System.out.println("接口返回数据："+ Arrays.toString(buffer));
//        byte [] newBuffer = new byte[buffer[5] + 2];
//        System.arraycopy(buffer, 0, newBuffer, 0, 13);
//        if(newBuffer[7] == 0x02 ){
//            byte [] lockState = new byte [4];
//            System.arraycopy(newBuffer,8,lockState,0,4);
//            String strByte = BytesToHexString(lockState);
//            System.out.println("返回锁状态: " + strByte);
//            if(strByte.equals("00000000")){
//                System.out.println("全部关闭");
//            }else{
//                short s = Short.parseShort(strByte,16);
//                String hexResult = get32BitBinString(s);
//                System.out.println(hexResult);
//                Matcher mat= Pattern.compile("1").matcher(hexResult);
//                int count=0;
//                while(mat.find()){
//                    count++;
//                }
//                System.out.println("出现次数："+ count);
//                String result =new StringBuilder(hexResult).reverse().toString();
//                int [] array = new int[count];
//                if(count == 1){
//                    array[0] = result.indexOf("1") + 1;
//                }else{
//                    for(int i = 0; i< count; i++){
//                        array[i] = result.indexOf("1",result.indexOf("1")+i) + 1;
//                    }
//                }
//                System.out.println("最终未关闭锁状态编号："+ Arrays.toString(array));
//            }
//        }
//        if(newBuffer[7] == 0x04 ){
//            System.out.println("回复开门指令");
//            if(newBuffer[8] == 0x00 && newBuffer[9] == 0x00 && newBuffer[10] == 0x00 && newBuffer[11] == 0x00){
//                System.out.println("开锁成功");
//            }else{
//                System.out.println("开锁失败");
//            }
//        }
//    }
//
//    public static  String get32BitBinString(short number) {
//        StringBuilder sBuilder = new StringBuilder();
//        for (int i = 0; i < 16; i++){
//            sBuilder.append(number & 1);
//            number = (short) (number >>> 1);
//        }
//        return sBuilder.reverse().toString();
//    }
//
//    public String BytesToHexString(byte[] src) {
//        StringBuilder stringBuilder = new StringBuilder("");
//        if (src == null || src.length <= 0) {
//            return null;
//        }
//        for (int i = 0; i < src.length; i++) {
//            int v = src[i] & 0xFF;
//            String hv = Integer.toHexString(v);
//            if (hv.length() < 2) {
//                stringBuilder.append(0);
//            }
//            stringBuilder.append(hv);
//        }
//        return stringBuilder.toString();
//    }
//
//}
