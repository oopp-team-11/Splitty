package client.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.io.IOException;
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
}