package com.example.wblpc1.jdbc_oracle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android_serialport_api.SerialPortUtils;
import com.HBuilder.integrate.R;
//import com.wbl.utils.GetSerialPortReturnData;
import io.dcloud.common.DHInterface.IWebview;
import io.dcloud.common.util.JSUtil;
import org.json.JSONArray;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SerialPortUtils utils = new SerialPortUtils();
//        GetSerialPortReturnData data = new GetSerialPortReturnData();
//        utils.setOnDataReceiveListener(data);
        utils.onCreate();

//        utils.sendSerialPort();
//        try{
//            for (int i = 0; i < 100; i++) {

//        try {
//            utils.sendSerialPort();
//            Thread.sleep(1000);
//            utils.sendSerialPort(1);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

//                Thread.sleep(1000);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
