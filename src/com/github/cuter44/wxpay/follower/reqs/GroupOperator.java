package com.github.cuter44.wxpay.follower.reqs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.WxmpException;
import com.github.cuter44.wxpay.follower.resp.GroupOperatorResponse;
import com.github.cuter44.wxpay.resps.SnsOAuthAccessTokenResponse;
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
	 *
	 * @param groupName 分组名字，长度在30个字符以内
	 * @return {@link GroupOperatorResponse}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeCreate ( String groupName ) throws IOException {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data = "{\"group\":{\"name\":\"" + groupName + "\"}}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "create?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new GroupOperatorResponse ( responseJson );
	}

	/**
	 * 获取所有分组
	 *
	 * @return {@link GroupOperatorResponse#getResultGroups()}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeGetAll () throws IOException {
		String responseJson = new String (
				Request.Get ( URL_API_BASE + "get?access_token=" + accessToken )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new GroupOperatorResponse ( responseJson );
	}

	/**
	 * 更改分组的名称
	 *
	 * @param id        要更改的分组id
	 * @param groupName 新的分组名称，长度在30字符以内
	 * @return {@link GroupOperatorResponse}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeUpdate ( String id, String groupName ) throws IOException {
		//		if ( groupName.length () >= 30 ) {
		//			throw new IllegalArgumentException ( "GroupName must be less than 30 characters:" + groupName);
		//		}
		String data = "{\"group\":{\"id\":" + id + ",\"name\":\"" + groupName + "\"}}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "update?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new GroupOperatorResponse ( responseJson );
	}

	/**
	 * 删除一个分组
	 *
	 * @param id 要删除的分组ID
	 * @return {@link GroupOperatorResponse}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeDelete ( String id ) throws IOException {
		String data = "{\"group\":{\"id\":" + id + "}}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "delete?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new GroupOperatorResponse ( responseJson );
	}

	/**
	 * 获取用户所在的分组id
	 *
	 * @param openId 用户的openId
	 * @return 用户所在的分组id
	 * @throws IOException
	 */
	public String executeGetId ( String openId ) throws IOException {
		String data = "{\"openid\":\"" + openId + "\"}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "getid?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		JSONObject json;
		try {
			json = JSON.parseObject ( responseJson );
		} catch ( Exception ex ) {
			throw ( new IllegalArgumentException ( "Malformed json input:" + responseJson ) );
		}

		Integer errcode = json.getInteger ( ERRCODE );

		if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
			throw ( new WxmpException ( errcode, json.getString ( ERRMSG ) ) );
		}

		return json.getString ( GROUPID );
	}

	/**
	 * 移动用户所在的分组
	 *
	 * @param openId    要移动的用户openId
	 * @param toGroupId 要把用户移动到的目标分组id
	 * @return {@link GroupOperatorResponse}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeMoveFollower ( String openId, String toGroupId ) throws IOException {
		String data = "{\"openid\":\"" + openId + "\",\"to_groupid\":" + toGroupId + "}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "members/update?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new GroupOperatorResponse ( responseJson );
	}

	/**
	 * 批量移动用户分组
	 *
	 * @param openIdList 要移动的用户openId列表
	 * @param toGroupId  要把用户移动到的目标分组id
	 * @return {@link GroupOperatorResponse}
	 * @throws IOException
	 */
	public GroupOperatorResponse executeMoveBatch ( List<String> openIdList, String toGroupId ) throws IOException {
		//		if ( openIdList.size () > 50 ) {
		//			throw new IllegalArgumentException ( "OpenIdList must be less 50: " + openIdList.size () );
		//		}
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
		return new GroupOperatorResponse ( responseJson );
	}

}
