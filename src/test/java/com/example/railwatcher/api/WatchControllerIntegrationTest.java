package com.example.railwatcher.api;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ActiveProfiles("test")
class WatchControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = JsonMapper.builder().findAndAddModules().build();
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void shouldCreateListAndPauseWatcher() throws Exception {
        String request = objectMapper.writeValueAsString(Map.ofEntries(
                Map.entry("providerType", "MOCK"),
                Map.entry("trainNumber", "12004"),
                Map.entry("journeyDate", LocalDate.now().plusDays(10).toString()),
                Map.entry("sourceStation", "NDLS"),
                Map.entry("destinationStation", "LKO"),
                Map.entry("boardingStation", "CNB"),
                Map.entry("quota", "GN"),
                Map.entry("travelClass", "3A"),
                Map.entry("originDepartureTime", LocalDateTime.now().plusDays(10).withHour(6).withMinute(10).withSecond(0).withNano(0).toString()),
                Map.entry("boardingDepartureTime", LocalDateTime.now().plusDays(10).withHour(10).withMinute(25).withSecond(0).withNano(0).toString()),
                Map.entry("notifyTelegram", false),
                Map.entry("notifyEmail", false),
                Map.entry("notifyWebhook", false)
        ));

        String response = mockMvc.perform(post("/api/watchers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trainNumber").value("12004"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        mockMvc.perform(get("/api/watchers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].trainNumber", hasItem("12004")));

        mockMvc.perform(patch("/api/watchers/{id}/pause", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAUSED"));
    }
}
