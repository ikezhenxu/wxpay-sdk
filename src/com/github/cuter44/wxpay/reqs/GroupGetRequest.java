package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.GroupResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/18/15.
 */
public class GroupGetRequest extends GroupRequestBase {

	public GroupGetRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=%s";
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeGetAll ();
	}

	public void setAccessToken ( String accessToken ) {
		this.accessToken = accessToken;
		url = String.format ( API_URL_FORMAT, accessToken );
	}

	/**
	 * 获取所有分组
	 *
	 * @return {@link GroupResponse#getResultGroups()}
	 */
	public GroupResponse executeGetAll () {
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Get ( url )
					       .execute ()
					       .returnContent ()
					       .asBytes (), "utf-8"
			);
			return new GroupResponse ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}
}
