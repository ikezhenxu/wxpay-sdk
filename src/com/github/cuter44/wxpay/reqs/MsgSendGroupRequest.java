package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxMsg;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.MsgResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.util.Properties;

/**
 * Created by kezhenxu on 4/19/15.
 */
public class MsgSendGroupRequest extends GroupRequestBase {

	protected String groupId;
	protected WxMsg  wxMsg;

	public MsgSendGroupRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( groupId == null || wxMsg == null ) {
			throw new IllegalStateException ( "Call setup before calling this method." );
		}
		return this;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return send2Group ();
	}

	public void setup ( String groupId, WxMsg wxMsg ) {
		this.groupId = groupId;
		this.wxMsg = wxMsg;
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
	 * @return {@see MsgResponse}
	 */
	private MsgResponse send2Group (  ) {
		JSONObject filter = new JSONObject ();
		filter.put ( "is_to_all", false );
		filter.put ( "group_id", groupId );
		return sendWithFilter ( wxMsg, filter );
	}

	private MsgResponse sendWithFilter ( WxMsg wxMsg, JSONObject filter ) {
		if ( wxMsg == null ) {
			throw new IllegalArgumentException ( "WxMsg can not be null." );
		}

		JSONObject data = wxMsg.toJson ();
		data.put ( "filter", filter );
		try {
			String response = new String (
					Request.Post (url )
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
