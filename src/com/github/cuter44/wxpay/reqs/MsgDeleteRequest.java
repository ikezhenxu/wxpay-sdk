package com.github.cuter44.wxpay.reqs;

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
public class MsgDeleteRequest extends GroupRequestBase {

	protected String msgId;

	public MsgDeleteRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=%s";
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
	 * 删除群发【订阅号与服务号认证后均可用】
	 * <p/>
	 * <br>请注意，只有已经发送成功的消息才能删除删除消息只是将消息的图文详情页失效，
	 * <br>已经收到的用户，还是能在其本地看到消息卡片。
	 * <br>另外，删除群发消息只能删除图文消息和视频消息，其他类型的消息一经发送，无法删除。
	 *
	 * @return {@see MsgResponse}
	 */
	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return deleteMsg ();
	}

	public void setup ( String msgId ) {
		this.msgId = msgId;
	}

	private MsgResponse deleteMsg () {
		String data = "{\"msg_id\":" + msgId + "}";
		try {
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
