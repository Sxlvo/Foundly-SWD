package model.utils;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GeocodingUtilsTest {

    @Test
    void testGetCoordinates_RispostaValida() throws IOException {
        HttpURLConnection connessione = mock(HttpURLConnection.class);
        when(connessione.getResponseCode()).thenReturn(200);
        String risposta = "[{\"lat\":\"40.68\",\"lon\":\"14.76\"}]";
        when(connessione.getInputStream())
                .thenReturn(new ByteArrayInputStream(risposta.getBytes(StandardCharsets.UTF_8)));

        assertArrayEquals(new double[]{40.68, 14.76},
                GeocodingUtils.getCoordinates("Via Roma", "Salerno", "SA", url -> connessione), 0.0001);
    }

    @Test
    void testGetCoordinates_SimulazioneErrore() {
        double[] coords = GeocodingUtils.getCoordinates("Via Roma", "Salerno", "SA", url -> {
            throw new IOException("Errore di rete simulato");
        });

        assertArrayEquals(new double[]{41.9028, 12.4964}, coords, 0.0001);
    }
}
