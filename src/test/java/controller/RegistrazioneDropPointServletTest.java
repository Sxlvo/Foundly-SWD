package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.service.DropPointService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RegistrazioneDropPointServletTest {

    private RegistrazioneDropPointServlet servlet;

    @Mock private DropPointService dpService;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new RegistrazioneDropPointServlet();

        // Iniezione del service tramite Reflection
        injectMock(servlet, "dpService", dpService);
    }

    private void injectMock(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }

    @Test
    void testDoGet() throws ServletException, IOException {
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).getRequestDispatcher(eq("/WEB-INF/jsp/registrazione_droppoint.jsp"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_Successo() throws ServletException, IOException {
        setupRequestParams("Bar Centrale", "bar@test.it", "40.5", "14.5");
        when(dpService.registraDropPoint(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyDouble(), anyDouble()))
                .thenReturn(true);
        when(request.getContextPath()).thenReturn("/Foundly");

        servlet.doPost(request, response);

        verify(response).sendRedirect("/Foundly/login?dpOk=1");
    }

    @Test
    void testDoPost_CampiMancanti() throws ServletException, IOException {
        setupRequestParams("Bar Centrale", "bar@test.it", null, "14.5");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("Tutti i campi sono obbligatori"));
        verify(dispatcher).forward(request, response);
        verifyNoInteractions(dpService);
    }

    @Test
    void testDoPost_EmailDuplicata() throws ServletException, IOException {
        setupRequestParams("Bar Duplicato", "esiste@test.it", "40.0", "14.0");

        // Il service restituisce false (email già presente)
        when(dpService.registraDropPoint(anyString(), anyString(), anyString(), anyString(),
                anyString(), anyString(), anyString(), anyString(), anyDouble(), anyDouble()))
                .thenReturn(false);

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errore"), contains("Email già registrata"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoPost_NumberFormatException() throws ServletException, IOException {
        // Testiamo il blocco catch della Servlet inviando stringhe non numeriche
        setupRequestParams("Bar Errore", "err@test.it", "not_a_number", "invalid");
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        // Deve scattare il controllo "latitudine == null" e mostrare l'errore campi mancanti
        verify(request).setAttribute(eq("errore"), anyString());
    }

    // Helper per settare i parametri comuni
    private void setupRequestParams(String nome, String email, String lat, String lon) {
        when(request.getParameter("nomeAttivita")).thenReturn(nome);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn("Password123!");
        when(request.getParameter("latitudine")).thenReturn(lat);
        when(request.getParameter("longitudine")).thenReturn(lon);
        when(request.getParameter("indirizzo")).thenReturn("Via Roma 1");
        when(request.getParameter("citta")).thenReturn("Salerno");
        when(request.getParameter("provincia")).thenReturn("SA");
        when(request.getParameter("telefono")).thenReturn("1234567890");
        when(request.getParameter("orari")).thenReturn("09:00-18:00");
    }
}
