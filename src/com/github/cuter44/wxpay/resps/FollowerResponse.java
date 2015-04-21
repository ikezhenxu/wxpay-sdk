package com.github.cuter44.wxpay.resps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxmpException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kezhenxu on 4/20/15.
 */
public class FollowerResponse extends ResponseBase {

	public static final String ERRCODE = "errcode";
	public static final String ERRMSG  = "errmsg";

	public JSONObject json;

	public FollowerResponse ( String jsonString )
			throws WxmpException {
		try {
			this.json = JSON.parseObject ( jsonString );
		} catch ( Exception ex ) {
			throw ( new IllegalArgumentException ( "Malformed json input:" + jsonString ) );
		}

		Integer errcode = this.getErrcode ();

		if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
			throw ( new WxmpException ( errcode, this.getErrmsg () ) );
		}

		return;
	}

	public int getCount () {
		return json.getInteger ( "total" );
	}

	public int getGroupIdIn() {
		return json.getInteger ( "groupid" );
	}

	public List<String> getAllOpenIds() {
		List<String> strings = new ArrayList<String> ();
		JSONArray objects = json.getJSONArray ( "openid" );
		for ( int ii = 0; ii < objects.size (); ii++ ) {
			strings.add ( objects.getString ( ii ) );
		}
		return strings;
	}
	/**
	 * @return errcode if error occured, otherwise null.
	 */
	public Integer getErrcode () {
		return (
				this.json.getInteger ( ERRCODE )
		);
	}

	public String getErrmsg () {
		return (
				this.json.getString ( ERRMSG )
		);
	}

}
