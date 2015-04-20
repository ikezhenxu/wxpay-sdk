package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.AccessTokenKeeper;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.GroupResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/20/15.
 */
public class GroupRequest extends RequestBase {

	enum ExecuteType {
		CREATE, RETRIEVE, UPDATE, DELETE;
	}

	protected static final String KEY_ID          = "id";
	protected static final String KEY_NAME        = "name";
	protected static final String KEY_OPENID_LIST = "openid_list";
	protected static final String KEY_TO_GROUP_ID = "to_groupid";

	public static final List<String> PARAMETER_KEYS = Arrays.asList (
			"id",
			"name",
			"openid_list",
			"to_groupid"
	                                                                );

	protected ExecuteType executeType = null;
	protected String accessToken;
	protected String name;
	protected int    id;
	protected int    toGroupId;

	public GroupRequest ( String aAccessToken, Properties aConf ) {
		super ( aConf );
		accessToken = aAccessToken;
	}

	public GroupRequest ( AccessTokenKeeper aKeeper, Properties aProperties ) {
		super ( aProperties );
		accessToken = aKeeper.getAccessToken ();
	}

	@Override
	public RequestBase build () {
		return this;
	}

	@Override
	public RequestBase sign () throws UnsupportedEncodingException, UnsupportedOperationException, IllegalStateException {
		return this;
	}

	@Override
	public String toURL () throws UnsupportedOperationException {
		return null;
	}

	@Override
	public GroupResponse execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeExactCode ();
	}

	private GroupResponse executeExactCode () {
		try {
			String body;
			String url;
			String returnContent;
			switch ( executeType ) {
				case CREATE:
					body = String.format ( "{\"group\": {\"id\": %d, \"name\": \"%s\"}}", id, name );
					url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/create?access_token=%s", accessToken );
					returnContent =
							Request.Post ( url )
							       .bodyString ( body, ContentType.APPLICATION_JSON )
							       .execute ()
							       .returnContent ()
							       .asString ();
					break;
				case RETRIEVE:
					url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/get?access_token=%s", accessToken );
					returnContent =
							Request.Get ( url )
							       .execute ()
							       .returnContent ()
							       .asString ();
					break;
				case UPDATE:
					body = String.format ( "{\"group\": {\"id\": %d, \"name\": \"%s\"}}", id, name );
					url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/update?access_token=%s", accessToken );
					returnContent =
							Request.Post ( url )
							       .bodyString ( body, ContentType.APPLICATION_JSON )
							       .execute ()
							       .returnContent ()
							       .asString ();
					break;
				case DELETE:
					body = String.format ( "{\"group\":{\"id\":%d}}", id );
					url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/delete?access_token=%s", accessToken );
					returnContent =
							Request.Post ( url )
							       .bodyString ( body, ContentType.APPLICATION_JSON )
							       .execute ()
							       .returnContent ()
							       .asString ();
					break;
				default:
					throw new IllegalStateException ( "Call setExecuteType before calling execute." );
			}
			return new GroupResponse ( returnContent );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	public ExecuteType getExecuteType () {
		return executeType;
	}

	public GroupRequest setExecuteType ( ExecuteType aExecuteTypea ) {
		executeType = aExecuteTypea;
		return this;
	}

	public String getName () {
		return name;
	}

	public GroupRequest setName ( String aNamea ) {
		name = aNamea;
		return this;
	}

	public int getId () {
		return id;
	}

	public GroupRequest setId ( int aIda ) {
		id = aIda;
		return this;
	}

	public int getToGroupId () {
		return toGroupId;
	}

	public GroupRequest setToGroupId ( int aToGroupIda ) {
		toGroupId = aToGroupIda;
		return this;
	}
}
