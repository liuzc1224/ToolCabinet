package com.UTFTool.functionClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

import com.UTFTool.functionUtils.SerialSDK_Commons;

public class SerialClientThread implements Runnable {

	private Socket socket;
	private static SerialSDK_Commons comm = new SerialSDK_Commons();

	private boolean StartInventory = false;
	private boolean CanceInventory = false;

	private final byte[] m_client02 = new byte[] { 0x5A, 0x55, 0x0A, 0x00, 0x00, 0x0D, 0x02, 0x00, 0x00, 0x00, 0x00,
			(byte) 0xC8, 0x6A, 0x69 };
	private final byte[] m_client03 = new byte[] { 0x5A, 0x55, 0x06, 0x00, 0x00, 0x0D, 0x03, (byte) 0xC5, 0x6A, 0x69 };

	private final byte[] m_inventory39 = new byte[] { 0x5A, 0x55, 0x0E, 0x00, 0x00, 0x0D, 0x07, 0x00, 0x00, 0x00, 0x00,
			0x00, 0x00, 0x00, 0x01, (byte) 0xD2, 0x6A, 0x69 };

	private final byte[] m_cancel15 = new byte[] { 0x5A, 0x55, 0x06, 0x00, 0x00, 0x0D, 0x15, (byte) 0xD7, 0x6A, 0x69 };

	private BufferedInputStream bis;
	private DataInputStream dis;
	private BufferedOutputStream os;
	private DataOutputStream dos;
	
	private String WriteRFIDRestult;
	
	
	
	public String getWriteRFIDRestult() {
		return WriteRFIDRestult;
	}

	public void setWriteRFIDRestult(String writeRFIDRestult) {
		WriteRFIDRestult = writeRFIDRestult;
	}

	int windex = 0;
	int rindex = 0;
	byte[] datacacth = new byte[1024];
	int datacacthmaxlength = 1024;
	byte[] byteFrameBuf = new byte[1024];
	byte byteFrameBufLen = 0x00;
	CmdDefine enumTmpRepCmdType = CmdDefine.NO_RECEIVE;
	short sFrameIndex = 0x0000;

	public SerialClientThread(Socket socket) {
		this.socket = socket;
	}

	public OnDataReceiveListener onDataReceiveListener = null;

	public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
		onDataReceiveListener = dataReceiveListener;
	}

	@Override
	public void run() {
		try {
			handlerSocket();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int ReadIndex = 0;
 
	private void handlerSocket() throws IOException { 
		bis = new BufferedInputStream(socket.getInputStream());
		dis = new DataInputStream(bis);
		byte[] bytes = new byte[1024];
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while ((ReadIndex = dis.read(bytes)) != -1) {
			if (dis.available() == 0) {
//				String ret1 = comm.BytesToHexString(bytes); 
//				System.out.println(df.format(new Date())+" "+ret1);
				SetCacthData(bytes, ReadIndex);
//				String ret3 = comm.BytesToHexString(datacacth); 
//				System.out.println("du "+ret3);
				while(FormateData())
				{
					String ret = comm.BytesToHexString(byteFrameBuf); 
					System.out.println(df.format(new Date())+" "+ret); 
					if (CmdDefine.SEND_CONNECT.GetValue() == byteFrameBuf[6])// 连接读写器
					{
						WriteBytesToClientSocket(m_client02);
					}
					if ((byteFrameBuf[6]) == CmdDefine.RECEIVE_KEEPALIVE.GetValue())// 心跳包
					{ 
						WriteBytesToClientSocket(m_client03);
						if (null != onDataReceiveListener)
							onDataReceiveListener.clientUTF(true);
					}
					if ((byteFrameBuf[6]) == CmdDefine.RECEIVE_INVENTORY_REPORT.GetValue())// 接收盘点数据
					{ 
						if (null != onDataReceiveListener)
							onDataReceiveListener.onDataReceive(byteFrameBuf, ReadIndex);
					}
				}
				
			}
		}
	}

	private CmdDefine CmdDefine(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean DismantlingPackage() {
		byte byteFrameLen = byteFrameBufLen;// 整个帧的长度
		CmdDefine byteFrameCmdType = CmdDefine.values()[0x00];// 帧中的指令类型
		byte[] tmpBuf = new byte[400];
		short sResponeFrameIndex = 0;// 响应包中的帧序号
		byte[] byteFrameIndex = new byte[2];
		ByteBuffer buff = ByteBuffer.allocate(400);
		if (null == byteFrameBuf || byteFrameLen <= 0) {
			return false;
		}
		// 删除特殊字0x99
		boolean nRetVal = Del0x99();
		if (!nRetVal)
			return nRetVal;
		// 得到帧序号,第4和第5字节为帧序号
		byte[] item = comm.GetSubBytes(byteFrameBuf, 3, 2);
		System.arraycopy(item, 0, byteFrameIndex, 0, item.length);
		// 由于字节序的问题,需要数组反转
		// Array.Reverse(byteFrameIndex);
		ByteBuffer buf = ByteBuffer.wrap(byteFrameIndex);
		buf.rewind();
		byteFrameIndex = buf.array();
		sResponeFrameIndex = comm.BytesToInt16(byteFrameIndex, 3);
		sFrameIndex = sResponeFrameIndex;
		// 得到指令类型,第7字节为指令类型
		byteFrameCmdType = CmdDefine.values()[byteFrameBuf[6]];
		// 判断指令类型
		if (enumTmpRepCmdType != (CmdDefine.NO_RECEIVE))// NO_RESPONE
		{
			if (byteFrameCmdType != enumTmpRepCmdType) {
				return false;
			}
		} else {
			enumTmpRepCmdType = byteFrameCmdType;// 返回得到的指令类型
		}
		// 得到回复的负载荷,第7字节开始到传入参数sLoadLen的长度为负载荷
		byte[] item1 = comm.GetSubBytes(byteFrameBuf, 7, byteFrameLen - 10);
		System.arraycopy(item1, 0, tmpBuf, 0, item1.length);

		// 清空数据
		comm.ZeroByteArray(byteFrameBuf, byteFrameLen);
		byte[] item2 = comm.GetSubBytes(tmpBuf, 0, byteFrameLen - 10);
		System.arraycopy(item2, 0, byteFrameBuf, 0, item2.length);
		byteFrameBufLen = (byte) (byteFrameLen - 10);
		return true;
	}

	private boolean Del0x99() {
		int i = 0;
		byte byteFrameLen = byteFrameBufLen;
		byte byteCRCIndex = (byte) (byteFrameLen - 3);// 检验和为最后第三个字节
		byte byteCRC32 = 0;// 校验和
		byte[] temBuf = new byte[400];
		int nLastSpecialWordIndex = 0;// 上一次出现特殊字的位置
		int nSpecialWordCnt = 0;// 特殊字的个数
		byte byteRealLength = 0;// 存储帧中第3字节帧长度
		if (null == byteFrameBuf || byteFrameLen <= 0)
			return false;
		// 计算校验和
		for (i = 0; i < byteCRCIndex; ++i)
			byteCRC32 += byteFrameBuf[i];
		// 验证校验和
		if (byteCRC32 != byteFrameBuf[byteCRCIndex])// 校验和不相等
			return false;
		// 从帧长度到校验和检测是否含有0x99
		for (i = 2; i < byteCRCIndex; ++i) {
			if (Protocol.FRAME_SIGN == byteFrameBuf[i])// 为加入的字0x99
			{
				// 下一个字节不为特殊字反码的值，则帧格式有误
				if (byteFrameBuf[i + 1] != (0xFF - Protocol.FRAME_SIGN)
						&& byteFrameBuf[i + 1] != (0xFF - Protocol.FRAME_HEAD1)
						&& byteFrameBuf[i + 1] != (0xFF - Protocol.FRAME_TAIL1)
						&& byteFrameBuf[i + 1] != Protocol.FRAME_TAIL1)/* 校验和也可能会出现0x99 */
				{
					return false;
				}
				byte[] item3 = comm.GetSubBytes(byteFrameBuf, nLastSpecialWordIndex, i - nLastSpecialWordIndex);// 拷贝上次出现特殊字到这次出现特殊字之间的数据
				System.arraycopy(item3, 0, temBuf, nLastSpecialWordIndex - nSpecialWordCnt, item3.length);
				nLastSpecialWordIndex = i + 2;
				temBuf[i - nSpecialWordCnt] = (byte) (0xFF - byteFrameBuf[i + 1]);// 取反码
				++nSpecialWordCnt;
			}
		}
		if (nSpecialWordCnt > 0)// 如果含有特殊字
		{
			byte[] item0 = comm.GetSubBytes(byteFrameBuf, nLastSpecialWordIndex, byteFrameLen - nLastSpecialWordIndex);
			System.arraycopy(item0, 0, temBuf, nLastSpecialWordIndex - nSpecialWordCnt, item0.length);
			// 拷贝上次出现特殊字到帧尾之间的数据
			comm.ZeroByteArray(byteFrameBuf, byteFrameLen);// 清空原来缓冲
			byteFrameBufLen = (byte) (byteFrameLen - nSpecialWordCnt);// 更改新的帧长度
			byte[] item2 = comm.GetSubBytes(temBuf, 0, byteFrameBufLen);
			System.arraycopy(item2, 0, byteFrameBuf, 0, item2.length);// 拷贝处理后的数据
		}
		// 帧头帧尾检验
		if (byteFrameBuf[0] != Protocol.FRAME_HEAD1 || byteFrameBuf[1] != Protocol.FRAME_HEAD2
				|| byteFrameBuf[byteFrameBufLen - 2] != Protocol.FRAME_TAIL1
				|| byteFrameBuf[byteFrameBufLen - 1] != Protocol.FRAME_TAIL2)
			return false;
		// 帧长度检验
		byteRealLength = byteFrameBuf[2];// 得到帧中的帧长度
		if ((byteFrameBufLen - 4) != byteRealLength)// 完整帧减去帧头帧尾4个字节
			return false;
		return true;
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
				nFrameTailIndex = FrameEndIndex(nFrameHeadIndex + 2, windex);// 再找帧尾
				if (nFrameTailIndex != -1 && nFrameTailIndex > nFrameHeadIndex) {
					byteFrameBufLen = (byte) (nFrameTailIndex - nFrameHeadIndex + 1);// 计算帧长度
					byte[] _item = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
					System.arraycopy(_item, 0, byteFrameBuf, 0, _item.length);
					rindex = (nFrameTailIndex + 1) % datacacthmaxlength;// 下次读取数据
					return true;
				}
				// 没找到帧尾
				else {
					return false;
				}
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
				nFrameTailIndex = FrameEndIndex(nFrameHeadIndex + 2, datacacthmaxlength);// 再查找帧尾
				if (nFrameTailIndex != -1)// 找到帧尾
				{
					if (nFrameTailIndex > nFrameHeadIndex) {
						byteFrameBufLen = (byte) (nFrameTailIndex - nFrameHeadIndex + 1);// 计算帧长度
						//System.out.println(nFrameHeadIndex + " " + byteFrameBufLen);
						byte[] _item = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
						System.arraycopy(_item, 0, byteFrameBuf, 0, _item.length);
						rindex = (nFrameTailIndex + 1) % datacacthmaxlength;// 下次读取数据
						return true;
					}
				} else {
					if (datacacth[datacacthmaxlength - 1] == Protocol.FRAME_TAIL1
							&& datacacth[0] == Protocol.FRAME_TAIL2)// 含有帧尾
					{
						byte[] _item = comm.GetSubBytes(datacacth, nFrameHeadIndex,
								datacacthmaxlength - nFrameHeadIndex);
						System.arraycopy(_item, 0, byteFrameBuf, 0, _item.length);
						byteFrameBuf[datacacthmaxlength - nFrameHeadIndex] = Protocol.FRAME_TAIL2;
						byteFrameBufLen = (byte) (datacacthmaxlength - nFrameHeadIndex + 1);// 计算帧长度
						rindex = 1;// 下次读取数据
						return true;
					}

					nFrameTailIndex = FrameEndIndex(0, windex);// 再查找帧尾
					if (nFrameTailIndex != -1)// 找到帧尾
					{
						// 拷贝帧头到数组尾的数据
						byteFrameBufLen = (byte) (datacacthmaxlength - nFrameHeadIndex);// 计算帧长度

						byte[] _item0 = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
						System.arraycopy(_item0, 0, byteFrameBuf, 0, _item0.length); // 拷贝数组头到帧尾的数据

						byte[] _item1 = comm.GetSubBytes(datacacth, 0, nFrameTailIndex + 1);
						System.arraycopy(_item1, 0, byteFrameBuf, byteFrameBufLen, _item1.length);
						byteFrameBufLen = (byte) (byteFrameBufLen + nFrameTailIndex + 1);// 计算帧长度
						rindex = (nFrameTailIndex + 1) % datacacthmaxlength;// 下次读取数据
						return true;
					} else// 找不到帧尾
					{
						return false;
					}
				}
			} else// 从rindex到数组尾没有找到帧头,则从数组头到windex查找帧尾
			{
				// 特殊情况,数组尾为帧头的一部分5A,数组头为帧头的一部分55
				if (datacacth[datacacthmaxlength - 1] == Protocol.FRAME_HEAD1 && datacacth[0] == Protocol.FRAME_HEAD2) {
					nFrameTailIndex = FrameEndIndex(0, windex);// 再查找帧尾
					if (nFrameTailIndex != -1)// 找到帧尾
					{
						byteFrameBuf[0] = Protocol.FRAME_HEAD1;
						// CommonFunctions.GetSubBytes(datacacth, 0, nFrameTailIndex +
						// 1).CopyTo(byteFrameBuf, 1); //拷贝帧数据

						byte[] _item1 = comm.GetSubBytes(datacacth, 0, nFrameTailIndex + 1);
						System.arraycopy(_item1, 0, byteFrameBuf, 1, _item1.length);

						byteFrameBufLen = (byte) (nFrameTailIndex + 2);// 计算帧长度
						rindex = (nFrameTailIndex + 1) % datacacthmaxlength;// 下次读取数据
						return true;
					} else// 没找到帧尾
					{
						return false;
					}
				}

				nFrameHeadIndex = FrameHeadIndex(0, windex);// 先查找帧头
				if (nFrameHeadIndex != -1)// 找到帧头
				{
					nFrameTailIndex = FrameEndIndex(0, windex);// 再查找帧尾
					if (nFrameTailIndex != -1)// 找到帧尾
					{
						byteFrameBufLen = (byte) (nFrameTailIndex - nFrameHeadIndex + 1);// 计算帧长度
						// CommonFunctions.GetSubBytes(datacacth, nFrameHeadIndex,
						// byteFrameBufLen).CopyTo(byteFrameBuf, 0); //拷贝帧数据

						byte[] _item1 = comm.GetSubBytes(datacacth, nFrameHeadIndex, byteFrameBufLen);
						System.arraycopy(_item1, 0, byteFrameBuf, 0, _item1.length);

						rindex = (nFrameTailIndex + 1) % datacacthmaxlength;// 下次读取数据
						return true;
					} else// 没找到帧尾
					{
						return false;
					}
				} else// 没找到帧头
				{
					rindex = windex;// 下次读取数据
					return false;
				}
			}
		}
		return false;
	}

	private int FrameHeadIndex(int nStartIndex, int nEndIndex) {
		int i = 0;
		for (i = nStartIndex; i < nEndIndex; ++i)// 先找帧头
		{
			if (datacacth[i] == Protocol.FRAME_HEAD1 && (i + 1 < nEndIndex) && datacacth[i + 1] == Protocol.FRAME_HEAD2)// 含有帧头
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

	private void WriteBytesToClientSocket(byte[] sendData) throws IOException {
		os = new BufferedOutputStream(socket.getOutputStream());
		dos = new DataOutputStream(os);
		dos.write(sendData);
		dos.flush();
		System.out.println(comm.BytesToHexString(sendData));
	}

	public void setStartInventory() throws IOException {
		StartInventory = true;
		WriteBytesToClientSocket(m_inventory39);
	}

	public void setCanceInventory() throws IOException {
		CanceInventory = true;
		WriteBytesToClientSocket(m_cancel15);
	}
	public void WriteFRID(byte[] data) throws IOException {
		CanceInventory = true;
		WriteBytesToClientSocket(data);
	}
}
