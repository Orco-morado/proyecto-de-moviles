package cl.duoc.activaciones.oportunidades.controller;

import cl.duoc.activaciones.oportunidades.dto.OportunidadDto;
import cl.duoc.activaciones.oportunidades.exception.ResourceNotFoundException;
import cl.duoc.activaciones.oportunidades.model.EstadoOportunidad;
import cl.duoc.activaciones.oportunidades.model.Prioridad;
import cl.duoc.activaciones.oportunidades.model.TipoEvento;
import cl.duoc.activaciones.oportunidades.service.OportunidadService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OportunidadController.class)
class OportunidadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OportunidadService service;

    private OportunidadDto dtoValida() {
        OportunidadDto dto = new OportunidadDto();
        dto.setNombre("Oportunidad de prueba");
        dto.setTipo(TipoEvento.FERIA_EMPRESARIAL);
        dto.setDescripcion("Descripcion valida");
        dto.setFuenteInformacion("Web");
        dto.setFechaDeteccion(1763740800000L);
        dto.setResponsableId(2L);
        dto.setPrioridad(Prioridad.ALTA);
        dto.setPotencialComercial("Alto");
        dto.setEstado(EstadoOportunidad.EVENTO_IDENTIFICADO);
        dto.setEventoId(1L);
        dto.setObservaciones("");
        return dto;
    }

    @Test
    void getObtenerTodas_devuelveLista() throws Exception {
        when(service.listar()).thenReturn(List.of(dtoValida()));

        mockMvc.perform(get("/api/oportunidades"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Oportunidad de prueba"));
    }

    @Test
    void getObtenerPorId_devuelveOportunidad() throws Exception {
        OportunidadDto dto = dtoValida();
        dto.setId(1L);
        when(service.obtener(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/oportunidades/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("FERIA_EMPRESARIAL"));
    }

    @Test
    void getObtenerPorId_inexistente_devuelve404() throws Exception {
        when(service.obtener(99L)).thenThrow(new ResourceNotFoundException("No existe la oportunidad con id 99"));

        mockMvc.perform(get("/api/oportunidades/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.mensajes[0]").value("No existe la oportunidad con id 99"));
    }

    @Test
    void postCrear_validaGeneraIdYDevuelve201() throws Exception {
        OportunidadDto generada = dtoValida();
        generada.setId(5L);
        when(service.crear(any(OportunidadDto.class))).thenReturn(generada);

        mockMvc.perform(post("/api/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoValida())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void postCrear_datosInvalidos_devuelve400() throws Exception {
        OportunidadDto invalida = dtoValida();
        invalida.setNombre("");
        invalida.setTipo(null);
        invalida.setPrioridad(null);

        mockMvc.perform(post("/api/oportunidades")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalida)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.mensajes.length()").value(3));
    }

    @Test
    void putActualizar_devuelveOportunidad() throws Exception {
        OportunidadDto dto = dtoValida();
        dto.setId(1L);
        when(service.actualizar(any(Long.class), any(OportunidadDto.class))).thenReturn(dto);

        mockMvc.perform(put("/api/oportunidades/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Oportunidad de prueba"));
    }

    @Test
    void deleteEliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/oportunidades/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEliminar_inexistente_devuelve404() throws Exception {
        doThrow(new ResourceNotFoundException("No existe la oportunidad con id 99"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/oportunidades/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }
}
