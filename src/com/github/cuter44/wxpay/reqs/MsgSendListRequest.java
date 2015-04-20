package com.github.cuter44.wxpay.reqs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxMsg;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;
import com.github.cuter44.wxpay.resps.MsgResponse;
import com.github.cuter44.wxpay.resps.ResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.util.List;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/19/15.
 */
public class MsgSendListRequest extends GroupRequestBase {

	protected List<String> openIds;
	protected WxMsg        wxMsg;

	public MsgSendListRequest ( Properties aConf ) {
		super ( aConf );
		API_URL_FORMAT = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=%s";
	}

	@Override
	public RequestBase build () {
		super.build ();
		if ( openIds == null || wxMsg == null ) {
			throw new IllegalStateException ( "Call setup before calling this method." );
		}
		return this;
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
	 * @return {@see MsgResponse}
	 */
	@Override
	public ResponseBase execute () throws WxpayException, WxpayProtocolException, UnsupportedOperationException {
		return send2List ();
	}

	public void setup ( List<String> openIds, WxMsg wxMsg ) {
		this.openIds = openIds;
		this.wxMsg = wxMsg;
	}

	private MsgResponse send2List () {
		if ( openIds == null || openIds.isEmpty () || wxMsg == null ) {
			throw new IllegalArgumentException ( "OpenId list and WxMsg can not be null or empty." );
		}

		try {
			JSONArray objects = new JSONArray ( ( List ) openIds );
			JSONObject data = wxMsg.toJson ();
			data.put ( "touser", objects );
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
