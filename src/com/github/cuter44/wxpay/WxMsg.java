package com.github.cuter44.wxpay;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by kezhenxu on 4/17/15.
 */
public class WxMsg implements Serializable {

	public enum WxMsgType {
		NEWS ( "mpnews" ), TEXT ( "text" ), VOICE ( "voice" ), IMAGE ( "image" ), VIDEO ( "video" );

		private String typeName;

		WxMsgType ( String typeName ) { this.typeName = typeName; }

		public String getTypeName () { return typeName; }
	}

	private WxMsgType msgType;
	private String    contentOrId;
	private String title       = "";
	private String description = "";

	public WxMsg ( WxMsgType type ) {
		this.msgType = type;
	}

	public WxMsgType getMsgType () {
		return msgType;
	}

	public void setMsgType ( WxMsgType msgType ) {
		this.msgType = msgType;
	}

	public String getContentOrId () {
		return contentOrId;
	}

	public void setContentOrId ( String contentOrId ) {
		this.contentOrId = contentOrId;
	}

	public String getTitle () {
		return title;
	}

	public void setTitle ( String title ) {
		this.title = title;
	}

	public String getDescription () {
		return description;
	}

	public void setDescription ( String description ) {
		this.description = description;
	}

	public JSONObject toJson () {
		JSONObject jsonObject = new JSONObject ();
		JSONObject content = new JSONObject ();
		if ( msgType == WxMsgType.TEXT ) {
			content.put ( "content", contentOrId );
		}
		else {
			content.put ( "media_id", contentOrId );
		}
		jsonObject.put ( "msgtype", msgType.typeName );
		jsonObject.put ( msgType.typeName, content );
		return jsonObject;
	}

}
