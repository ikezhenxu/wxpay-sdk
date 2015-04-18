package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.GroupResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/19/15.
 */
public class GroupMoveRequest extends GroupRequestBase {

	protected List<String> openIds;
	protected String       toGroupId;

	public GroupMoveRequest ( Properties aConf ) {
		super ( aConf );
		openIds = new ArrayList<String> ();
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( openIds.isEmpty () ) {
			throw new IllegalStateException ( "Call setup method before calling this method." );
		}
		return this;
	}

	@Override
	public String toURL () throws UnsupportedOperationException {
		return url;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeMove ();
	}

	public void setup ( String openId, String toGroupId ) {
		openIds.clear ();
		openIds.add ( openId );
		this.toGroupId = toGroupId;
	}

	public void setup ( List<String> openIds, String toGroupId ) {
		openIds.clear ();
		openIds.addAll ( openIds );
		this.toGroupId = toGroupId;
	}

	/**
	 * 批量移动用户分组
	 *
	 * @return {@link WxmpResponseBase}
	 */
	public GroupResponse executeMove () {
		//		if ( openIdList.size () > 50 ) {
		//			throw new IllegalArgumentException ( "OpenIdList must be less 50: " + openIdList.size () );
		//		}
		try {
			StringBuilder data = new StringBuilder ()
					.append ( "{\"openid_list\":[" );
			boolean first = true;
			for ( String s : openIds ) {
				if ( ! first ) {
					data.append ( "," );
				}
				data.append ( s );
				first = false;
			}
			data.append ( "],\"to_groupid\":" + toGroupId + "}" );
			String responseJson = new String (
					Request.Post ( url )
					       .body ( new StringEntity ( data.toString () ) )
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
