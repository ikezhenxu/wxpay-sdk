package com.github.cuter44.wxpay.resps;

import com.github.cuter44.nyafx.crypto.CryptoBase;
import com.github.cuter44.nyafx.text.URLBuilder;
import com.github.cuter44.wxpay.WxpayException;
import com.github.cuter44.wxpay.WxpayProtocolException;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/** General response passed by req.execute() or alipay callback/redirect gateways
 * Actually reqs/gateways passed excatly response type, i.e. sub-class of this.
 * I'm recommending you to see the sub-class javadoc... to help you write elegant code.
 */
public class ResponseBase
{
  // CONSTANTS
    public static final String KEY_KEY          = "key_key";

    public static final String KEY_RETURN_CODE  = "return_code";
    public static final String KEY_RETURN_MSG   = "return_msg";
    public static final String KEY_RESULT_CODE  = "result_code";
    public static final String KEY_ERR_CODE     = "err_code";
    public static final String KEY_ERR_CODE_DES = "err_code_des";

    public static final String VALUE_SUCCESS    = "SUCCESS";
    public static final String VALUE_FAIL       = "FAIL";

    protected static CryptoBase crypto = CryptoBase.getInstance();

    protected Boolean validity = null;

  // STRING
    protected String respString;
    /**
     * retrieve callback params or response content as String
     */
    public String getString()
    {
        return(this.respString);
    }

  // PROPERTIES
    protected Properties respProp;

    /**
     * retrieve callback params or response content as Properties
     */
    public Properties getProperties()
    {
        return(this.respProp);
    }

    public String getProperty(String key)
    {
        return(this.respProp.getProperty(key));
    }


  // CONSTRUCT
    public ResponseBase()
    {
        return;
    }

    public ResponseBase(String aRespString)
    {
        this(aRespString, null);

        return;
    }

    public ResponseBase(Properties aRespProp)
    {
        this(null, aRespProp);

        return;
    }

    public ResponseBase(String aRespString, Properties aRespProp)
    {
        this.respString = aRespString;
        this.respProp = aRespProp;

        return;
    }

  // EXCEPTION
    /** 此字段是通信标识，非交易标识，交易是否成功需要查看 result_code 来判断
     */
    public boolean isReturnCodeSuccess()
    {
        return(
            VALUE_SUCCESS.equals(this.getProperty(KEY_RETURN_CODE))
        );
    }

    public WxpayProtocolException getReturnMsg()
    {
        String returnMsg = this.getProperty(KEY_RETURN_MSG);

        if (returnMsg != null)
            return(new WxpayProtocolException(returnMsg));

        // else
        return(null);
    }

    public boolean isResultCodeSuccess()
    {
        return(
            VALUE_SUCCESS.equals(this.getProperty(KEY_RESULT_CODE))
        );
    }

    public WxpayException getErrCode()
    {
        String errCode = this.getProperty(KEY_ERR_CODE);

        if (errCode != null)
            return(new WxpayException(errCode));

        // else
        return(null);
    }

    public String getErrCodeDes()
    {
        return(
            this.getProperty(KEY_ERR_CODE_DES)
        );
    }

  // VERIFY
    /**
     * @param paramNames key names to submit, in dictionary order
     */
    protected String sign(List<String> paramNames)
        throws UnsupportedEncodingException, UnsupportedOperationException, IllegalStateException
    {
        String key = this.getProperty(KEY_KEY);

        String sign = this.signMD5(paramNames, key);

        return(sign);
    }

    /**
     * caculate sign according to wxp-spec
     * @exception UnsupportedEncodingException if your runtime does not support utf-8.
     */
    protected String signMD5(List<String> paramNames, String key)
        throws UnsupportedEncodingException
    {
        if (key == null)
            throw(new IllegalArgumentException("No KEY, no sign. Please check your configuration."));

        StringBuilder sb = new StringBuilder()
            .append(this.toQueryString(paramNames))
            .append("&key="+key);

        String sign = this.crypto.byteToHex(
            this.crypto.MD5Digest(
                sb.toString().getBytes("utf-8")
        ));

        sign = sign.toUpperCase();

        return(sign);
    }

    /** Provide query string to sign().
     * toURL() may not invoke this method.
     */
    protected String toQueryString(List<String> paramNames)
    {
        URLBuilder ub = new URLBuilder();

        for (String key:paramNames)
            ub.appendParam(key, this.getProperty(key));

        return(ub.toString());
    }

    protected boolean verifySign(List<String> paramNames, Properties conf)
        throws UnsupportedEncodingException
    {
        this.respProp = buildConf(conf, this.respProp);
        String stated = this.getProperty("sign");
        String calculated = this.sign(
            paramNames
        );

        return(
            stated!=null && stated.equals(calculated)
        );
    }

    /** 子类应该实现这个方法以验证签名
     * SUB-CLASS MUST IMPLEMENT THIS METHOD TO BE CALLBACKED.
     */
    protected boolean verifySign(Properties conf)
        throws UnsupportedEncodingException, UnsupportedOperationException
    {
        throw(new UnsupportedOperationException("Don't know which params should be signed."));
    }

    /**
     * verify response sign
     * @return true if passed (i.e. response content should be trusted), otherwise false
     */
    public boolean verify(Properties conf)
        throws UnsupportedEncodingException
    {
        if (this.validity != null)
            return(this.validity);

        Boolean skipSign    = Boolean.valueOf(conf.getProperty("SKIP_VERIFY_NOTIFY_SIGN"));

        // else
        this.validity = true;

        if (!skipSign)
            this.validity = this.validity && this.verifySign(conf);

        return(this.validity);
    }

    protected static Properties buildConf(Properties prop, Properties defaults)
    {
        Properties ret = new Properties(defaults);
        Iterator<String> iter = prop.stringPropertyNames().iterator();
        while (iter.hasNext())
        {
            String key = iter.next();
            ret.setProperty(key, prop.getProperty(key));
        }
        return(ret);
    }
}
