package com.FedericoFunes.app_service.services.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GoogleMapsService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public double[] getLatLngFromAddress(String address) throws Exception {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + address.replace(" ", "+")
                + "&key=" + apiKey;

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());

        JsonNode location = root.path("results").get(0).path("geometry").path("location");
        double lat = location.path("lat").asDouble();
        double lng = location.path("lng").asDouble();

        return new double[]{lat, lng};
    }
}

