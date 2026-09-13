package com.SIH.Women.Safety.Device.controller;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.SIH.Women.Safety.Device.WomenSafetyDeviceApplication;

@SpringBootTest(classes = WomenSafetyDeviceApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApiEndpointSmokeTest {

    private static final String JSON_BODY = "{}";
    private static final int EXPECTED_REST_ENDPOINT_COUNT = 46;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    /**
     * Copy-paste API reference. Base URL for the running backend is http://localhost:8080.
     * The mapping test below is the authoritative check that every entry is registered.
     */
    private static Stream<Arguments> apiEndpoints() {
        return Stream.of(
                endpoint("GET", "/api/admin/users"),
                endpoint("POST", "/api/evidence/upload"),
                endpoint("POST", "/api/evidence/upload-bytes"),
                endpoint("POST", "/api/auth/register"),
                endpoint("POST", "/api/auth/login"),
                endpoint("GET", "/api/auth/recovery-check"),
                endpoint("POST", "/api/auth/admin-login"),
                endpoint("POST", "/api/danger-zone/add"),
                endpoint("GET", "/api/danger-zone/check"),
                endpoint("POST", "/api/emergency-contacts/add/{userId}"),
                endpoint("GET", "/api/emergency-contacts/{userId}"),
                endpoint("POST", "/api/fake-call/log/{userId}"),
                endpoint("GET", "/api/analytics/{userId}"),
                endpoint("POST", "/api/location/update"),
                endpoint("GET", "/api/location/history/{userId}"),
                endpoint("GET", "/api/risk/evaluate"),
                endpoint("POST", "/api/risk/evaluate-multi"),
                endpoint("POST", "/api/risk-session/evaluate/{userId}"),
                endpoint("POST", "/api/risk-session/event"),
                endpoint("GET", "/api/risk-session/current/{userId}"),
                endpoint("POST", "/api/safety/safe-route"),
                endpoint("GET", "/api/safety/route-deviation/{userId}"),
                endpoint("GET", "/api/safety/danger-zone-check"),
                endpoint("GET", "/api/safety/nearby-help"),
                endpoint("POST", "/api/safety/fall-detection"),
                endpoint("POST", "/api/safety/heart-rate"),
                endpoint("POST", "/api/safety/distress"),
                endpoint("GET", "/api/safety/crime-heatmap"),
                endpoint("GET", "/api/safety/live-sos-map"),
                endpoint("POST", "/api/sensor/data"),
                endpoint("POST", "/api/smart-sos/evaluate/{userId}"),
                endpoint("POST", "/api/sms/send"),
                endpoint("POST", "/api/sms/dispatch-sos"),
                endpoint("POST", "/api/sos/trigger"),
                endpoint("POST", "/api/sos/cancel/{id}"),
                endpoint("POST", "/api/sos/silent-trigger"),
                endpoint("GET", "/api/sos/history/{userId}"),
                endpoint("POST", "/api/trusted-contacts/add"),
                endpoint("GET", "/api/trusted-contacts/{userId}"),
                endpoint("GET", "/api/trusted-contacts/all"),
                endpoint("DELETE", "/api/trusted-contacts/delete/{id}"),
                endpoint("PUT", "/api/trusted-contacts/update/{id}"),
                endpoint("PUT", "/api/users/profile"),
                endpoint("PUT", "/api/users/password"),
                endpoint("POST", "/api/location/update/{userId}"),
                endpoint("GET", "/api/location/{userId}")
        );
    }

    @Test
    void allDocumentedRestEndpointsAreRegistered() {
        Set<String> registeredMappings = requestMappingHandlerMapping.getHandlerMethods().keySet().stream()
                .flatMap(mapping -> mapping.getMethodsCondition().getMethods().stream()
                        .map(method -> method.name() + " " + mapping.getPatternValues().iterator().next()))
                .collect(Collectors.toSet());

        List<String> documentedMappings = apiEndpoints()
                .map(arguments -> arguments.get()[0] + " " + arguments.get()[1])
                .toList();

        assertEquals(EXPECTED_REST_ENDPOINT_COUNT, documentedMappings.size());
        assertEquals(EXPECTED_REST_ENDPOINT_COUNT, registeredMappings.stream()
                .filter(this::isDocumentedRestEndpoint)
                .count());

        for (String documentedMapping : documentedMappings) {
            assertTrue(registeredMappings.contains(documentedMapping),
                    "Missing registered endpoint: " + documentedMapping);
        }
    }

    @ParameterizedTest(name = "{0} {1} is reachable")
    @MethodSource("apiEndpoints")
    void everyDocumentedEndpointRespondsWithoutRouteError(String httpMethod, String path) throws Exception {
        String requestPath = path.replace("{userId}", "test-user").replace("{id}", "test-id");
        MvcResult result = switch (httpMethod) {
            case "GET" -> mockMvc.perform(get(requestPath)).andReturn();
            case "POST" -> path.equals("/api/evidence/upload")
                ? mockMvc.perform(multipart(requestPath)
                    .file(new MockMultipartFile("file", "empty.wav", "audio/wav", new byte[0])))
                    .andReturn()
                : path.equals("/api/evidence/upload-bytes")
                    ? mockMvc.perform(post(requestPath)
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .content(new byte[0])).andReturn()
                    : mockMvc.perform(post(requestPath)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON_BODY)).andReturn();
            case "PUT" -> mockMvc.perform(put(requestPath)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JSON_BODY)).andReturn();
            case "DELETE" -> mockMvc.perform(delete(requestPath)).andReturn();
            default -> throw new IllegalArgumentException("Unsupported method: " + httpMethod);
        };

        int status = result.getResponse().getStatus();
        assertNotEquals(405, status, httpMethod + " " + path + " rejected its HTTP method");
    }

    private static Arguments endpoint(String method, String path) {
        return Arguments.of(method, path);
    }

    private boolean isDocumentedRestEndpoint(String mapping) {
        return apiEndpoints().anyMatch(arguments -> mapping.equals(arguments.get()[0] + " " + arguments.get()[1]));
    }
}
