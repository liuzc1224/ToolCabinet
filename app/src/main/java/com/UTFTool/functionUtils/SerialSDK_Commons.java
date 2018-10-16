package com.UTFTool.functionUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SerialSDK_Commons {

	public static String BytesToHexString(byte[] src) {
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

	public static String BytesHexString(byte[] b) {
		String ret = "";
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ret += hex.toUpperCase();
		}
		return ret;
	}

	public static byte[] GetSubBytes(byte[] byteSrc, int nIndex, int nLength) {
		byte[] byteResult = new byte[nLength];
		for (int i = 0; i < nLength; i++) {
			if ((i + nIndex) < byteSrc.length) {
				byteResult[i] = byteSrc[i + nIndex];
			} else {
				break;
			}
		}
		return byteResult;
	}

	public static short BytesToInt16(byte[] byteSrc, int nStartIndex) {
		// System.out.println(byteSrc.length+" "+ nStartIndex);
		if (byteSrc != null && byteSrc.length - nStartIndex >= 2) {
			byte[] byTmp = new byte[2];
			byte[] item = GetSubBytes(byteSrc, nStartIndex, 2);
			System.arraycopy(item, 0, byTmp, 0, item.length);
			// 转成低字节序
			ByteBuffer buf = ByteBuffer.wrap(byTmp);
			buf.rewind();
			// Array.Reverse(byTmp, 0, 2);
			return byteToShort(byTmp);
		} else {
			return 0;
		}
	}

	public static short byteToShort(byte[] b) {
		short s = 0;
		short s0 = (short) (b[0] & 0xff);// 最低位
		short s1 = (short) (b[1] & 0xff);
		s1 <<= 8;
		s = (short) (s0 | s1);
		return s;
	}

	public static void ZeroByteArray(byte[] byteSrc, int nArraySize) {
		for (int i = 0; i < nArraySize; ++i) {
			byteSrc[i] = 0;
		}
	}

	public static byte[] StringtoBytes(String str) {
		if (str == null || str.trim().equals("")) {
			return new byte[0];
		}

		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length() / 2; i++) {
			String subStr = str.substring(i * 2, i * 2 + 2);
			bytes[i] = (byte) Integer.parseInt(subStr, 16);
		}
		return bytes;
	}

	public static byte[] HexString2Bytes(String strHexData, boolean bIsMinValueOne) {
		int i = 0;
		int nTmpLength = strHexData.length() / 2;
		char szHigh, szLow;
		int nHigh = 0, nLow = 0;

		if (strHexData == "" || strHexData == null) {
			return null;
		}
		byte[] arrbyteResult = new byte[nTmpLength];
		char[] arrszHex = strHexData.toCharArray();
		for (i = 0; i < nTmpLength; ++i) {
			szHigh = arrszHex[2 * i];
			szLow = arrszHex[2 * i + 1];
			// 高位
			nHigh = HexChar2Int(szHigh);
			// 低位
			nLow = HexChar2Int(szLow);
			arrbyteResult[i] = (byte) (nHigh * 16 + nLow);
			// 最小值为1,为了避免0使得字符串结束
			if (bIsMinValueOne) {
				arrbyteResult[i] = (byte) arrbyteResult[i] == (byte) 0 ? (byte) 1 : arrbyteResult[i];
			}
		}
		return arrbyteResult;
	}

	public static int HexChar2Int(char szHex) {
		int nData = 0;
		if (szHex >= 'a' && szHex <= 'f') {
			nData = szHex - 'a' + 10;
		} else if (szHex >= 'A' && szHex <= 'F') {
			nData = szHex - 'A' + 10;
		} else {
			nData = szHex - '0';
		}
		return nData;
	}

 
	public static String strTo16(String s) {
	    String str = "";
	    for (int i = 0; i < s.length(); i++) {
	        int ch = (int) s.charAt(i);
	        String s4 = Integer.toHexString(ch);
	        str = str + s4;
	    }
	    return str;
	}
	public static byte[] intToByteArray(int a) {   
		return new byte[] {   
		        (byte) ((a >> 24) & 0xFF),   
		        (byte) ((a >> 16) & 0xFF),      
		        (byte) ((a >> 8) & 0xFF),      
		        (byte) (a & 0xFF)   
		    };   
		}
	public static byte[] shortToByteArray(int s) {   
		return new byte[] {   
				 (byte) ((s >> 8)& 0xFF),
				  (byte) ((s >> 0)& 0xFF)
		    };   
		}
}
