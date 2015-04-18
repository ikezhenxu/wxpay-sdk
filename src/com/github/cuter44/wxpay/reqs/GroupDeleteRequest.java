package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.GroupResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/19/15.
 */
public class GroupDeleteRequest extends GroupRequestBase {

	protected String groupId;

	public GroupDeleteRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( groupId == null ) {
			throw new IllegalStateException ( "Call setup method before calling this method." );
		}
		return this;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeDelete ( groupId );
	}

	public void setup ( String groupId ) {
		this.groupId = groupId;
	}

	/**
	 * 删除分组
	 * <br>注意本接口是删除一个用户分组，
	 * <br>删除分组后，所有该分组内的用户自动进入默认分组。
	 *
	 * @param id 要删除的分组ID
	 * @return {@link WxmpResponseBase}
	 */
	public GroupResponse executeDelete ( String id ) {
		String data         = "{\"group\":{\"id\":" + id + "}}";
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Post ( url )
					       .body ( new StringEntity ( data ) )
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
