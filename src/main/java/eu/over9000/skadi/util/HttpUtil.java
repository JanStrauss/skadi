/*
 * Copyright (c) 2014-2016 s1mpl3x <jan[at]over9000.eu>
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
 */

package eu.over9000.skadi.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Util class used to send http requests to the twitch API.
 */
public class HttpUtil {

	private static final String SKADI_CLIENT_ID = "i2uu9j43ure9x7n4ojpgg4hvcnw6y91";
	private static final int CONNECTION_COUNT = 100;
	private static final HttpClient HTTP_CLIENT;

	static {
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		cm.setMaxTotal(CONNECTION_COUNT);
		cm.setDefaultMaxPerRoute(CONNECTION_COUNT);
		HTTP_CLIENT = HttpClients.createMinimal(cm);
	}

	public static InputStream getAPIResponseBin(final String apiURL) throws IOException, URISyntaxException {
		final URI URL = new URI(apiURL);
		final HttpGet request = new HttpGet(URL);
		request.setHeader("Client-ID", SKADI_CLIENT_ID);
		final HttpResponse response = HTTP_CLIENT.execute(request);
		final HttpEntity entity = response.getEntity();
		return entity.getContent();
	}
}
