package com.github.cuter44.wxpay.resps;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.cuter44.wxpay.Group;
import com.github.cuter44.wxpay.WxmpException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class GroupResponse extends ResponseBase {

	public static final String ERRCODE = "errcode";
	public static final String ERRMSG  = "errmsg";

	public static final String GROUP  = "group";
	public static final String GROUPS = "groups";
	public static final String ID     = "id";
	public static final String NAME   = "name";

	protected JSONObject jsonObject;

	public GroupResponse ( String jsonString ) {
		try {
			this.jsonObject = JSON.parseObject ( jsonString );
		} catch ( Exception ex ) {
			throw ( new IllegalArgumentException ( "Malformed json input:" + jsonString ) );
		}

		Integer errcode = this.getErrcode ();

		if ( ( errcode != null ) && ! ( errcode.equals ( 0 ) ) ) {
			throw ( new WxmpException ( errcode, this.getErrmsg () ) );
		}

		return;
	}

	public Group getResultGroup () {
		return Group.fromJsonString (
				jsonObject.toJSONString ()
		                            );
	}

	public List<Group> getResultGroups () {
		if ( jsonObject == null || jsonObject.isEmpty () ) {
			return null;
		}

		JSONArray   groups    = jsonObject.getJSONArray ( GROUPS );
		List<Group> groupList = new ArrayList<Group> ();
		for ( int ii = 0; ii < groups.size (); ii++ ) {
			Group group = Group.fromJsonString (
					groups.getJSONObject ( ii )
					      .toJSONString () );
			groupList.add ( ii, group );
		}
		return groupList;
	}

	/**
	 * @return errcode if error occured, otherwise null.
	 */
	public Integer getErrcode () {
		return (
				jsonObject.getInteger ( ERRCODE )
		);
	}

	public String getErrmsg () {
		return (
				jsonObject.getString ( ERRMSG )
		);
	}
}
