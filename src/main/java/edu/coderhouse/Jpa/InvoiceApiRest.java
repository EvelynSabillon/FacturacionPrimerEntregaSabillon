package edu.coderhouse.Jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class InvoiceApiRest {

    private static final String URL_API = "http://worldclockapi.com/api/json/utc/now";

    public String obtenerFechaUTC() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(URL_API, String.class);
            // Aquí deberías extraer la fecha del JSON que te devuelve la API.
            // Suponiendo que la API te devuelve un JSON como:
            // {"currentDateTime":"2025-03-28T04:37Z", ...}
            // Necesitas extraer "currentDateTime" de la respuesta.
            String fecha = response.split("\"currentDateTime\":\"")[1].split("\"")[0];
            return fecha;
        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Si hay un error, retornamos null
        }
    }
}