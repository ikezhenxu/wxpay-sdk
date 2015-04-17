package com.github.cuter44.wxpay.follower.reqs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxmpException;
import com.github.cuter44.wxpay.follower.resp.GroupResponse;
import com.github.cuter44.wxpay.resps.SnsOAuthAccessTokenResponse;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.List;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class GroupOperator {
	//	create?access_token=ACCESS_TOKEN";
	//	get?access_token=ACCESS_TOKEN
	//	update?access_token=ACCESS_TOKEN
	//	delete?access_token=ACCESS_TOKEN
	//	getid?access_token=ACCESS_TOKEN
	//	members/update?access_token=ACCESS_TOKEN
	//	members/batchupdate?access_token=ACCESS_TOKEN

	public static final String ERRCODE = "errcode";
	public static final String ERRMSG  = "errmsg";

	public static final String GROUPID = "groupid";

	public static final String URL_API_BASE = "https://api.weixin.qq.com/cgi-bin/groups/";

	protected String accessToken;

	public GroupOperator ( String accessToken ) {
		this.accessToken = accessToken;
	}

	public GroupOperator ( SnsOAuthAccessTokenResponse resp ) {
		this.accessToken = resp.getAccessToken ();
	}

	/**
	 * 新建一个分组
	 * <br>一个公众账号，最多支持创建100个分组。
	 *
	 * @param groupName 分组名字，长度在30个字符以内
	 * @return {@link GroupResponse}
	 */
	public GroupResponse executeCreate ( String groupName ) {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data = "{\"group\":{\"name\":\"" + groupName + "\"}}";
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Post ( URL_API_BASE + "create?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new GroupResponse ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 获取所有分组
	 *
	 * @return {@link GroupResponse#getResultGroups()}
	 */
	public GroupResponse executeGetAll () {
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Get ( URL_API_BASE + "get?access_token=" + accessToken )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new GroupResponse ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 更改分组的名称
	 *
	 * @param id        要更改的分组id
	 * @param groupName 新的分组名称，长度在30字符以内
	 * @return {@link WxmpResponseBase}
	 */
	public WxmpResponseBase executeUpdate ( String id, String groupName ) {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data = "{\"group\":{\"id\":" + id + ",\"name\":\"" + groupName + "\"}}";
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Post ( URL_API_BASE + "update?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new WxmpResponseBase ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 删除分组
	 * <br>注意本接口是删除一个用户分组，
	 * <br>删除分组后，所有该分组内的用户自动进入默认分组。
	 *
	 * @param id 要删除的分组ID
	 * @return {@link WxmpResponseBase}
	 */
	public WxmpResponseBase executeDelete ( String id ) {
		String data = "{\"group\":{\"id\":" + id + "}}";
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Post ( URL_API_BASE + "delete?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new WxmpResponseBase ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 获取用户所在的分组id
	 * <br>查询用户所在分组
	 * <br>通过用户的OpenID查询其所在的GroupID。
	 *
	 * @param openId 用户的openId
	 * @return 用户所在的分组id
	 */
	public String executeGetId ( String openId ) {
		try {
			String data = "{\"openid\":\"" + openId + "\"}";
			String responseJson = new String (
					Request.Post ( URL_API_BASE + "getid?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			JSONObject json;
			json = JSON.parseObject ( responseJson );
			Integer errcode = json.getInteger ( ERRCODE );

			if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
				throw ( new WxmpException ( errcode, json.getString ( ERRMSG ) ) );
			}
			return json.getString ( GROUPID );
		} catch ( Exception ex ) {
			ex.printStackTrace ();
			throw ( new RuntimeException ( ex ) );
		}
	}

	/**
	 * 移动用户所在的分组
	 *
	 * @param openId    要移动的用户openId
	 * @param toGroupId 要把用户移动到的目标分组id
	 * @return {@link WxmpResponseBase}
	 */
	public WxmpResponseBase executeMoveFollower ( String openId, String toGroupId ) {
		String data = "{\"openid\":\"" + openId + "\",\"to_groupid\":" + toGroupId + "}";
		String responseJson = null;
		try {
			responseJson = new String (
					Request.Post ( URL_API_BASE + "members/update?access_token=" + accessToken )
							.body ( new StringEntity ( data ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new WxmpResponseBase ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

	/**
	 * 批量移动用户分组
	 *
	 * @param openIdList 要移动的用户openId列表
	 * @param toGroupId  要把用户移动到的目标分组id
	 * @return {@link WxmpResponseBase}
	 */
	public WxmpResponseBase executeMoveBatch ( List<String> openIdList, String toGroupId ) {
		//		if ( openIdList.size () > 50 ) {
		//			throw new IllegalArgumentException ( "OpenIdList must be less 50: " + openIdList.size () );
		//		}
		try {
			StringBuilder data = new StringBuilder ()
					.append ( "{\"openid_list\":[" );
			boolean first = true;
			for ( String s : openIdList ) {
				if ( ! first ) {
					data.append ( "," );
				}
				data.append ( s );
				first = false;
			}
			data.append ( "],\"to_groupid\":" + toGroupId + "}" );
			String responseJson = new String (
					Request.Post ( URL_API_BASE + "members/update?access_token=" + accessToken )
							.body ( new StringEntity ( data.toString () ) )
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);
			return new WxmpResponseBase ( responseJson );
		} catch ( IOException e ) {
			e.printStackTrace ();
			throw new RuntimeException ( e );
		}
	}

}
