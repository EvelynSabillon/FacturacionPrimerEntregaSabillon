package edu.coderhouse.Jpa;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WorldClockApiService {
    private static final String WORLD_CLOCK_API_URL = "http://worldclockapi.com/api/json/utc/now";

    public Date obtenerFechaActual() {
        RestTemplate restTemplate = new RestTemplate();
        try {
            WorldClockResponse response = restTemplate.getForObject(WORLD_CLOCK_API_URL, WorldClockResponse.class);
            if (response != null && response.getCurrentDateTime() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
                return dateFormat.parse(response.getCurrentDateTime());
            }
        } catch (RestClientException | java.text.ParseException e) {
            // Si falla la API o el parseo, usar fecha local
        }
        // Si algo falla, retornamos la fecha actual
        return new Date();
    }

    // Clase interna para mapear la respuesta de la API
    public static class WorldClockResponse {
        private String currentDateTime;

        public String getCurrentDateTime() {
            return currentDateTime;
        }

        public void setCurrentDateTime(String currentDateTime) {
            this.currentDateTime = currentDateTime;
        }
    }
}
