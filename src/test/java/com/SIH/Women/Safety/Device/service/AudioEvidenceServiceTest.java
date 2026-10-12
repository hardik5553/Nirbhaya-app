package com.SIH.Women.Safety.Device.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.SIH.Women.Safety.Device.Repository.AudioVaultRepository;
import com.SIH.Women.Safety.Device.Repository.UserRepository;
import com.SIH.Women.Safety.Device.model.AudioVault;

@ExtendWith(MockitoExtension.class)
class AudioEvidenceServiceTest {

    @Mock
    private AudioVaultRepository audioVaultRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AudioEvidenceService service;

    private HttpServer storageServer;
    private final AtomicReference<String> uploadPath = new AtomicReference<>();
    private final AtomicReference<String> uploadContentType = new AtomicReference<>();
    private final AtomicInteger deleteCount = new AtomicInteger();
    private volatile int uploadStatus = 200;

    @BeforeEach
    void startStorageStub() throws IOException {
        storageServer = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        storageServer.createContext("/storage/v1/object", this::handleStorageRequest);
        storageServer.start();
        ReflectionTestUtils.setField(service, "supabaseUrl", "http://127.0.0.1:" + storageServer.getAddress().getPort());
        ReflectionTestUtils.setField(service, "serviceRoleKey", "test-key");
        ReflectionTestUtils.setField(service, "storageBucket", "test-bucket");
    }

    @AfterEach
    void stopStorageStub() {
        storageServer.stop(0);
    }

    @Test
    void savesStableObjectPathAndReturnsPrivateSignedUrl() {
        String signedUrl = service.saveAudioBytes(
                "user-1",
                "  USER@EXAMPLE.COM ",
                new byte[] { 1, 2, 3 },
                "audio/webm;codecs=opus",
                "private recording.webm");

        ArgumentCaptor<AudioVault> recordCaptor = ArgumentCaptor.forClass(AudioVault.class);
        verify(audioVaultRepository).save(recordCaptor.capture());
        AudioVault savedRecord = recordCaptor.getValue();

        assertTrue(signedUrl.startsWith("http://127.0.0.1:" + storageServer.getAddress().getPort()
                + "/storage/v1/object/sign/test-bucket/user-1/"));
        assertTrue(signedUrl.contains("?token=test-token"));
        assertEquals("audio/webm;codecs=opus", uploadContentType.get());
        assertTrue(uploadPath.get().startsWith("/storage/v1/object/test-bucket/user-1/"));
        assertEquals("user-1", savedRecord.getUserId());
        assertEquals("user@example.com", savedRecord.getUserEmail());
        assertTrue(savedRecord.getAudioUrl().startsWith("user-1/"));
        assertTrue(savedRecord.getAudioUrl().endsWith("-private_recording.webm"));
        assertTrue(!savedRecord.getAudioUrl().contains("/storage/v1/"));
    }

    @Test
    void refreshesExpiredSignedLinksWhenHistoryIsFetched() {
        AudioVault savedRecord = new AudioVault("user-1", "user@example.com", "user-1/recording.webm", "recording.webm");
        when(audioVaultRepository.findByUserIdOrderByCreatedAtDesc("user-1")).thenReturn(List.of(savedRecord));

        List<AudioVault> records = service.getVaultHistory("user-1", null);

        assertEquals(1, records.size());
        assertTrue(records.get(0).getAudioUrl().contains("/storage/v1/object/sign/test-bucket/user-1/recording.webm?token=test-token"));
        assertEquals("user-1/recording.webm", savedRecord.getAudioUrl().substring(
                savedRecord.getAudioUrl().indexOf("/storage/v1/object/sign/test-bucket/") + "/storage/v1/object/sign/test-bucket/".length(),
                savedRecord.getAudioUrl().indexOf("?")));
    }

    @Test
    void reportsStorageFailureAndDoesNotPersistAnErrorAsAnAudioUrl() {
        uploadStatus = 400;

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.saveAudioBytes("user-1", new byte[] { 1 }));

        assertEquals(HttpStatus.BAD_GATEWAY, error.getStatusCode());
        verify(audioVaultRepository, never()).save(any());
        assertEquals(0, deleteCount.get());
    }

    @Test
    void removesUploadedObjectIfTheDatabaseWriteFails() {
        when(audioVaultRepository.save(any(AudioVault.class))).thenThrow(new IllegalStateException("database unavailable"));

        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> service.saveAudioBytes("user-1", "user@example.com", new byte[] { 1 }, "audio/webm", "recording.webm"));

        assertEquals(HttpStatus.BAD_GATEWAY, error.getStatusCode());
        assertEquals(1, deleteCount.get());
    }

    private void handleStorageRequest(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getRawPath();
        if ("DELETE".equals(exchange.getRequestMethod())) {
            deleteCount.incrementAndGet();
            respond(exchange, 200, "{}");
        } else if (path.contains("/sign/")) {
            String marker = "/storage/v1/object/sign/test-bucket/";
            String storedPath = path.substring(path.indexOf(marker) + marker.length());
            respond(exchange, 200, "{\"signedURL\":\"/object/sign/test-bucket/" + storedPath + "?token=test-token\"}");
        } else {
            uploadPath.set(path);
            uploadContentType.set(exchange.getRequestHeaders().getFirst("Content-Type"));
            exchange.getRequestBody().readAllBytes();
            respond(exchange, uploadStatus, "{}");
        }
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] responseBody = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, responseBody.length);
        exchange.getResponseBody().write(responseBody);
        exchange.close();
    }
}
