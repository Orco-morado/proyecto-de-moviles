package cl.duoc.activaciones.oportunidades.repository;

import cl.duoc.activaciones.oportunidades.model.Oportunidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OportunidadRepository extends JpaRepository<Oportunidad, Long> {
}
