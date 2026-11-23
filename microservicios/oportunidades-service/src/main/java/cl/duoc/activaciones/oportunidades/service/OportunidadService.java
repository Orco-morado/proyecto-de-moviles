package cl.duoc.activaciones.oportunidades.service;

import cl.duoc.activaciones.oportunidades.dto.OportunidadDto;
import cl.duoc.activaciones.oportunidades.exception.ResourceNotFoundException;
import cl.duoc.activaciones.oportunidades.model.EstadoOportunidad;
import cl.duoc.activaciones.oportunidades.model.Oportunidad;
import cl.duoc.activaciones.oportunidades.repository.OportunidadRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OportunidadService {

    private final OportunidadRepository repository;

    public OportunidadService(OportunidadRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<OportunidadDto> listar() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public OportunidadDto obtener(Long id) {
        return repository.findById(id).map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la oportunidad con id " + id));
    }

    public OportunidadDto crear(OportunidadDto dto) {
        Oportunidad oportunidad = toEntity(dto);
        oportunidad.setId(null);
        if (oportunidad.getEstado() == null) {
            oportunidad.setEstado(EstadoOportunidad.EVENTO_IDENTIFICADO);
        }
        oportunidad.setFechaActualizacion(System.currentTimeMillis());
        return toDto(repository.save(oportunidad));
    }

    public OportunidadDto actualizar(Long id, OportunidadDto dto) {
        Oportunidad existente = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la oportunidad con id " + id));
        Oportunidad oportunidad = toEntity(dto);
        oportunidad.setId(existente.getId());
        if (oportunidad.getEstado() == null) {
            oportunidad.setEstado(existente.getEstado());
        }
        oportunidad.setFechaActualizacion(System.currentTimeMillis());
        return toDto(repository.save(oportunidad));
    }

    public void eliminar(Long id) {
        Oportunidad oportunidad = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la oportunidad con id " + id));
        repository.delete(oportunidad);
    }

    private OportunidadDto toDto(Oportunidad oportunidad) {
        OportunidadDto dto = new OportunidadDto();
        dto.setId(oportunidad.getId());
        dto.setNombre(oportunidad.getNombre());
        dto.setTipo(oportunidad.getTipo());
        dto.setDescripcion(oportunidad.getDescripcion());
        dto.setFuenteInformacion(oportunidad.getFuenteInformacion());
        dto.setFechaDeteccion(oportunidad.getFechaDeteccion());
        dto.setResponsableId(oportunidad.getResponsableId());
        dto.setPrioridad(oportunidad.getPrioridad());
        dto.setPotencialComercial(oportunidad.getPotencialComercial());
        dto.setEstado(oportunidad.getEstado());
        dto.setEventoId(oportunidad.getEventoId());
        dto.setFechaActualizacion(oportunidad.getFechaActualizacion());
        dto.setObservaciones(oportunidad.getObservaciones());
        return dto;
    }

    private Oportunidad toEntity(OportunidadDto dto) {
        Oportunidad oportunidad = new Oportunidad();
        oportunidad.setNombre(dto.getNombre());
        oportunidad.setTipo(dto.getTipo());
        oportunidad.setDescripcion(dto.getDescripcion());
        oportunidad.setFuenteInformacion(dto.getFuenteInformacion());
        oportunidad.setFechaDeteccion(dto.getFechaDeteccion());
        oportunidad.setResponsableId(dto.getResponsableId());
        oportunidad.setPrioridad(dto.getPrioridad());
        oportunidad.setPotencialComercial(dto.getPotencialComercial());
        oportunidad.setEstado(dto.getEstado());
        oportunidad.setEventoId(dto.getEventoId());
        oportunidad.setFechaActualizacion(dto.getFechaActualizacion());
        oportunidad.setObservaciones(dto.getObservaciones());
        return oportunidad;
    }
}
