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

import javax.json.Json;
import javax.json.JsonObject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.*;

public class ServerUtils {

	//private static final String SERVER = "http://localhost:8080/";
	//private static final String SERVER = "https://test.requestcatcher.com/";


	/**
	 * Method that sends a join event get request to the server
	 * @param invitationCode invitation code of the event
	 * @param server server url
	 * @throws IOException if something goes wrong
	 * @throws InterruptedException if something goes wrong with the request
	 */
	public void getEvent(long invitationCode, String server)
		throws IOException, InterruptedException {
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(server + "/events/" + invitationCode))
				.GET()
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.statusCode());
		System.out.println(response.body());
	}

	/**
	 * Method that sends a create event post request to the server
	 * @param eventName name of the event
	 * @param server server url
	 * @throws IOException if something goes wrong
	 * @throws InterruptedException if something goes wrong with the request
	 */
	public long createEvent(String eventName, String server)
		throws IOException, InterruptedException {
		JsonObject json = Json.createObjectBuilder()
			.add("eventName", eventName)
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(server + "/events"))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json.toString()))
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		String responseJson = response.body();
		JSONObject jsonObject = new JSONObject(responseJson);
		long invitationCode = Long.parseLong((jsonObject.get("invitationCode").toString()));

		return invitationCode;
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