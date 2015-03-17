/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014-2015 s1mpl3x <jan[at]over9000.eu>
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package eu.over9000.skadi.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Util class used to send http requests to the twitch API.
 *
 * @author Jan Strauß
 *
 */
public class HttpUtil {
	
	private static final String CLIENT_ID = "i2uu9j43ure9x7n4ojpgg4hvcnw6y91";
	private static final int CONNECTION_COUNT = 100;
	private static final HttpClient HTTP_CLIENT;
	
	static {
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(HttpUtil.CONNECTION_COUNT);
		cm.setDefaultMaxPerRoute(HttpUtil.CONNECTION_COUNT);
		HTTP_CLIENT = HttpClients.createMinimal(cm);
	}
	
	public static String getAPIResponse(final String apiUrl) throws URISyntaxException, ClientProtocolException,
	        IOException {
		final URI URL = new URI(apiUrl);
		final HttpGet request = new HttpGet(URL);
		request.setHeader("Client-ID", HttpUtil.CLIENT_ID);
		final HttpResponse response = HttpUtil.HTTP_CLIENT.execute(request);
		final String responseString = new BasicResponseHandler().handleResponse(response);
		return responseString;
	}
}
