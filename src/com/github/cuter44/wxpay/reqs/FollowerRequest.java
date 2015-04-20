package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.AccessTokenKeeper;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.FollowerResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/20/15.
 */
public class FollowerRequest extends RequestBase {

	public static final String DATA        = "data";
	public static final String OPENID      = "openid";
	public static final String NEXT_OPENID = "next_openid";

	enum ExecuteType {
		RETRIEVE_GROUP_IN, RETRIEVE_ALL, RETRIEVE_COUNT,
		UPDATE_REMARK, UPDATE_MOVE, UPDATE_BATCH_MOVE,
		DELETE
	}

	protected String        accessToken;
	protected ExecuteType   executeType;
	protected String        openId;
	protected String        remark;
	protected int           toGroupId;
	protected List<Integer> openIds;
	protected String        openIdsString;

	public FollowerRequest ( String aAccessToken, Properties aConf ) {
		super ( aConf );
		accessToken = aAccessToken;
	}

	public FollowerRequest ( AccessTokenKeeper aKeeper, Properties aProperties ) {
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
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return executeExactCode ();
	}

	private ResponseBase executeExactCode () {
		try {
			String returnContent;
			switch ( executeType ) {
				case RETRIEVE_GROUP_IN:
					returnContent = retrieveGroupIn ();
					break;
				case RETRIEVE_ALL:
					returnContent = getAllFollowers ();
					break;
				case RETRIEVE_COUNT:
					returnContent = retrieveCount ();
					break;
				case UPDATE_REMARK:
					returnContent = updateRemark ();
					break;
				case UPDATE_MOVE:
					returnContent = updateMove ();
					break;
				case UPDATE_BATCH_MOVE:
					returnContent = updateBatchMove ();
					break;
				default:
					throw new IllegalStateException ( "Call setExecuteType before calling execute." );
			}
			return new FollowerResponse ( returnContent );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	private String updateMove () throws IOException {
		String body;
		String url;
		String returnContent;
		body = String.format ( "{\"openid\":\"%s\",\"to_groupid\":%d}", openId, toGroupId );
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/members/update?access_token=%s", accessToken );
		returnContent =
				Request.Post ( url )
				       .bodyString ( body, ContentType.APPLICATION_JSON )
				       .execute ()
				       .returnContent ()
				       .asString ();
		return returnContent;
	}

	private String updateBatchMove () throws IOException {
		String body;
		String url;
		String returnContent;
		body = String.format ( "{\"openid_list\":%s,\"to_groupid\":%d}", openIdsString, toGroupId );
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/members/batchupdate?access_token=", accessToken );
		returnContent =
				Request.Post ( url )
				       .bodyString ( body, ContentType.APPLICATION_JSON )
				       .execute ()
				       .returnContent ()
				       .asString ();
		return returnContent;
	}

	private String updateRemark () throws IOException {
		String body;
		String url;
		String returnContent;
		body = String.format ( "{\"openid\":\"%s\",\"remark\":\"%s\"}", openId, remark );
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=%s", accessToken );
		returnContent =
				Request.Post ( url )
				       .bodyString ( body, ContentType.APPLICATION_JSON )
				       .execute ()
				       .returnContent ()
				       .asString ();
		return returnContent;
	}

	private String retrieveCount () throws IOException {
		String url;
		String returnContent;
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s", accessToken );
		String responseJson =
				Request.Get ( url )
				       .execute ()
				       .returnContent ()
				       .asString ();
		returnContent = responseJson;
		return returnContent;
	}

	private String retrieveGroupIn () throws IOException {
		String body;
		String url;
		String returnContent;
		body = String.format ( "{\"openid\":\"%s\"}", openId );
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/groups/getid?access_token=%s", accessToken );
		returnContent =
				Request.Post ( url )
				       .bodyString ( body, ContentType.APPLICATION_JSON )
				       .execute ()
				       .returnContent ()
				       .asString ();
		return returnContent;
	}

	private String getAllFollowers () {
		String    url;
		boolean   firstTime  = true;
		String    nextOpenId = "";
		JSONObject jsonObject2 = new JSONObject ();
		JSONArray result     = new JSONArray ();
		jsonObject2.put ( "openid", result );
		try {
			while ( true ) {
				url = String.format (
						"https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s&next_openid=%s",
						accessToken,
						( firstTime ? "\"\"" : nextOpenId ) );
				String responseJson =
						Request.Get ( url )
						       .execute ()
						       .returnContent ()
						       .asString ();

				JSONObject jsonObject = JSONObject.parseObject ( responseJson );
				JSONObject data = jsonObject.getJSONObject ( DATA );
				result.addAll ( data.getJSONArray ( OPENID ) );

				nextOpenId = jsonObject.getString ( NEXT_OPENID );
				if ( nextOpenId == null || nextOpenId.trim ().equals ( "" ) ) {
					break;
				}
			}
			return jsonObject2.toJSONString ();
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	public String getAccessToken () {
		return accessToken;
	}

	public FollowerRequest setAccessToken ( String aAccessToken ) {
		accessToken = aAccessToken;
		return this;
	}

	public ExecuteType getExecuteType () {
		return executeType;
	}

	public FollowerRequest setExecuteType ( ExecuteType aExecuteType ) {
		executeType = aExecuteType;
		return this;
	}

	public String getOpenId () {
		return openId;
	}

	public FollowerRequest setOpenId ( String aOpenId ) {
		openId = aOpenId;
		return this;
	}

	public String getRemark () {
		return remark;
	}

	public FollowerRequest setRemark ( String aRemark ) {
		remark = aRemark;
		return this;
	}

	public int getToGroupId () {
		return toGroupId;
	}

	public FollowerRequest setToGroupId ( int aToGroupId ) {
		toGroupId = aToGroupId;
		return this;
	}

	public List<Integer> getOpenIds () {
		return openIds;
	}

	public FollowerRequest setOpenIds ( List<Integer> aOpenIds ) {
		openIds = aOpenIds;
		boolean       isFirst = true;
		StringBuilder builder = new StringBuilder ();
		builder.append ( "[" );
		for ( Integer id : aOpenIds ) {
			if ( ! isFirst ) {
				builder.append ( "," );
			}
			builder.append ( id ).append ( "," );
			isFirst = false;
		}
		builder.append ( "]" );
		openIdsString = builder.toString ();
		return this;
	}
}
