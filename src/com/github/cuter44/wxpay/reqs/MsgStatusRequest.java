package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.MsgResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

import java.lang.String;
import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class MsgStatusRequest extends RequestBase {

	enum ExecuteType{
		CREATE_TO_GROUP, CREATE_TO_LIST, CREATE_PREVIEW,
		RETRIEVE, RETRIEVE_STATUS,
		UPDATE, DELETE
	}

	protected ExecuteType executeType;
	protected String accessToken;
	protected int msgId;
	protected int groupId;
	protected List<String> openIds;

	public MsgStatusRequest ( Properties aConf, String accessToken) {
		super ( aConf );
		this.accessToken = accessToken;
	}

	public MsgStatusRequest ( Properties aConf, AccessTokenKeeper accessTokenKeeper) {
		super ( aConf );
		this.accessToken = accessTokenKeeper.getAccessToken ();
	}

	@Override
	public RequestBase build () {
		return this;
	}

	@Override
	public MsgResponse execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		String data;
		String url;
		String returnContent;
		switch (executeType) {
			case CREATE_TO_GROUP:
				url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s", accessToken);
				JSONObject filter = new JSONObject ();
				filter.put ( "is_to_all", false );
				filter.put ( "group_id", groupId );
				returnContent = sendWithFilter( wxMsg, filter );
				break;
			case CREATE_TO_LIST:
				returnContent = send2List();
				break;
			case CREATE_PREVIEW:
				returnContent = preview();
				break;
			case RETRIEVE:
				break;
			case RETRIEVE_STATUS:
				returnContent = checkStatus();
				break;
			case DELETE:
				url = String.format( "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=%s", accessToken );
				data = String.format ("{\"msg_id\":%d}", msgId);
				String responseString =
						Request.Post( url )
						.bodyString( data, ContentType.APPLICATION_JSON )
						.execute()
						.returnContent()
						.asString();
				returnContent = responseString;
				break;
			default:
				throw new IllegalStateException( "Call setExecuteType before calling execute." );
		}
		return new MsgResponse(returnContent);
	}

	private String checkStatus () {
		if ( msgId == null || msgId.toString ().length () == 0 ) {
			throw new RuntimeException ( "MsgId cannot be null or empty." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=%s", accessToken);
		String data = String.format("{\"msg_id\":%d}", msgId);
		String response = new String (
				Request.Post ( url )
					   .bodyString( data, ContentType.APPLICATION_JSON)
					   .execute ()
					   .returnContent ()
					   .asString();
		return response;
	}

	private String sendWithFilter ( WxMsg wxMsg, JSONObject filter ) {
		if ( wxMsg == null ) {
			throw new IllegalArgumentException ( "WxMsg can not be null." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s", accessToken);
		JSONObject data = wxMsg.toJson ();
		data.put ( "filter", filter );
		String response = new String (
				Request.Post (url )
						.bodyString(data.toJSONString(), ContentType.APPLICATION_JSON)
						.execute()
						.returnContent()
						.asString();
		return response;
	}

	private String send2List () {
		if ( openIds == null || openIds.isEmpty () || wxMsg == null ) {
			throw new IllegalArgumentException ( "OpenId list and WxMsg can not be null or empty." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=%s", accessToken);
		JSONArray objects = new JSONArray ( ( List ) openIds );
		JSONObject data = wxMsg.toJson ();
		data.put ( "touser", objects);
		String response =
				Request.Post(url)
						.bodyString( data.toJSONString (), ContentType.APPLICATION_JSON)
						.execute ()
						.returnContent ()
						.asString();
		return response;
	}

	private String preview () {
		if ( openid == null || openid.trim ().length () == 0 || wxMsg == null ) {
			throw new IllegalArgumentException ( "Neither OpenId nor WxMsg can be null or empty." );
		}
		url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=%s", accessToken);
		JSONObject data = wxMsg.toJson();
		data.put ( "touser", openid );
		String response = new String (
				Request.Post ( url )
						.bodyString ( data.toJSONString () )
						.execute ()
						.returnContent ()
						.asString();
		return response;
	}
}