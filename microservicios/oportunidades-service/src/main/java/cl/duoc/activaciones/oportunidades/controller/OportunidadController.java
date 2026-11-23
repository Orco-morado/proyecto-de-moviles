package cl.duoc.activaciones.oportunidades.controller;

import cl.duoc.activaciones.oportunidades.dto.OportunidadDto;
import cl.duoc.activaciones.oportunidades.service.OportunidadService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/oportunidades")
@CrossOrigin(origins = "*")
public class OportunidadController {

    private final OportunidadService service;

    public OportunidadController(OportunidadService service) {
        this.service = service;
    }

    @GetMapping
    public List<OportunidadDto> obtenerTodas() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public OportunidadDto obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OportunidadDto crear(@Valid @RequestBody OportunidadDto dto) {
        return service.crear(dto);
    }

    @PutMapping("/{id}")
    public OportunidadDto actualizar(@PathVariable Long id, @Valid @RequestBody OportunidadDto dto) {
        return service.actualizar(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
