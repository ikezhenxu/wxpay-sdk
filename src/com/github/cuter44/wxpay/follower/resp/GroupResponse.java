package com.github.cuter44.wxpay.follower.resp;

import com.alibaba.fastjson.JSONArray;
import com.github.cuter44.wxpay.follower.Group;
import com.github.cuter44.wxpay.resps.WxmpResponseBase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class GroupResponse extends WxmpResponseBase {

	public static final String GROUP  = "group";
	public static final String GROUPS = "groups";
	public static final String ID     = "id";
	public static final String NAME   = "name";

	public GroupResponse ( String jsonString ) {
		super ( jsonString );
	}

	public Group getResultGroup () {
		return Group.fromJsonString (
				getProperty ( GROUP )
		);
	}

	public List<Group> getResultGroups () {
		String jsonString = getProperty ( GROUP );
		if ( jsonString == null || jsonString.trim ().length () == 0 ) {
			return null;
		}

		JSONArray groups = JSONArray.parseArray (
				getProperty ( GROUPS ) );
		List<Group> groupList = new ArrayList<Group> ();
		for ( int ii = 0; ii < groups.size (); ii++ ) {
			Group group = Group.fromJsonString (
					groups.getJSONObject ( ii )
							.toJSONString () );
			groupList.add ( ii, group );
		}
		return groupList;
	}

}
