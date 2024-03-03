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
import java.util.ArrayList;
import java.util.List;
import commons.Event;
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

	/**
	 * Method that sends a get request to the server to get the recent events
	 * @param server server url
	 * @param path path to the config file
	 * @return list of recent events
	 * @throws IOException if something goes wrong
	 * @throws InterruptedException if something goes wrong with the request
	 */
	public List<Event> getRecentEvents(String server, String path) throws IOException, InterruptedException {
		FileSystemUtils fileSystemUtils = new FileSystemUtils();
		List<Long> codes = fileSystemUtils.readInvitationCodes(path);

		URI uri = URI.create(server + "/events?query=titles&invitationCodes=" + codes.toString()
				.replace("[", "").
				replace("]", "").
				replace(" ", ""));

		HttpRequest request = HttpRequest.newBuilder()
				.uri(uri)
				.header("Accept", "application/json")
				.GET()
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		JSONObject jsonObject = new JSONObject(response.body());
		JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("events").toString());

		List<Event> events = new ArrayList<>();
		//TODO: Here Event objects with only invitationCode and eventName will be parsed using @JsonView(Views.StartScreenView.class)
		//Use https://www.baeldung.com/jackson-json-view-annotation for reference
//		for(Object object: jsonArray) {
//			events.add(new Event(
//					Long.parseLong(((JSONObject) object).get("invitationCode").toString()),
//					((JSONObject) object).get("eventName").toString(),
//					null,
//					null,
//					null)
//			);
//		}

		if(response.statusCode() == 205)
			fileSystemUtils.updateConfigFile("config.json", fileSystemUtils.extractInvitationCodesFromEventList(events));

		return events;
	}

	/**
	 * Method that sends a create participant post request to the server
	 * @param invitationCode invitation code of the event
	 * @param firstName first name of the participant
	 * @param lastName last name of the participant
	 * @param email email of the participant
	 * @param iban IBAN of the participant
	 * @param bic BIC of the participant
	 * @param server server url
	 * @throws IOException if something goes wrong
	 * @throws InterruptedException if something goes wrong with the request
	 */
	public long createParticipant(long invitationCode, String firstName, String lastName, String email,
								  String iban, String bic, String server)
			throws IOException, InterruptedException {

		JsonObject json = Json.createObjectBuilder()
				.add("invitationCode", invitationCode)
				.add("firstName", firstName)
				.add("lastName", lastName)
				.add("email", email)
				.add("iban", iban)
				.add("bic", bic)
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(server + "/participants"))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(json.toString()))
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		String responseJson = response.body();
		JSONObject jsonObject = new JSONObject(responseJson);
		long participantId = Long.parseLong((jsonObject.get("participantId").toString()));

		return participantId;
	}

	/**
	 * Method that sends an edit participant put request to the server
	 * @param participantId id of the participant
	 * @param firstName first name of the participant
	 * @param lastName last name of the participant
	 * @param email email of the participant
	 * @param iban IBAN of the participant
	 * @param bic BIC of the participant
	 * @param server server url
	 * @throws IOException if something goes wrong
	 * @throws InterruptedException if something goes wrong with the request
	 */
	public long editParticipant(long participantId, String firstName, String lastName, String email,
								  String iban, String bic, String server)
			throws IOException, InterruptedException {

		JsonObject json = Json.createObjectBuilder()
				.add("participantId", participantId)
				.add("firstName", firstName)
				.add("lastName", lastName)
				.add("email", email)
				.add("iban", iban)
				.add("bic", bic)
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(server + "/participants/"+participantId))
				.header("Content-Type", "application/json")
				.header("Accept", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(json.toString()))
				.build();

		HttpClient client = HttpClient.newHttpClient();
		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		String responseJson = response.body();
		JSONObject jsonObject = new JSONObject(responseJson);
		long responseParticipantId = Long.parseLong((jsonObject.get("participantId").toString()));

		return responseParticipantId;
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