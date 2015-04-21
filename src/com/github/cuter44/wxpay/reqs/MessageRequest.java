package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.AccessTokenKeeper;
import com.github.cuter44.wxpay.WxMsg;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.MessageResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class MessageRequest extends RequestBase {

	enum ExecuteType{
		CREATE_TO_GROUP, CREATE_TO_LIST, CREATE_PREVIEW,
		RETRIEVE_STATUS,
		DELETE
	}

	protected ExecuteType executeType;
	protected String accessToken;
	protected Integer msgId;
	protected Integer groupId;
	protected String openid;
	protected WxMsg wxMsg;
	protected List<String> openIds;

	public MessageRequest ( Properties aConf, String accessToken ) {
		super ( aConf );
		this.accessToken = accessToken;
	}

	public MessageRequest ( Properties aConf, AccessTokenKeeper accessTokenKeeper ) {
		super ( aConf );
		this.accessToken = accessTokenKeeper.getAccessToken ();
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
	public MessageResponse execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		String data;
		String url;
		String returnContent;
		try {
			switch ( executeType ) {
				case CREATE_TO_GROUP:
					returnContent = executeCreate ();
					break;
				case CREATE_TO_LIST:
					returnContent = send2List ();
					break;
				case CREATE_PREVIEW:
					returnContent = preview ();
					break;
				case RETRIEVE_STATUS:
					returnContent = checkStatus ();
					break;
				case DELETE:
					returnContent = executeDelete ();
					break;
				default:
					throw new IllegalStateException ( "Call setExecuteType before calling execute." );
			}
			return new MessageResponse (returnContent);
		}catch ( Exception e ) {
			throw new RuntimeException ( e );
		}
	}

	private String executeDelete () throws IOException {
		String url;
		String data;
		String returnContent;
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=%s", accessToken );
		data = String.format ( "{\"msg_id\":%d}", msgId );
		String responseString =
				Request.Post ( url )
						.bodyString ( data, ContentType.APPLICATION_JSON )
						.execute ()
						.returnContent ()
						.asString ();
		returnContent = responseString;
		return returnContent;
	}

	private String executeCreate () throws IOException {
		String url;
		String returnContent;
		url = String.format ( "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s", accessToken );
		JSONObject filter = new JSONObject ();
		filter.put ( "is_to_all", false );
		filter.put ( "group_id", groupId );
		returnContent = sendWithFilter ( filter );
		return returnContent;
	}

	private String checkStatus () throws IOException {
		if ( msgId == null || msgId.toString ().length () == 0 ) {
			throw new RuntimeException ( "MsgId cannot be null or empty." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=%s", accessToken);
		String data = String.format("{\"msg_id\":%d}", msgId);
		String response =
				Request.Post ( url )
					   .bodyString ( data, ContentType.APPLICATION_JSON )
					   .execute ()
					   .returnContent ()
					   .asString();
		return response;
	}

	private String sendWithFilter ( JSONObject filter ) throws IOException {
		if ( wxMsg == null ) {
			throw new IllegalArgumentException ( "WxMsg can not be null." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s", accessToken);
		JSONObject data = wxMsg.toJson ();
		data.put ( "filter", filter );
		String response =
				Request.Post (url )
						.bodyString ( data.toJSONString (), ContentType.APPLICATION_JSON )
						.execute()
						.returnContent()
						.asString();
		return response;
	}

	private String send2List () throws IOException {
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

	private String preview () throws IOException {
		if ( openid == null || openid.trim ().length () == 0 || wxMsg == null ) {
			throw new IllegalArgumentException ( "Neither OpenId nor WxMsg can be null or empty." );
		}
		String url = String.format("https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=%s", accessToken);
		JSONObject data = wxMsg.toJson();
		data.put ( "touser", openid );
		String response =
				Request.Post ( url )
						.bodyString ( data.toJSONString (), ContentType.APPLICATION_JSON )
						.execute ()
						.returnContent ()
						.asString();
		return response;
	}
}