package client.utils;

import static org.junit.jupiter.api.Assertions.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import java.io.IOException;

class ServerUtilsTest {

    @Test
    void sendCreateRequestThrowsInterruptedException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new InterruptedException()).when(serverUtils).sendCreateRequest("eventName", "http://localhost:8080");
        assertThrows(InterruptedException.class, () -> serverUtils.sendCreateRequest("eventName", "http://localhost:8080"));
    }

    @Test
    void sendCreateRequestThrowsIOException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new IOException()).when(serverUtils).sendCreateRequest("eventName", "http://localhost:8080");
        assertThrows(IOException.class, () -> serverUtils.sendCreateRequest("eventName", "http://localhost:8080"));
    }

    @Test
    void sendJoinRequestThrowsInterruptedException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new InterruptedException()).when(serverUtils).sendJoinRequest("invitationCode", "http://localhost:8080");
        assertThrows(InterruptedException.class, () -> serverUtils.sendJoinRequest("invitationCode", "http://localhost:8080"));
    }

    @Test
    void sendJoinRequestThrowsIOException() throws IOException, InterruptedException {
        ServerUtils serverUtils = Mockito.spy(ServerUtils.class);
        Mockito.doThrow(new IOException()).when(serverUtils).sendJoinRequest("invitationCode", "http://localhost:8080");
        assertThrows(IOException.class, () -> serverUtils.sendJoinRequest("invitationCode", "http://localhost:8080"));
    }

    @Test
    void sendJoinRequestWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9090));
        wireMockServer.start();

        String randomCode = "ABC123";
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(get(urlEqualTo("/events/" + randomCode))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("")));

        serverUtils.sendJoinRequest(randomCode, "http://localhost:9090");
        assertEquals(200, wireMockServer.getAllServeEvents().get(0).getResponse().getStatus());
        wireMockServer.stop();
    }

    @Test
    void sendCreateRequestWireMock() throws IOException, InterruptedException {
        WireMockServer wireMockServer = new WireMockServer(WireMockConfiguration
            .options()
            .port(9091));
        wireMockServer.start();

        String randomName = "testEvent";
        ServerUtils serverUtils = new ServerUtils();
        wireMockServer.stubFor(post(urlEqualTo("/events"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{'invitationCode': 'ABC123'}")));

        String randomCode = "ABC123";
        wireMockServer.stubFor(get(urlEqualTo("/events/" + randomCode))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody("")));

        serverUtils.sendCreateRequest(randomName, "http://localhost:9091");
        assertEquals(200, wireMockServer.getAllServeEvents().get(0).getResponse().getStatus());
        wireMockServer.stop();
    }
}