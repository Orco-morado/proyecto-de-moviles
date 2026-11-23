package cl.duoc.activaciones.oportunidades.service;

import cl.duoc.activaciones.oportunidades.dto.OportunidadDto;
import cl.duoc.activaciones.oportunidades.model.EstadoOportunidad;
import cl.duoc.activaciones.oportunidades.model.Prioridad;
import cl.duoc.activaciones.oportunidades.model.TipoEvento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("h2")
class OportunidadServiceIntegrationTest {

    @Autowired
    private OportunidadService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void seedCargaCuatroOportunidadesYPostGeneraIdCinco() {
        List<OportunidadDto> iniciales = service.listar();

        assertEquals(4, iniciales.size());
        assertEquals(List.of(1L, 2L, 3L, 4L), iniciales.stream().map(OportunidadDto::getId).toList());

        OportunidadDto dto = new OportunidadDto();
        dto.setId(0L);
        dto.setNombre("Oportunidad E2E");
        dto.setTipo(TipoEvento.ACTIVACION_COMERCIAL);
        dto.setDescripcion("Prueba de generacion de id");
        dto.setFuenteInformacion("Web");
        dto.setFechaDeteccion(1763740800000L);
        dto.setPrioridad(Prioridad.MEDIA);
        dto.setPotencialComercial("Medio");
        dto.setEstado(EstadoOportunidad.EVENTO_IDENTIFICADO);

        OportunidadDto creada = service.crear(dto);

        assertEquals(5L, creada.getId());
        Integer max = jdbcTemplate.queryForObject("SELECT MAX(id) FROM oportunidades", Integer.class);
        assertEquals(5, max);
        assertTrue(service.listar().size() >= 5);

        System.out.println("SELECT id, nombre, estado, fecha_actualizacion FROM oportunidades ORDER BY id;");
        jdbcTemplate.query(
                "SELECT id, nombre, estado, fecha_actualizacion AS fechaActualizacion FROM oportunidades ORDER BY id",
                (rs, numeroFila) -> {
                    System.out.printf("%d | %s | %s | %d%n",
                            rs.getLong("id"),
                            rs.getString("nombre"),
                            rs.getString("estado"),
                            rs.getLong("fechaActualizacion"));
                    return null;
                }
        );
    }
}
