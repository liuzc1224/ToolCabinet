package com.UTFTool.functionClient;

public class WriteDdata {
	public String m_strAccessPassword;                 //访问密码
    public int m_nAntennaNo ;                           //天线号
    public String m_strTargetTag ;                      //目标标签
    public int m_enumBankType;              //存储区
    public int m_nOffset;                          //起始偏移量
    public int m_nLength;                          //操作长度
    public boolean m_bEncryptOrDecrypt;               //是否进行加密与解密操作
    public boolean m_bSignatureOrCheck;               //是否进行签名或验签名操作
    public String m_strWriteData;                  //要写入的数据
    public byte[] m_bytes ;
}
