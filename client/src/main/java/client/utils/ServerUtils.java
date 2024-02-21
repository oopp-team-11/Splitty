/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import org.glassfish.jersey.client.ClientConfig;

import commons.Quote;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;

import javax.json.JsonObject;

public class ServerUtils {

	private static final String SERVER = "https://test.requestcatcher.com/";

	// Method that sends client's json data to server on endpoint /init_client

	public void sendJsonClient(JsonObject json) throws IOException, InterruptedException {

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(SERVER + "init_client"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json.toString()))
				.build();

		// Send the request and get the response

		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.statusCode());
	}

//	public void getQuotesTheHardWay() throws IOException, URISyntaxException {
//		var url = new URI("http://localhost:8080/api/quotes").toURL();
//		var is = url.openConnection().getInputStream();
//		var br = new BufferedReader(new InputStreamReader(is));
//		String line;
//		while ((line = br.readLine()) != null) {
//			System.out.println(line);
//		}
//	}

//	public List<Quote> getQuotes() {
//		return ClientBuilder.newClient(new ClientConfig()) //
//				.target(SERVER).path("api/quotes") //
//				.request(APPLICATION_JSON) //
//				.accept(APPLICATION_JSON) //
//                .get(new GenericType<List<Quote>>() {});
//	}

//	public Quote addQuote(Quote quote) {
//		return ClientBuilder.newClient(new ClientConfig()) //
//				.target(SERVER).path("api/quotes") //
//				.request(APPLICATION_JSON) //
//				.accept(APPLICATION_JSON) //
//				.post(Entity.entity(quote, APPLICATION_JSON), Quote.class);
//	}
}