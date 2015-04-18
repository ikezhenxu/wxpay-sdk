package com.github.cuter44.wxpay.reqs;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * Created by kezhenxu on 4/18/15.
 */
public abstract class GroupRequestBase extends RequestBase {

	protected String API_URL_FORMAT;
	protected String accessToken;
	protected String url;

	public GroupRequestBase ( Properties aConf ) {
		super ( aConf );
	}

	@Override
	public RequestBase build () {
		if ( accessToken == null ) {
			throw new IllegalStateException ( "You must call setAccessToken method before calling this method." );
		}
		return this;
	}

	// DO NOT need sign in message module
	@Override
	public RequestBase sign () throws UnsupportedEncodingException, UnsupportedOperationException, IllegalStateException {
		return this;
	}

	@Override
	public String toURL () throws UnsupportedOperationException {
		build ();
		return url;
	}

	// set before call build method
	public void setAccessToken ( String accessToken ) {
		this.accessToken = accessToken;
		url = String.format ( API_URL_FORMAT, accessToken );
	}
}
