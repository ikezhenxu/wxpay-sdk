package com.github.cuter44.wxpay.follower;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by kezhenxu on 4/15/15.
 */
public class Group implements Serializable {

	public static final String KEY_NAME  = "name";
	public static final String KEY_ID    = "id";
	public static final String KEY_COUNT = "count";

	private String  name;
	private String  id;
	private Integer count;

	public String getName () {
		return name;
	}

	public void setName ( String name ) {
		this.name = name;
	}

	public String getId () {
		return id;
	}

	public void setId ( String id ) {
		this.id = id;
	}

	public Integer getCount () {
		return count;
	}

	public void setCount ( Integer count ) {
		this.count = count;
	}

	public static Group fromJsonString ( String jsonString ) {
		JSONObject jsonObject = JSONObject.parseObject ( jsonString );
		Group group = new Group ();
		group.setCount ( jsonObject.getInteger ( KEY_COUNT ) );
		group.setId ( jsonObject.getString ( KEY_ID ) );
		group.setName ( jsonObject.getString ( KEY_NAME ) );
		return group;
	}
}
