package cn.op.common.domain;

import java.io.Serializable;

/**
 * 实体基类：实现序列化
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public abstract class Base implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String id;
	
	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "oschina";

}
