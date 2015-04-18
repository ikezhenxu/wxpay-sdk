package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxmpException;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class UserGroupRequest extends GroupRequestBase {

	public static final String ERRCODE = "errcode";
	public static final String ERRMSG  = "errmsg";

	public static final String GROUPID = "groupid";

	public static final String URL_API_BASE = "https://api.weixin.qq.com/cgi-bin/groups/";

	protected String accessToken;

	public UserGroupRequest ( Properties aConf ) {
		super ( aConf );
	}

	@Override
	public RequestBase build () {
		return this;
	}

	// Do NOT need sign in sending message
	@Override
	public RequestBase sign () throws UnsupportedEncodingException, UnsupportedOperationException, IllegalStateException {
		return this;
	}

	@Override
	public String toURL () throws UnsupportedOperationException {
		return null;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return null;
	}

	/**
	 * 获取用户所在的分组id
	 * <br>查询用户所在分组
	 * <br>通过用户的OpenID查询其所在的GroupID。
	 *
	 * @param openId 用户的openId
	 * @return 用户所在的分组id
	 */
	public String executeGetId ( String openId ) {
		try {
			String data = "{\"openid\":\"" + openId + "\"}";
			String responseJson = new String (
					Request.Post ( URL_API_BASE + "getid?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			JSONObject json;
			json = JSON.parseObject ( responseJson );
			Integer errcode = json.getInteger ( ERRCODE );

			if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
				throw ( new WxmpException ( errcode, json.getString ( ERRMSG ) ) );
			}
			return json.getString ( GROUPID );
		} catch ( Exception ex ) {
			ex.printStackTrace ();
			throw ( new RuntimeException ( ex ) );
		}
	}

}
