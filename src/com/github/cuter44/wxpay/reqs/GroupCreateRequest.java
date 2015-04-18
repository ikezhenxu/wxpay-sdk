package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.GroupResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/18/15.
 */
public class GroupCreateRequest extends GroupRequestBase {

	public GroupCreateRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=%s";
	}

	private String groupName;

	@Override
	public RequestBase build () {
		super.build ();
		if ( groupName == null ) {
			throw new IllegalStateException ( "You must call setGroupName method before calling this method." );
		}
		return this;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeCreate ( groupName );
	}

	public void setGroupName ( String groupName ) {
		this.groupName = groupName;
	}

	/**
	 * 新建一个分组
	 * <br>一个公众账号，最多支持创建100个分组。
	 *
	 * @param groupName 分组名字，长度在30个字符以内
	 * @return {@link GroupResponse}
	 */
	public GroupResponse executeCreate ( String groupName ) {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data         = "{\"group\":{\"name\":\"" + groupName + "\"}}";
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
