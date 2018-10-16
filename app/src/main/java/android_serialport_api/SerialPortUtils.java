package android_serialport_api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.util.Log;
import com.UTFTool.functionClient.OnDataReceiveListener;

public class SerialPortUtils {
	private boolean isStop = false;
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private ReadThread mReadThread;
	private final String TAG = "SerialPort";
	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public OnDataReceiveListener onDataReceiveListener = null;

//	public static interface OnDataReceiveListener {
//		public void onDataReceive(byte[] buffer, int size);
//	}

	public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}

	public void onCreate() {
		SerialPort mserialPort;
		try {
			mserialPort = new SerialPort(new File("/dev/ttyUSB0"), 9600, 0,8,1,'N');
			mOutputStream = mserialPort.getOutputStream();
			mInputStream = mserialPort.getInputStream();
			// sendSerialPort("1234567890");
			mReadThread = new ReadThread();
			isStop = false;
			Log.i(TAG, "sendSerialPort:ReadThreadstart");
			mReadThread.start();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送获取锁状态指令
	 */
	public void sendSerialPort(){
		Log.d(TAG,"sendSerialPort: 发送获取锁状态");
		try{
			byte [] sendResult = new byte[9];
			byte [] send = new byte[]{(byte) 0xA6,(byte) 0xA8, 0x05, 0x00, 0x00, 0x07, 0x00, 0x01};
			int temp = 0;
			for(int i = 0; i < send.length; i++){
				temp += send[i];
			}
			System.arraycopy(send,0,sendResult,0,8);
			sendResult[8] = (byte) temp;
			System.out.println("状态发送数据：" + Arrays.toString(sendResult));
			if (send.length > 0) {
				mOutputStream.write(sendResult);
				mOutputStream.flush();
				Log.d(TAG, "sendSerialPort: 串口数据发送成功");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * 发送开门指令
	 */
	public void sendSerialPort(int number){
		Log.d(TAG,"sendSerialPort: 发送开锁命令");
		try {
			byte [] sendResult = new byte[13];
			byte [] send = new byte[] {(byte) 0xA6,(byte) 0xA8, 0x05, 0x00, 0x00, 0x0B, 0x00, 0x03};
			int temp = 0;
			for(int i = 0; i < send.length; i++){
				temp += send[i];
			}
			byte [] lockID = getLock(number);
			temp += lockID[0];
			temp += lockID[1];
			temp += lockID[2];
			temp += lockID[3];
			System.arraycopy(send, 0, sendResult, 0, 8);
			System.arraycopy(lockID, 0, sendResult, 8, 4);
			sendResult[12] = (byte) temp;
			if (send.length > 0) {
				mOutputStream.write(sendResult);
				mOutputStream.flush();
				Log.d(TAG, "sendSerialPort: 串口数据发送成功");
			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static byte []  getLock (int number) {
		String str = "00000000000000000000000000000000";
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
		byte[] bytes = new byte[str.length() / 8];
		for(int i = 0; i < str.length() / 8; i++) {
			String subStr = str.substring(i * 8, i * 8 + 8);
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


	public class ReadThread extends Thread {

		int windex = 0;
		int rindex = 0;
		byte[] datacacth = new byte[512];
		int datacacthmaxlength = 512;
		byte[] byteFrameBuf = new byte[512];
		byte byteFrameBufLen = 0x00;
		short sFrameIndex = 0x0000;
		private  int Operation=0;
		private  int o_result=0;

		private Commons comm = new Commons();

		@Override
		public void run() {
			super.run();
			while (!isStop && !isInterrupted()) {
				int size;
				try {
					if (mInputStream == null)
						return;
					byte[] buffer = new byte[512];
					size = mInputStream.read(buffer);
					SetCacthData(buffer, size);
					System.out.println("缓存数据："+Arrays.toString(datacacth));
					while (FormateData()){
						Log.i(TAG, "run: 接收到了数据：" + Arrays.toString(byteFrameBuf));
						Log.i(TAG, "run: 接收到了数据大小：" + String.valueOf(size));
						if (null != onDataReceiveListener) {
							Log.i(TAG, "传值给接口");
							onDataReceiveListener.LockonDataReceive(byteFrameBuf, size);
						}
					}
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}

		private void SetCacthData(byte[] dataFrom, int ilength) {
			int nTmpIndex = 0;
			byte[] byteTmp = null;
			int nTmpLen = 0;
			if (datacacth == null)
				return;
			if (datacacth.length < datacacthmaxlength) {
				byte[] _temp = new byte[datacacth.length];
				System.arraycopy(datacacth, 0, _temp, 0, datacacth.length);
				datacacth = new byte[datacacthmaxlength];
				System.arraycopy(_temp, 0, datacacth, 0, _temp.length);
			}
			if (windex + ilength <= datacacthmaxlength) {
				if (windex >= rindex) {
					byte[] _item = comm.GetSubBytes(dataFrom, 0, ilength);
					System.arraycopy(_item, 0, datacacth, windex, _item.length);
					windex = (windex + ilength) % datacacthmaxlength;
				} else if (windex + ilength < rindex) {
					byte[] _item = comm.GetSubBytes(dataFrom, 0, ilength);
					System.arraycopy(_item, 0, datacacth, windex, _item.length);
					windex = (windex + ilength) % datacacthmaxlength;
				} else {
					nTmpLen = datacacthmaxlength;
					datacacthmaxlength = datacacthmaxlength * 2;// 扩大缓冲容量
					byteTmp = new byte[datacacthmaxlength];
					// comm.GetSubBytes(datacacth, rindex, nTmpLen - rindex).CopyTo(byteTmp, 0);
					// //拷贝数组尾部数据
					byte[] _item0 = comm.GetSubBytes(datacacth, rindex, nTmpLen - rindex);
					System.arraycopy(_item0, 0, byteTmp, 0, _item0.length);
					// comm.GetSubBytes(datacacth, 0, windex).CopyTo(byteTmp, nTmpLen - rindex);
					// //拷贝数组头数据
					byte[] _item1 = comm.GetSubBytes(datacacth, 0, 1);
					System.arraycopy(_item1, 0, byteTmp, nTmpLen - rindex, _item1.length);

					// comm.GetSubBytes(dataFrom, 0, ilength).CopyTo(byteTmp, nTmpLen - rindex +
					// windex); //拷贝新读取到的数据
					byte[] _item2 = comm.GetSubBytes(dataFrom, 0, ilength);
					System.arraycopy(_item2, 0, byteTmp, nTmpLen - rindex + windex, _item2.length);

					windex = (nTmpLen - rindex + windex + ilength) % datacacthmaxlength; // 记录这次写入数据后的索引值
					datacacth = new byte[datacacthmaxlength];
					System.arraycopy(byteTmp, 0, datacacth, 0, byteTmp.length);
					// byteTmp.CopyTo(datacacth, 0);
				}
			} else {
				if (windex >= rindex) {
					if (ilength + windex - datacacthmaxlength <= rindex)// 先存放到数组尾,然后存放到数组头,没有出现覆盖数据 S__ R--W__E
					{
						nTmpIndex = datacacthmaxlength - windex;
						// comm.GetSubBytes(dataFrom, 0, nTmpIndex).CopyTo(datacacth, windex);
						// //将一部分数据存储到数组尾
						byte[] _item0 = comm.GetSubBytes(dataFrom, 0, nTmpIndex);
						System.arraycopy(_item0, 0, datacacth, windex, _item0.length);

						nTmpIndex = ilength - nTmpIndex;
						// comm.GetSubBytes(dataFrom, datacacthmaxlength - windex,
						// nTmpIndex).CopyTo(datacacth, 0);//将剩余的数据放入数组开始的地方
						//System.out.println(dataFrom.length + " " + datacacthmaxlength + " " + windex + " " + nTmpIndex);
						byte[] _item1 = comm.GetSubBytes(dataFrom, datacacthmaxlength - windex, nTmpIndex);
						//System.out.println(datacacth.length + " " + _item1.length);
						System.arraycopy(_item1, 0, datacacth, 0, _item1.length);
						windex = nTmpIndex % datacacthmaxlength;// 记录这次写入数据后的索引值
					} else if (ilength + windex - datacacthmaxlength > rindex)// 先存放到数组尾,然后存放到数组头,出现覆盖数据 S__R=--W__E
					{
						nTmpLen = datacacthmaxlength;
						datacacthmaxlength = datacacthmaxlength * 2;// 扩大缓冲容量
						byteTmp = new byte[datacacthmaxlength];
						// comm.GetSubBytes(datacacth, windex, nTmpLen - windex).CopyTo(byteTmp, 0);
						// //拷贝数组尾部数据
						byte[] _item0 = comm.GetSubBytes(dataFrom, datacacthmaxlength - windex, nTmpIndex);
						System.arraycopy(_item0, 0, byteTmp, 0, _item0.length);

						// comm.GetSubBytes(datacacth, 0, rindex).CopyTo(byteTmp, nTmpLen - windex);
						// //拷贝数组头数据
						byte[] _item1 = comm.GetSubBytes(datacacth, 0, rindex);
						System.arraycopy(_item1, 0, byteTmp, nTmpLen - windex, _item1.length);
						// comm.GetSubBytes(dataFrom, 0, ilength).CopyTo(byteTmp, nTmpLen - windex +
						// rindex); //拷贝新读取到的数据

						byte[] _item2 = comm.GetSubBytes(dataFrom, 0, ilength);
						System.arraycopy(_item2, 0, byteTmp, nTmpLen - windex + rindex, _item2.length);

						windex = nTmpLen - windex + rindex + ilength;// 记录这次写入数据后的索引值
						datacacth = new byte[datacacthmaxlength];
						// byteTmp.CopyTo(datacacth, 0);
						System.arraycopy(byteTmp, 0, datacacth, 0, byteTmp.length);
					} else {

					}
				} else// 先存放到数组尾,然后存放到数组头,出现覆盖数据 S_ W==R__E
				{
					nTmpLen = datacacthmaxlength;
					datacacthmaxlength = datacacthmaxlength * 2;// 扩大缓冲容量
					byteTmp = new byte[datacacthmaxlength];

					// comm.GetSubBytes(datacacth, rindex, nTmpLen - rindex).CopyTo(byteTmp, 0);
					// //拷贝数组尾部数据
					byte[] _item0 = comm.GetSubBytes(datacacth, rindex, nTmpLen - rindex);
					System.arraycopy(_item0, 0, byteTmp, 0, _item0.length);

					// comm.GetSubBytes(datacacth, 0, windex).CopyTo(byteTmp, nTmpLen - rindex);
					// //拷贝数组头数据
					byte[] _item1 = comm.GetSubBytes(datacacth, 0, windex);
					System.arraycopy(_item1, 0, byteTmp, nTmpLen - rindex, _item1.length);

					// comm.GetSubBytes(dataFrom, 0, ilength).CopyTo(byteTmp, nTmpLen - rindex +
					// windex); //拷贝新读取到的数据
					byte[] _item2 = comm.GetSubBytes(dataFrom, 0, ilength);
					System.arraycopy(_item2, 0, byteTmp, nTmpLen - rindex + windex, _item2.length);
					windex = (nTmpLen - rindex + windex + ilength) % datacacthmaxlength; // 记录这次写入数据后的索引值
					datacacth = new byte[datacacthmaxlength];
					// byteTmp.CopyTo(datacacth, 0);
					System.arraycopy(byteTmp, 0, datacacth, 0, byteTmp.length);
				}
			}
		}

		private boolean FormateData() {
			int nFrameHeadIndex = -1;// 帧头的索引
			int nFrameTailIndex = -1;// 帧尾的索引
			if (datacacth == null) {
				return false;
			}
			if (null == byteFrameBuf) {
				return false;
			}
			if (GetDataCacheValidDataSize() < 9)// 有效数据位空小于最小帧长度9
			{
				//System.out.println("有效数据位空小于最小帧长度9");
				return false;
			}
			// 情况1.写入数据索引大于读取数据索引,即正向读取数据g_nWriteIndex>g_nReadIndex
			if (windex > rindex) {
				nFrameHeadIndex = FrameHeadIndex(rindex, windex);// 先找帧头
				if (nFrameHeadIndex != -1)// 找到帧头
				{
					byteFrameBufLen = (byte) ((nFrameHeadIndex + datacacth[nFrameHeadIndex+5] + 1) - nFrameHeadIndex + 1);// 计算帧长度
					byte[] _item = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
					System.arraycopy(_item, 0, byteFrameBuf, 0, _item.length);
					rindex = (nFrameHeadIndex + datacacth[nFrameHeadIndex+5]+2) % datacacthmaxlength;// 下次读取数据
					return true;
				}
				// 没找到帧头
				else {
					rindex = windex;// 下次读取数据
					return false;
				}
			}
			// 情况2.写入数据索引小于读取数据索引,即正向读取数据后反向读取数据,循环读取g_nWriteIndex<=g_nReadIndex
			else {
				// 遍历数组查找帧头和帧尾
				nFrameHeadIndex = FrameHeadIndex(rindex, datacacthmaxlength);// 先查找帧头
				if (nFrameHeadIndex != -1)// 找到帧头
				{
					if(datacacthmaxlength - (nFrameHeadIndex + 1) < 5){
						byteFrameBufLen = (byte) ((nFrameHeadIndex + datacacth[(5- (datacacthmaxlength - (nFrameHeadIndex + 1)))-1] + 1) - nFrameHeadIndex + 1);// 计算帧长度
					}else{
						byteFrameBufLen = (byte) ((nFrameHeadIndex + datacacth[nFrameHeadIndex+5] + 1) - nFrameHeadIndex + 1);// 计算帧长度
					}
					byte[] _item = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
					System.arraycopy(_item, 0, byteFrameBuf, 0, _item.length);
					rindex = (nFrameHeadIndex + datacacth[(5- (datacacthmaxlength - (nFrameHeadIndex + 1)))-1] + 2) % datacacthmaxlength;// 下次读取数据
					return true;
				}
				return false;
			}
		}

		private int FrameHeadIndex(int nStartIndex, int nEndIndex) {
			int i = 0;
			for (i = nStartIndex; i < nEndIndex; ++i)// 先找帧头
			{
				if (datacacth[i] == (byte) 0xA6 && (i + 1 <= nEndIndex))// 含有帧头
				{
					return i;
				}
			}
			return -1;
		}

		private int FrameEndIndex(int nStartIndex, int nEndIndex) {
			int i = 0;
			for (i = nStartIndex; i < nEndIndex; ++i)// 再找帧尾
			{
				if (datacacth[i] == Protocol.FRAME_TAIL1 && (i + 1 < nEndIndex) && datacacth[i + 1] == Protocol.FRAME_TAIL2)// 含有帧尾
				{
					return i + 1;
				}
			}
			return -1;
		}

		private int GetDataCacheValidDataSize() {
			if (windex == rindex)// 读写位置相等,则表示没有数据
			{
				return 0;
			} else if (windex > rindex)// 写入数据位置大于上次读取数据位置,则数据为正向存放
			{
				return windex - rindex;
			} else {
				return this.datacacthmaxlength - rindex + windex;// 写入数据位置小于上次读取数据位置,表示数据从数据尾部存放到数据开始的地方
			}
		}


	}

}
