package com.github.cuter44.wxpay.follower.reqs;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.follower.resp.FollowerOperatorResponse;
import com.github.cuter44.wxpay.resps.SnsOAuthAccessTokenResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class FollowerOperator {

	//		info/updateremark?access_token=ACCESS_TOKEN
	//	    get?access_token=ACCESS_TOKEN

	public static final String URL_API_BASE = "https://api.weixin.qq.com/cgi-bin/user/";
	public static final String TOTAL        = "total";
	public static final String COUNT        = "count";
	public static final String DATA         = "data";
	public static final String OPENID       = "openid";
	public static final String NEXT_OPENID  = "next_openid";

	protected String accessToken;

	public FollowerOperator ( String accessToken ) {
		this.accessToken = accessToken;
	}

	public FollowerOperator ( SnsOAuthAccessTokenResponse response ) {
		this.accessToken = response.getAccessToken ();
	}

	/**
	 * 更改用户备注名
	 *
	 * @param openId 用户openId
	 * @param remark 用户新的备注名
	 * @return {@link FollowerOperatorResponse}
	 * @throws IOException
	 */
	public FollowerOperatorResponse executeUpdateRemark ( String openId, String remark ) throws IOException {
		String data = "{\"openid\":\"" + openId + "\",\"remark\":\"" + remark + "\"}";
		String responseJson = new String (
				Request.Post ( URL_API_BASE + "info/updateremark?access_token=" + accessToken )
						.body ( new StringEntity ( data ) )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		return new FollowerOperatorResponse ( responseJson );
	}

	/**
	 * 获取粉丝总数量
	 *
	 * @return 粉丝总数量
	 * @throws IOException
	 */
	public int executeGetFollowersCount () throws IOException {
		String responseJson = new String (
				Request.Get ( URL_API_BASE + "get?access_token=" + accessToken )
						.execute ()
						.returnContent ()
						.asBytes (), "utf-8"
		);
		JSONObject jsonObject = JSONObject.parseObject ( responseJson );
		return jsonObject.getInteger ( TOTAL );
	}

	/**
	 * 获取所有粉丝的openid列表
	 *
	 * @return 所有粉丝openId列表
	 * @throws IOException
	 */
	public List<String> executeGetAllFollowers () throws IOException {
		boolean firstTime = true;
		String nextOpenId = "";
		LinkedList<String> result = new LinkedList<String> ();

		while ( true ) {
			String responseJson = new String (
					Request.Get (
							URL_API_BASE + "get?access_token=" + accessToken
									+ ( firstTime ? "" : ( "&next_openid=" + nextOpenId ) )
					)
							.execute ()
							.returnContent ()
							.asBytes (), "utf-8"
			);

			JSONObject jsonObject = JSONObject.parseObject ( responseJson );
			nextOpenId = jsonObject.getString ( NEXT_OPENID );
			JSONObject data = jsonObject.getJSONObject ( DATA );
			JSONArray openIds = data.getJSONArray ( OPENID );
			for ( int ii = 0; ii < openIds.size (); ii++ ) {
				result.addLast ( openIds.getString ( ii ) );
			}
			if ( nextOpenId == null || nextOpenId.trim ().equals ( "" ) ) {
				break;
			}
		}
		return result;
	}
}
