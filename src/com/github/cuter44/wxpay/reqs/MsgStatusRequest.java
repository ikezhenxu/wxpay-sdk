package com.github.cuter44.wxpay.reqs;

import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.MsgResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.util.Properties;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class MsgStatusRequest extends GroupRequestBase {

	protected String msgId;

	public MsgStatusRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( msgId == null ) {
			throw new IllegalStateException ( "Call setup before call this method." );
		}
		return this;
	}

	/**
	 * 查询群发消息发送状态【订阅号与服务号认证后均可用】
	 *
	 * @return {@see MsgResponse}
	 */
	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return checkStatus ();
	}

	public void setup ( String msgId ) {
		this.msgId = msgId;
	}

	protected MsgResponse checkStatus () {
		if ( msgId == null || msgId.toString ().length () == 0 ) {
			throw new RuntimeException ( "MsgId cannot be null or empty." );
		}
		try {
			String data = "{\"msg_id\":" + msgId + "}";
			String response = new String (
					Request.Post ( url )
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

}