package com.github.cuter44.wxpay.message.resp;

import com.github.cuter44.wxpay.WxmpException;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;

/**
 * Created by kezhenxu on 4/17/15.
 */
public class MsgResponse extends WxmpResponseBase{

	public static final String MSD_ID = "msg_id";
	public static final String MSG_STATUS = "msg_status";

	public MsgResponse ( String jsonString ) throws WxmpException {
		super ( jsonString );
	}

	public String getMsdId () {
		return getProperty ( MSD_ID );
	}

	public String getMsgStatus () {
		return getProperty ( MSG_STATUS );
	}
}
