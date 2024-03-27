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
import java.util.UUID;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import commons.Event;
import org.json.*;

/**
 * Server Utilities to help communicate with server side
 */
public class ServerUtils {

    /**
     * Method that sends a get request to the server to get the event
     * @param invitationCode invitation code of the event
     * @param server server url
     * @return event object
     * @throws IOException if something goes wrong
     * @throws InterruptedException if something goes wrong with the request
     */
    public Event getEvent(UUID invitationCode, String server)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(server + "/events/" + invitationCode))
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Event event = mapper.readValue(response.body(), Event.class);

        return event;
    }

    /**
     * Method that sends a create event post request to the server
     * @param eventName name of the event
     * @param server server url
     * @throws IOException if something goes wrong
     * @throws InterruptedException if something goes wrong with the request
     * @return an invitation code UUID
     */
    public UUID createEvent(String eventName, String server)
            throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(server + "/events"))
                .header("Content-Type", "text/plain")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(eventName))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String responseJson = response.body();
        JSONObject jsonObject = new JSONObject(responseJson);
        System.out.println(jsonObject);
        UUID invitationCode;
        if (jsonObject.has("id")) {
            invitationCode = UUID.fromString(jsonObject.getString("id"));
        } else {
            throw new JSONException("The key 'invitationCode' does not exist in the JSON object.");
        }

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
        List<UUID> codes = fileSystemUtils.readInvitationCodes(path);

        URI uri = URI.create(server + "/events?query=titles&invitationCodes=" + codes.toString()
                .replace("[", "").
                replace("]", "").
                replace(" ", ""));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                //.header("Accept", "application/json")
                .GET()
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        String jsonResp = response.body();
        jsonResp = "{\"events\":" + jsonResp + "}";

        JSONObject jsonObject = new JSONObject(jsonResp);
        JSONArray jsonArray = new JSONArray(jsonObject.getJSONArray("events").toString());

        List<Event> events = new ArrayList<>();

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        //TODO: Here Event objects with only invitationCode and eventName will be parsed using
        // @JsonView(Views.StartScreenView.class)
        //Use https://www.baeldung.com/jackson-json-view-annotation for reference
        for(Object object: jsonArray) {
            String jsonString = ((JSONObject) object).toString();
            Event event = mapper.readValue(jsonString, Event.class);
            events.add(event);
        }

        if(response.statusCode() == 206) {
            System.out.println("Invitation codes are invalid. Updating config file.");
            fileSystemUtils.updateConfigFile("config.json",
                    fileSystemUtils.extractInvitationCodesFromEventList(events));
        }


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
     * @return participant ID UUID
     */
    public UUID createParticipant(UUID invitationCode, String firstName, String lastName, String email,
                                  String iban, String bic, String server)
            throws IOException, InterruptedException {

        JsonObject json = Json.createObjectBuilder()
                .add("invitationCode", invitationCode.toString())
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
        UUID participantId = UUID.fromString((jsonObject.get("participantId").toString()));

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
     * @return response participant UUID
     */
    public UUID editParticipant(UUID participantId, String firstName, String lastName, String email,
                                String iban, String bic, String server)
            throws IOException, InterruptedException {

        JsonObject json = Json.createObjectBuilder()
                .add("participantId", participantId.toString())
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
        UUID responseParticipantId = UUID.fromString((jsonObject.get("participantId").toString()));

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