package com.UTFTool.functionClient;

public interface OnDataReceiveListener {
	void onDataReceive(byte[] buffer, int size);

	void LockonDataReceive(byte[] buffer, int size);

	void clientUTF(boolean flag);
}
