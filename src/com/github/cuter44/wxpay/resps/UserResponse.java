package com.github.cuter44.wxpay.resps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxmpException;

/**
 * Created by kezhenxu on 4/19/15.
 */
public class UserResponse extends ResponseBase {

	public static final String MSD_ID     = "msg_id";
	public static final String MSG_STATUS = "msg_status";

	public static final String ERRCODE = "errcode";
	public static final String ERRMSG  = "errmsg";

	public static final String GROUP  = "group";
	public static final String GROUPS = "groups";
	public static final String ID     = "id";
	public static final String NAME   = "name";
	public static final String TOTAL  = "total";
	protected JSONObject jsonObject;

	public UserResponse ( String jsonString ) {
		try {
			this.jsonObject = JSON.parseObject ( jsonString );
		} catch ( Exception ex ) {
			throw ( new IllegalArgumentException ( "Malformed json input:" + jsonString ) );
		}

		Integer errcode = this.getErrcode ();

		if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
			throw ( new WxmpException ( errcode, this.getErrmsg () ) );
		}

		return;
	}

	/**
	 * @return errcode if error occured, otherwise null.
	 */
	public Integer getErrcode () {
		return (
				jsonObject.getInteger ( ERRCODE )
		);
	}

	public String getErrmsg () {
		return (
				jsonObject.getString ( ERRMSG )
		);
	}

	public int getTotalCount () {
		return jsonObject.getInteger ( TOTAL );
	}
}
