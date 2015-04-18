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
public class MsgPreviewRequest extends GroupRequestBase {

	protected String openid;
	protected WxMsg  wxMsg;

	public MsgPreviewRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( openid == null || wxMsg == null ) {
			throw new IllegalStateException ( "Call setup before call this method." );
		}
		return this;
	}

	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return preview ();
	}

	public void setup ( String openid, WxMsg wxMsg ) {
		this.openid = openid;
		this.wxMsg = wxMsg;
	}

	/**
	 * 预览接口【订阅号与服务号认证后均可用】
	 * <p/>
	 * 开发者可通过该接口发送消息给指定用户，在手机端查看消息的样式和排版。
	 *
	 * @return {@see MsgResponse}
	 */
	public MsgResponse preview () {
		if ( openid == null || openid.trim ().length () == 0 || wxMsg == null ) {
			throw new IllegalArgumentException ( "Neither OpenId nor WxMsg can be null or empty." );
		}
		JSONObject data = wxMsg.toJson ();
		data.put ( "touser", openid );
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
