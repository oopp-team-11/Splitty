package client.utils;

import static org.junit.jupiter.api.Assertions.*;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import commons.Event;
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
        long randomCode = UUID.randomUUID().hashCode();
        Mockito.doThrow(new InterruptedException()).when(serverUtils).getEvent(randomCode, "http://localhost:8080");
    }

    @Test
    void getEventThrowsIOException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        long randomCode = UUID.randomUUID().hashCode();
        Mockito.doThrow(new IOException()).when(serverUtils).getEvent(randomCode, "http://localhost:8080");
        assertThrows(IOException.class, () -> serverUtils.getEvent(randomCode, "http://localhost:8080"));
    }

    @Test
    void getEventWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9090));
        wireMockServer.start();

        long randomCode = UUID.randomUUID().hashCode();
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(get(urlEqualTo("/events/" + randomCode))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

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
        int responseCode = UUID.randomUUID().hashCode();
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(post(urlEqualTo("/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{'invitationCode': " + responseCode + "}")));

        serverUtils.createEvent(randomName, "http://localhost:9091");
        JSONObject jsonObject = new JSONObject(wireMockServer.getAllServeEvents().get(0).getResponse().getBodyAsString());


        wireMockServer.stubFor(get(urlEqualTo("/events/" + responseCode))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        serverUtils.createEvent(randomName, "http://localhost:9091");

        assertEquals(responseCode, jsonObject.getInt("invitationCode"));
        assertEquals(200, wireMockServer.getAllServeEvents().get(1).getResponse().getStatus());
        wireMockServer.stop();
    }

    @Test
    void getRecentEventsWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9092));
        wireMockServer.start();

        ServerUtils serverUtils = new ServerUtils();
        List<Long> codes = new ArrayList<>();
        String randomFileName = UUID.randomUUID().toString() + ".json";

        codes.add(54321L);
        codes.add(12345L);

        JsonObject json = Json.createObjectBuilder()
                .add("invitationCodes", Json.createArrayBuilder(codes))
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

        String jsonEventsList = "{\n" +
                "    \"events\": [\n" +
                "        {\n" +
                "            \"invitationCode\": 12345,\n" +
                "            \"eventName\": \"testEvent1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"invitationCode\": 54321,\n" +
                "            \"eventName\": \"testEvent2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        JSONObject jsonObject = new JSONObject(jsonEventsList);
        JSONArray jsonArray = new JSONArray(jsonObject
                .getJSONArray("events").toString());

        List<Event> events = new ArrayList<>();
        for(Object object: jsonArray) {
            events.add(new Event(
                    Long.parseLong(((JSONObject) object)
                            .get("invitationCode").toString()),
                    ((JSONObject) object).get("eventName").toString(),
                    null,
                    null,
                    null)
            );
        }

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
}