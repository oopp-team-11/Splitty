package client.utils;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import commons.Event;
import commons.Participant;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class ServerUtilsTest {

    @Test
    void createEventThrowsInterruptedException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new InterruptedException()).when(serverUtils).createEvent("eventName", "http://localhost:8080");
        assertThrows(InterruptedException.class, () -> serverUtils.createEvent("eventName", "http://localhost:8080"));
    }

    @Test
    void createEventThrowsIOException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new IOException()).when(serverUtils).createEvent("eventName", "http://localhost:8080");
        assertThrows(IOException.class, () -> serverUtils.createEvent("eventName", "http://localhost:8080"));
    }

    @Test
    void getEventThrowsInterruptedException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        UUID randomCode = UUID.randomUUID();
        Mockito.doThrow(new InterruptedException()).when(serverUtils).getEvent(randomCode, "http://localhost:8080");
    }

    @Test
    void getEventThrowsIOException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        UUID randomCode = UUID.randomUUID();
        Mockito.doThrow(new IOException()).when(serverUtils).getEvent(randomCode, "http://localhost:8080");
        assertThrows(IOException.class, () -> serverUtils.getEvent(randomCode, "http://localhost:8080"));
    }

    @Test
    void getEventWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9090));
        wireMockServer.start();

        UUID randomCode = UUID.randomUUID();
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(get(urlEqualTo("/events/" + randomCode))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                {
                                    "id":"5bf9b70b-c20a-4959-883f-4fa0e54f90d2",
                                    "title":"TEST",
                                    "creationDate":"2024-03-17T14:47:58.026241",
                                    "lastActivity":"2024-03-17T14:47:58.026241"
                                }""")));

        serverUtils.getEvent(randomCode, "http://localhost:9090");
        assertEquals(200, wireMockServer.getAllServeEvents().get(0).getResponse().getStatus());
        wireMockServer.stop();
    }

    @Test
    void createEventWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9091));
        wireMockServer.start();

        String randomName = "testEvent";
        UUID responseCode = UUID.randomUUID();
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(post(urlEqualTo("/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{'id': " + responseCode + "}")));

        serverUtils.createEvent(randomName, "http://localhost:9091");
        JSONObject jsonObject = new JSONObject(wireMockServer.getAllServeEvents().get(0).getResponse().getBodyAsString());


        wireMockServer.stubFor(get(urlEqualTo("/events/" + responseCode))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        serverUtils.createEvent(randomName, "http://localhost:9091");

        assertEquals(responseCode.toString(), jsonObject.getString("id"));
        assertEquals(200, wireMockServer.getAllServeEvents().get(1).getResponse().getStatus());
        wireMockServer.stop();
    }

    @Test
    void getRecentEventsWireMock() throws IOException, InterruptedException, IllegalAccessException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9092));
        wireMockServer.start();

        ServerUtils serverUtils = new ServerUtils();
        List<UUID> codes = new ArrayList<>();
        String randomFileName = UUID.randomUUID() + ".json";

        codes.add(UUID.fromString("3909e6f3-7bf3-47c5-992a-3dcf53738ab5"));
        codes.add(UUID.fromString("370e486d-6aa8-4d29-90c6-10aade69b13b"));

        List<String> codeStrings = codes.stream().map(UUID::toString).toList();
        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codeStrings))
                .build();

        if(new File(randomFileName).exists()) {
            new File(randomFileName).delete();
        }

        FileWriter file = new FileWriter(randomFileName);
        file.write(json.toString());
        file.flush();
        file.close();

        URI uri = URI.create("/events"
        +"?query=titles&invitationCodes=" + codes.toString()
                .replace("[", "").
                replace("]", "").
                replace(" ", ""));

        String jsonEventsList = """
                        [
                        {
                            "id": "3909e6f3-7bf3-47c5-992a-3dcf53738ab5",
                            "title": "testEvent1"
                        },
                        {
                            "id": "370e486d-6aa8-4d29-90c6-10aade69b13b",
                            "title": "testEvent2"
                        }
                    ]
                """;
//        JSONObject jsonObject = new JSONObject(jsonEventsList);
//        JSONArray jsonArray = new JSONArray(jsonObject
//                .getJSONArray("events").toString());
//
//        System.out.println("AAAAAAAA");

        List<Event> events = new ArrayList<>();

        Event event1 = new Event("testEvent1");
        Event event2 = new Event("testEvent2");
        setId(event1, UUID.fromString("3909e6f3-7bf3-47c5-992a-3dcf53738ab5"));
        setId(event2, UUID.fromString("370e486d-6aa8-4d29-90c6-10aade69b13b"));
        setParticipants(event1, null);
        setParticipants(event2, null);
        events.add(event1);
        events.add(event2);

//        for(Object object: jsonArray) {
//            events.add(new Event(
//                    Long.parseLong(((JSONObject) object)
//                            .get("invitationCode").toString()),
//                    ((JSONObject) object).get("eventName").toString(),
//                    null,
//                    null,
//                    null)
//            );
//        }

        wireMockServer.stubFor(get(urlEqualTo(uri.toString()))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(jsonEventsList)));

        assertEquals(serverUtils.getRecentEvents("http://localhost:9092",
                        randomFileName), events);

        new File(randomFileName).delete();
        wireMockServer.stop();
    }


    private static void setId(Event toSet, UUID newId) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "id", newId, true);
    }

    private static void setParticipants(Event toSet, List<Participant> newParticipants) throws IllegalAccessException {
        FieldUtils.writeField(toSet, "participants", newParticipants, true);
    }


}