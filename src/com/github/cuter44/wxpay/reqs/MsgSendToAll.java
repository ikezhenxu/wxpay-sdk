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
public class MsgSendToAll extends GroupRequestBase {

	protected WxMsg wxMsg;

	public MsgSendToAll ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( wxMsg == null ) {
			throw new IllegalStateException ( "Call setup before calling this method." );
		}
		return this;
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
	 * @return {@see MsgResponse}
	 */
	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return send2All ();
	}

	public void setup ( WxMsg wxMsg ) {
		this.wxMsg = wxMsg;
	}


	protected MsgResponse send2All () {
		JSONObject filter = new JSONObject ();
		filter.put ( "is_to_all", true );
		return sendWithFilter ( wxMsg, filter );
	}

	protected MsgResponse sendWithFilter ( WxMsg wxMsg, JSONObject filter ) {

		JSONObject data = wxMsg.toJson ();
		data.put ( "filter", filter );
		try {
			String response = new String (
					Request.Post ( url )
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
