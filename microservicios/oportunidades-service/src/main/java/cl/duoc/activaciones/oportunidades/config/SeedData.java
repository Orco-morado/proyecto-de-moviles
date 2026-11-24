package cl.duoc.activaciones.oportunidades.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;

@Component
public class SeedData {

    private static final long DIA = 24L * 60 * 60 * 1000;

    private final JdbcTemplate jdbcTemplate;

    public SeedData(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void cargar() {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM oportunidades", Integer.class);
        if (count != null && count > 0) {
            return;
        }

        long ahora = System.currentTimeMillis();
        insertar(1L, "Feria de Beneficios InnovaTech 2026", "FERIA_EMPRESARIAL",
                "Participar con stand de convenios dentales y veterinarios", "LinkedIn",
                ahora - 5 * DIA, 2L, "ALTA", "Alto: 300 trabajadores estimados",
                "CONTACTO_REALIZADO", 1L, ahora - 2 * DIA);
        insertar(2L, "Jornada de Bienestar Municipal", "ACTIVIDAD_MUNICIPAL",
                "Presentar convenios a funcionarios municipales", "Sitio web institucional",
                ahora - 12 * DIA, 2L, "MEDIA", "Medio: 150 funcionarios estimados",
                "PROPUESTA_ENVIADA", 2L, ahora - 1 * DIA);
        insertar(3L, "Expo Familias Plaza Ficticia", "ACTIVACION_COMERCIAL",
                "Activación comercial en centro comercial", "Instagram",
                ahora - 3 * DIA, 1L, "BAJA", "Por evaluar",
                "EVENTO_IDENTIFICADO", 3L, ahora - 3 * DIA);
        insertar(4L, "Feria de Salud Preventiva", "FERIA_INSTITUCIONAL",
                "Stand de convenios de salud en feria interna", "Referencia interna",
                ahora - 60 * DIA, 2L, "MEDIA", "Medio: 200 trabajadores estimados",
                "ACTIVACION_REALIZADA", 4L, ahora - 30 * DIA);

        reiniciarIdentidad();
    }

    private void insertar(Long id, String nombre, String tipo, String descripcion, String fuente,
                          Long fechaDeteccion, Long responsableId, String prioridad,
                          String potencial, String estado, Long eventoId, Long fechaActualizacion) {
        jdbcTemplate.update(
                "INSERT INTO oportunidades (id, nombre, tipo, descripcion, fuente_informacion, " +
                        "fecha_deteccion, responsable_id, prioridad, potencial_comercial, estado, " +
                        "evento_id, fecha_actualizacion, observaciones) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, '')",
                id, nombre, tipo, descripcion, fuente, fechaDeteccion, responsableId, prioridad,
                potencial, estado, eventoId, fechaActualizacion
        );
    }

    private void reiniciarIdentidad() {
        String producto = jdbcTemplate.execute((Connection connection) ->
                connection.getMetaData().getDatabaseProductName());
        if (producto != null && producto.toLowerCase().contains("h2")) {
            jdbcTemplate.execute("ALTER TABLE oportunidades ALTER COLUMN id RESTART WITH 5");
            //agrego soporte para reiniciar la secuencia en postgresql (neon)
        } else if (producto != null && producto.toLowerCase().contains("postgresql")) {
            jdbcTemplate.queryForObject(
                    "SELECT setval(pg_get_serial_sequence('oportunidades', 'id'), 4, true)",
                    Long.class
            );
        } else {
            jdbcTemplate.execute("ALTER TABLE oportunidades AUTO_INCREMENT = 5");
        }
    }
}