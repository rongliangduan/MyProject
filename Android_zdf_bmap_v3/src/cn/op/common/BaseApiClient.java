package cn.op.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import cn.op.common.util.Log;

/**
 * API客户端接口：用于访问网络数据
 * 
 */
public class BaseApiClient {
	private static String TAG = Log.makeLogTag(BaseApiClient.class);

	public static final String UTF_8 = "UTF-8";
	public static final String GBK = "GBK";
	public static final String DESC = "descend";
	public static final String ASC = "ascend";

	private final static int TIMEOUT_CONNECTION = 20000;
	private final static int TIMEOUT_SOCKET = 20000;
	protected final static int RETRY_TIME = 3;

	private static String appUserAgent;

	@SuppressWarnings("unused")
	private static String getUserAgent(BaseApplication BaseApplication) {
		if (appUserAgent == null || appUserAgent == "") {
			StringBuilder ua = new StringBuilder("OSChina.NET");
			ua.append('/' + BaseApplication.getPackageInfo().versionName + '_'
					+ BaseApplication.getPackageInfo().versionCode);// App版本
			ua.append("/Android");// 手机系统平台
			ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
			ua.append("/" + android.os.Build.MODEL); // 手机型号
			ua.append("/" + BaseApplication.getAppId());// 客户端唯一标识
			appUserAgent = ua.toString();
		}
		return appUserAgent;
	}

	protected static String _MakeURL(String p_url, Map<String, Object> params) {
		return _MakeURL(p_url, params, UTF_8);
	}

	@SuppressWarnings("deprecation")
	private static String _MakeURL(String p_url, Map<String, Object> params,
			String charsetName) {
		StringBuilder url = new StringBuilder(p_url);
		if (url.indexOf("?") < 0)
			url.append('?');

		for (String name : params.keySet()) {
			url.append('&');
			url.append(name);
			url.append('=');
			try {
				if (charsetName == null) {
					// 不做URLEncoder处理
					// url.append(String.valueOf(params.get(name)));
					url.append(URLEncoder.encode(String.valueOf(params
							.get(name))));
				} else {
					url.append(URLEncoder.encode(
							String.valueOf(params.get(name)), charsetName));
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return url.toString().replace("?&", "?");
	}

	// protected static SoapObject soapCall(SoapObject rpc, String url)
	// throws XmlPullParserException, AppException {
	//
	// return soapCall(rpc, url, null, false);
	// }

	// /**
	// * 调用失败后会重试两次
	// *
	// * @param rpc
	// * @param url
	// * @return
	// * @throws XmlPullParserException
	// * @throws AppException
	// */
	// protected static SoapObject soapCallRetry(SoapObject rpc, String url)
	// throws XmlPullParserException, AppException {
	//
	// return soapCall(rpc, url, null, true);
	// }

	/**
	 * dotNet的webService专用
	 * 
	 * @param rpc
	 * @param url
	 * @param soapAction
	 * @return
	 * @throws XmlPullParserException
	 * @throws AppException
	 */
	// protected static SoapObject soapCallDotNet(SoapObject rpc, String url,
	// String soapAction) throws XmlPullParserException, AppException {
	//
	// return soapCall(rpc, url, soapAction, true, false);
	// }

	// private static SoapObject soapCall(SoapObject rpc, String url,
	// String soapAction, boolean needRetry)
	// throws XmlPullParserException, AppException {
	//
	// return soapCall(rpc, url, soapAction, false, needRetry);
	// }

	// private static SoapObject soapCall(SoapObject rpc, String url,
	// String soapAction, boolean isDotNet, boolean needRetry)
	// throws XmlPullParserException, AppException {
	//
	// SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
	// SoapEnvelope.VER11);
	// // envelope.bodyOut = rpc;
	// envelope.dotNet = isDotNet;
	// envelope.setOutputSoapObject(rpc);
	//
	// SoapObject result = null;
	// int time = 0;
	// do {
	// HttpTransportSE ht = null;
	// try {
	//
	// ht = new HttpTransportSE(url);
	// ht.debug = true;
	//
	// ht.call(soapAction, envelope);
	// Log.d(TAG, "===soap_call_request===" + ht.requestDump);
	// Log.d(TAG, "===soap_call_response===" + ht.responseDump);
	//
	// // 3. 解析返回的数据
	// result = (SoapObject) envelope.getResponse();
	//
	// break;
	// } catch (IOException e) {
	// time++;
	// if (needRetry && time < RETRY_TIME) {
	// Log.w(TAG, "======soapCallGetBody====== retry " + time);
	// continue;
	// }
	// e.printStackTrace();
	// throw AppException.network(e);
	// } finally {
	// if (ht != null) {
	// ht.reset();
	// ht = null;
	// }
	// }
	// } while (time < RETRY_TIME);
	//
	// return result;
	// }

	public static String http_post_string(String url, String jsonString)
			throws AppException {
		Log.d(TAG, "http_post_string==> " + url);

		long millis1 = System.currentTimeMillis();

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SOCKET);

		HttpClient httpClient = new DefaultHttpClient(httpParams);

		String respBody = null;
		int time = 0;
		do {
			HttpPost httpPost = null;
			try {
				httpPost = new HttpPost(url);
				httpPost.setHeader("Content-type", " text/xml; charset=utf-8");
				// String userAgent = getUserAgent(BaseApplication);
				// requisicao.setHeader("User-Agent", userAgent);

				// I can see the json correctly print on log with the following
				// entry.
				Log.d(TAG, "JSon String to send as body in this request ==>> "
						+ jsonString);
				// than I try to send JSon using setEntityMethod
				StringEntity sEntity = new StringEntity(jsonString, UTF_8);
				sEntity.setContentType("text/xml; charset=utf-8");
				httpPost.setEntity(sEntity);

				HttpResponse httpResp = httpClient.execute(httpPost);

				int statusCode = httpResp.getStatusLine().getStatusCode();

				// String reasonPhrase = httpResp.getStatusLine()
				// .getReasonPhrase();
				
				HttpEntity httpEntity = httpResp.getEntity();
				// InputStream in = httpEntity.getContent(); //Get the data
				// in
				// the entity
				respBody = EntityUtils.toString(httpEntity);
				

				if (statusCode != HttpStatus.SC_OK) {
					Log.e(TAG, "http_post_string==> " + respBody);
					throw AppException.http(statusCode);
				}
				
				Log.d(TAG, "http_post_string==> " + respBody);
		
				break;
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					continue;
				}
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				if (httpPost != null) {
					httpPost.abort();
					httpPost = null;
				}
			}
		} while (time < RETRY_TIME);

		respBody = respBody.replaceAll("\\p{Cntrl}", "");

		long millis2 = System.currentTimeMillis();
		Log.d(TAG, "======http_post_string====== url=" + url + ",====time="
				+ (millis2 - millis1));

		return respBody;
	}

	public static HttpEntity http_get(String url) throws AppException {
		Log.d(TAG, "get_url==> " + url);

		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,
				TIMEOUT_CONNECTION);
		HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_SOCKET);

		HttpClient httpClient = new DefaultHttpClient(httpParams);

		HttpEntity httpEntity = null;
		int time = 0;
		do {

			HttpGet httpGet = null;
			try {

				httpGet = new HttpGet(url);
				httpGet.setHeader("Connection", "Keep-Alive");

				HttpResponse httpResp = httpClient.execute(httpGet);
				int statusCode = httpResp.getStatusLine().getStatusCode();

				if (statusCode != HttpStatus.SC_OK) {
					throw AppException.http(statusCode);
				}

				httpEntity = httpResp.getEntity();

				break;
			} catch (IOException e) {
				time++;
				if (time < RETRY_TIME) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
					}
					continue;
				}
				// 发生网络异常
				e.printStackTrace();
				throw AppException.network(e);
			} finally {
				// 释放连接
				if (httpGet != null) {
					httpGet.abort();
					httpGet = null;
				}
			}
		} while (time < RETRY_TIME);

		return httpEntity;
	}

	public static String http_get_string(String url) throws AppException {

		String responseBody = "";

		HttpEntity httpEntity = http_get(url);
		try {
			responseBody = EntityUtils.toString(httpEntity);
			Log.d(TAG, "get_responseBody==> " + responseBody);
		} catch (IOException e) {
			e.printStackTrace();
			throw AppException.network(e);
		}

		responseBody = responseBody.replaceAll("\\p{Cntrl}", "");

		return responseBody;
	}

	/**
	 * get请求URL
	 * 
	 * @param url
	 * @throws AppException
	 */
	public static InputStream http_get_inputstream(String urlStr)
			throws AppException {

		HttpURLConnection urlConn = null;
		InputStream inputStream = null;
		try {
			URL url = new URL(urlStr);
			urlConn = (HttpURLConnection) url.openConnection();
			inputStream = urlConn.getInputStream();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			throw AppException.network(e);
		}

		return inputStream;

	}

}