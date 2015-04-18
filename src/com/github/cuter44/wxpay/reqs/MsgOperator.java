package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.message.WxMsg;
import com.github.cuter44.wxpay.message.resp.MsgResponse;
import com.github.cuter44.wxpay.resps.SnsOAuthAccessTokenResponse;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.util.List;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class MsgOperator {

	//  https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=ACCESS_TOKEN
	//	https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN
	//	https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=ACCESS_TOKEN
	//	https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=ACCESS_TOKEN

	public static final String URL_API_BASE = "https://api.weixin.qq.com/cgi-bin/message/";

	protected String accessToken;

	public MsgOperator ( String accessToken ) {
		this.accessToken = accessToken;
	}

	public MsgOperator ( SnsOAuthAccessTokenResponse token ) {
		this.accessToken = token.getAccessToken ();
	}

	/**
	 * <br>根据OpenID列表群发【订阅号不可用，服务号认证后可用】
	 * <p/>
	 * <br>请注意：在返回成功时，意味着群发任务提交成功，
	 * <br>并不意味着此时群发已经结束，所以，
	 * <br>仍有可能在后续的发送过程中出现异常情况导致用户未收到消息，
	 * <br>如消息有时会进行审核、服务器不稳定等。
	 * <br>此外，群发任务一般需要较长的时间才能全部发送完毕，请耐心等待。
	 *
	 * @param openIds 可以接收到此消息的用户 openid 列表
	 * @param wxMsg   要发送的微信消息
	 * @return {@see MsgResponse}
	 */
	public MsgResponse send2List ( List<String> openIds, WxMsg wxMsg ) {
		if ( openIds == null || openIds.isEmpty () || wxMsg == null ) {
			throw new IllegalArgumentException ( "OpenId list and WxMsg can not be null or empty." );
		}

		try {
			JSONArray objects = new JSONArray ( ( List ) openIds );
			JSONObject data = wxMsg.toJson ();
			data.put ( "touser", objects );
			String response = new String (
					Request.Post ( URL_API_BASE + "mass/send?access_token=" + accessToken )
							.body ( new StringEntity ( data.toJSONString () ) )
							.execute ()
							.returnContent ()
							.asBytes (), "UTF-8" );
			return new MsgResponse ( response );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 根据分组进行群发【订阅号与服务号认证后均可用】
	 * <p/>
	 * <br>请注意：在返回成功时，意味着群发任务提交成功，
	 * <br>并不意味着此时群发已经结束，所以，
	 * <br>仍有可能在后续的发送过程中出现异常情况导致用户未收到消息，
	 * <br>如消息有时会进行审核、服务器不稳定等。
	 * <br>此外，群发任务一般需要较长的时间才能全部发送完毕，请耐心等待。
	 *
	 * @param groupId 接受到该消息的用户分组id
	 * @param wxMsg   要发送的微信消息
	 * @return {@see MsgResponse}
	 */
	public MsgResponse send2Group ( String groupId, WxMsg wxMsg ) {
		JSONObject filter = new JSONObject ();
		filter.put ( "is_to_all", false );
		filter.put ( "group_id", groupId );
		return sendWithFilter ( wxMsg, filter );
	}

	/**
	 * 发送消息给所有用户
	 * <p/>
	 * <br>请注意：在返回成功时，意味着群发任务提交成功，
	 * <br>并不意味着此时群发已经结束，所以，
	 * <br>仍有可能在后续的发送过程中出现异常情况导致用户未收到消息，
	 * <br>如消息有时会进行审核、服务器不稳定等。
	 * <br>此外，群发任务一般需要较长的时间才能全部发送完毕，请耐心等待。
	 *
	 * @param wxMsg 要发送的消息
	 * @return {@see MsgResponse}
	 */
	public MsgResponse send2All ( WxMsg wxMsg ) {
		JSONObject filter = new JSONObject ();
		filter.put ( "is_to_all", true );
		return sendWithFilter ( wxMsg, filter );
	}

	/**
	 * 删除群发【订阅号与服务号认证后均可用】
	 * <p/>
	 * <br>请注意，只有已经发送成功的消息才能删除删除消息只是将消息的图文详情页失效，
	 * <br>已经收到的用户，还是能在其本地看到消息卡片。
	 * <br>另外，删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	 *
	 * @param msgId 要删除的消息id
	 * @return {@see WxmpResponseBase}
	 */
	public WxmpResponseBase deleteMsg ( String msgId ) {
		if ( msgId == null || msgId.toString ().length () == 0 ) {
			throw new RuntimeException ( "MsgId cannot be null or empty." );
		}
		String data = "{\"msg_id\":" + msgId + "}";
		try {
			String response = new String (
					Request.Post ( URL_API_BASE + "mass/delete?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "UTF-8" );
			return new MsgResponse ( response );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 预览接口【订阅号与服务号认证后均可用】
	 * <p/>
	 * 开发者可通过该接口发送消息给指定用户，在手机端查看消息的样式和排版。
	 *
	 * @param openid 要接受此预览信息的用户 openid
	 * @param wxMsg  要预览的消息
	 * @return {@see MsgResponse}
	 */
	public MsgResponse preview ( String openid, WxMsg wxMsg ) {
		if ( openid == null || openid.trim ().length () == 0 || wxMsg == null ) {
			throw new IllegalArgumentException ( "Neither OpenId nor WxMsg can be null or empty." );
		}
		JSONObject data = wxMsg.toJson ();
		data.put ( "touser", openid );
		try {
			String response = new String (
					Request.Post ( URL_API_BASE + "mass/preview?access_token=" + accessToken )
							.body ( new StringEntity ( data.toJSONString () ) )
							.execute ()
							.returnContent ()
							.asBytes (), "UTF-8" );
			return new MsgResponse ( response );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 查询群发消息发送状态【订阅号与服务号认证后均可用】
	 *
	 * @param msgId 要查看的消息 id
	 * @return {@see MsgResponse}
	 */
	public MsgResponse checkStatus ( String msgId ) {
		if ( msgId == null || msgId.toString ().length () == 0 ) {
			throw new RuntimeException ( "MsgId cannot be null or empty." );
		}
		try {
			String data = "{\"msg_id\":" + msgId + "}";
			String response = new String (
					Request.Post ( URL_API_BASE + "mass/sendall?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "UTF-8" );
			return new MsgResponse ( response );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	private MsgResponse sendWithFilter ( WxMsg wxMsg, JSONObject filter ) {
		if ( wxMsg == null ) {
			throw new IllegalArgumentException ( "WxMsg can not be null." );
		}

		JSONObject data = wxMsg.toJson ();
		data.put ( "filter", filter );
		try {
			String response = new String (
					Request.Post ( URL_API_BASE + "mass/sendall?access_token=" + accessToken )
							.body ( new StringEntity ( data.toJSONString () ) )
							.execute ()
							.returnContent ()
							.asBytes (), "UTF-8" );
			return new MsgResponse ( response );
		} catch ( Exception e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

}