package com.UTFTool.functionClient;

import com.UTFTool.functionUtils.SerialSDK_Commons;

public class Protocol {
	/// <summary>
    /// 填充字        
    /// </summary>
	public static byte FRAME_SIGN = (byte) 0x99;
    /// <summary>
    /// 帧头1
    /// </summary>
    public static byte FRAME_HEAD1 = 0x5A;
    /// <summary>
    /// 帧头2
    /// </summary>
    public static byte FRAME_HEAD2 = 0x55;
    /// <summary>
    /// 帧尾1
    /// </summary>
    public static byte FRAME_TAIL1 = 0x6A;
    /// <summary>
    /// 帧尾
    /// </summary>
    public static byte FRAME_TAIL2 = 0x69;
    /// <summary>
    /// 端口号
    /// </summary>
    public static byte PROTOCOL_PORT = 0x0D;
    /// <summary>
    /// 负载荷起始位置索引                 
    /// </summary>
    public static int FRAME_LOAD_INDEX = 7;
    
    private static SerialSDK_Commons comm = new SerialSDK_Commons();
    public static boolean MakePackage( byte[] refbyteFrameLoadBuf,  byte refbyteFrameLoadLen, CmdDefine szCmdType, short sFrameIndex)
    {
        //校验和
        byte byteCRC32 = 0;
        int i = 0, tmp = 0;

        if (null == refbyteFrameLoadBuf)
        {
            return false;
        } 
        //第1和第2字节为帧头0x5A55
        refbyteFrameLoadBuf[0] = FRAME_HEAD1;
        refbyteFrameLoadBuf[1] = FRAME_HEAD2;
        //除负载荷外第9和第10字节为帧尾0x6A69
        refbyteFrameLoadBuf[8 + refbyteFrameLoadLen] = FRAME_TAIL1;
        refbyteFrameLoadBuf[9 + refbyteFrameLoadLen] = FRAME_TAIL2; 
        //第3字节为帧长度	1个字节
        refbyteFrameLoadLen += 6;//帧长度=帧负载长度+6个字节(帧长度1、帧序号2、端口1、指令类型1、校验和1)
        refbyteFrameLoadBuf[2] = refbyteFrameLoadLen; 
        //第4和第5字节为帧序号 暂时无意
        refbyteFrameLoadBuf[3] = (byte)(sFrameIndex & 0xFF00);
        refbyteFrameLoadBuf[4] = (byte)(sFrameIndex & 0x00FF); 
        //第6个字节为端口号
        refbyteFrameLoadBuf[5] = PROTOCOL_PORT; 
        //第7字节为指令类型
        refbyteFrameLoadBuf[6] = (byte)szCmdType.GetValue(); 
        //第8个字节开始为帧负载为输入参数pszFrameLoadBuf自身 
        //加入帧头帧尾长度，为整个帧的长度
        refbyteFrameLoadLen += 4;
        //计算校验和,从帧头到校验和之前累加
        tmp = refbyteFrameLoadLen - 3;
        for (i = 0; i < tmp; ++i)
        {
            byteCRC32 += refbyteFrameLoadBuf[i];
        }
        //倒数第3个字节为校验和
        refbyteFrameLoadBuf[tmp] = byteCRC32;

        //处理特殊字5A,99,6A作加0x99处理
        return Add0x99( refbyteFrameLoadBuf,  refbyteFrameLoadLen);
    }
    public static boolean Add0x99( byte[] refbyteFrameBuf,  byte refbyteFrameLen)
    {
        int i = 0;
        byte byteFrameLen = refbyteFrameLen;
        byte byteCRCIndex = (byte)(byteFrameLen - 3);//检验和为最后第三个字节
        byte[] temBuf = new byte[400];
        int nLastSpecialWordIndex = 0;//上一次出现特殊字的位置 
        int nSpecialWordCnt = 0;//特殊字的个数	
        byte byteCRC32 = 0;//校验和
        if (null == refbyteFrameBuf)
        {
            return false;
        }
        //从帧长度到校验和检测是否含有特殊字
        for (i = 2; i < byteCRCIndex; ++i)
        {
            if (true == IsSpecialWord(refbyteFrameBuf[i]))//为特殊字
            {
               byte[] item= comm.GetSubBytes(refbyteFrameBuf, nLastSpecialWordIndex, i - nLastSpecialWordIndex);
               System.arraycopy(item, 0, temBuf, nLastSpecialWordIndex + nSpecialWordCnt, item.length);
              
                nLastSpecialWordIndex = i + 1;
                temBuf[i + nSpecialWordCnt] = FRAME_SIGN;
                temBuf[i + 1 + nSpecialWordCnt] = (byte)(0xFF - refbyteFrameBuf[i]);//取反码
                ++nSpecialWordCnt;
            }
        }
        if (nSpecialWordCnt > 0)//如果含有特殊字
        {
           byte[] item0= comm.GetSubBytes(refbyteFrameBuf, nLastSpecialWordIndex, byteFrameLen - nLastSpecialWordIndex);
           System.arraycopy(item0, 0, temBuf, nLastSpecialWordIndex + nSpecialWordCnt, item0.length);
                           
            comm.ZeroByteArray(refbyteFrameBuf, byteFrameLen);//清空原来缓冲		      
            refbyteFrameLen = (byte)(byteFrameLen + nSpecialWordCnt);//更改新的帧长度
           byte[] item1= comm.GetSubBytes(temBuf, 0, refbyteFrameLen);
           System.arraycopy(item1, 0, refbyteFrameBuf, 0, item1.length);
		       
                                                                                               //重新计算校验和
            byteCRCIndex = (byte)(refbyteFrameLen - 3);
            for (i = 0; i < byteCRCIndex; ++i)
            {
                byteCRC32 += refbyteFrameBuf[i];
            }
            //倒数第3个字节为校验和
            refbyteFrameBuf[byteCRCIndex] = byteCRC32;
        }
        return true;
    }
    private static boolean IsSpecialWord(byte data)
    {
        if (FRAME_HEAD1 == data || FRAME_SIGN == data || FRAME_TAIL1 == data)
        {
            return true;
        }
        return false;
    }
}
