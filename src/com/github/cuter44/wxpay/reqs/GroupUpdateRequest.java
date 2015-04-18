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
 * Created by kezhenxu on 4/18/15.
 */
public class GroupUpdateRequest extends GroupRequestBase {

	public GroupUpdateRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=%s";
	}

	protected String groupId;
	protected String newName;

	@Override
	public RequestBase build () {
		super.build ();
		if ( groupId == null || newName == null ) {
			throw new IllegalStateException ( "Call setup method before calling this method!" );
		}
		return this;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeUpdate ();
	}

	public void setup ( String groupId, String newName ) {
		this.groupId = groupId;
		this.newName = newName;
	}

	/**
	 * 更改分组的名称
	 *
	 * @return {@link WxmpResponseBase}
	 */
	public GroupResponse executeUpdate () {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data         = "{\"group\":{\"id\":" + groupId + ",\"name\":\"" + newName + "\"}}";
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
