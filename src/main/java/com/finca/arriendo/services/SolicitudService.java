package com.finca.arriendo.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.finca.arriendo.dto.SolicitudDto;
import com.finca.arriendo.model.Estado;
import com.finca.arriendo.model.Finca;
import com.finca.arriendo.model.Solicitud;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.FincaRepository;
import com.finca.arriendo.repository.SolicitudRepository;
import com.finca.arriendo.repository.UsuarioRepository;

@Service
public class SolicitudService {

    private final ModelMapper modelMapper;
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final FincaRepository fincaRepository;

    public SolicitudService(SolicitudRepository solicitudRepository, UsuarioRepository usuarioRepository,
                            FincaRepository fincaRepository, ModelMapper modelMapper) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.fincaRepository = fincaRepository;
        this.modelMapper = modelMapper;
    }

    // Crear una nueva solicitud
    public SolicitudDto crearSolicitud(SolicitudDto solicitudDto) {
        // Inicializar la entidad Solicitud
        Solicitud solicitud = new Solicitud();
    
        // Buscar entidades relacionadas
        Usuario arrendatario = usuarioRepository.findById(solicitudDto.getArrendatarioId())
                .orElseThrow(() -> new RuntimeException("Arrendatario no encontrado"));
        Usuario arrendador = usuarioRepository.findById(solicitudDto.getArrendadorId())
                .orElseThrow(() -> new RuntimeException("Arrendador no encontrado"));
        Finca finca = fincaRepository.findById(solicitudDto.getFincaId())
                .orElseThrow(() -> new RuntimeException("Finca no encontrada"));
    
        // Asignar las propiedades a la solicitud
        solicitud.setArrendatario(arrendatario);
        solicitud.setArrendador(arrendador);
        solicitud.setFinca(finca);
        solicitud.setFechaInicio(solicitudDto.getFechaInicio());
        solicitud.setFechaFin(solicitudDto.getFechaFin());
        solicitud.setPrecio(solicitudDto.getPrecio());
        solicitud.setCantPersonas(solicitudDto.getCantPersonas());
        solicitud.setEstado(Estado.EN_TRAMITE); // Estado inicial
    
        // Guardar en la base de datos
        Solicitud nuevaSolicitud = solicitudRepository.save(solicitud);
    
        // Retornar DTO
        return mapToDto(nuevaSolicitud);
    }


    // Obtener todas las solicitudes
    public List<SolicitudDto> obtenerTodasLasSolicitudes() {
        List<Solicitud> solicitudes = solicitudRepository.findAll();
        return solicitudes.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    // Obtener solicitud por ID
    public Optional<SolicitudDto> obtenerSolicitudPorId(Long id) {
        return solicitudRepository.findById(id).map(this::mapToDto);
    }

    // Actualizar solicitud
    public Optional<SolicitudDto> actualizarSolicitud(Long id, SolicitudDto solicitudDto) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(id);
        if (optionalSolicitud.isPresent()) {
            Solicitud solicitud = optionalSolicitud.get();
            mapToEntity(solicitudDto, solicitud);
            Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
            return Optional.of(mapToDto(updatedSolicitud));
        }
        return Optional.empty();
    }

    // Eliminar solicitud
    public boolean eliminarSolicitud(Long id) {
        if (solicitudRepository.existsById(id)) {
            solicitudRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Calificar arrendatario
    public Optional<SolicitudDto> calificarArrendatario(Long id, int calificacion) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(id);
        if (optionalSolicitud.isPresent()) {
            Solicitud solicitud = optionalSolicitud.get();
            solicitud.setCalifArrendatario(calificacion);
            Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
            return Optional.of(mapToDto(updatedSolicitud));
        }
        return Optional.empty();
    }

    // Calificar finca
    public Optional<SolicitudDto> calificarFinca(Long id, int calificacion) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(id);
        if (optionalSolicitud.isPresent()) {
            Solicitud solicitud = optionalSolicitud.get();
            solicitud.setCalifFinca(calificacion);
            Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
            return Optional.of(mapToDto(updatedSolicitud));
        }
        return Optional.empty();
    }

    // Métodos privados para mapear entre DTO y entidad
    private SolicitudDto mapToDto(Solicitud solicitud) {
        if (solicitud == null) {
            return null; 
        }
    
        SolicitudDto dto = new SolicitudDto();
        dto.setId(solicitud.getId());
        dto.setArrendatarioId(solicitud.getArrendatario() != null ? solicitud.getArrendatario().getId() : null);
        dto.setArrendadorId(solicitud.getArrendador() != null ? solicitud.getArrendador().getId() : null);
        dto.setFincaId(solicitud.getFinca() != null ? solicitud.getFinca().getId() : null);
        dto.setEstado(solicitud.getEstado());
        dto.setFechaInicio(solicitud.getFechaInicio());
        dto.setFechaFin(solicitud.getFechaFin());
        dto.setCalifFinca(solicitud.getCalifFinca());
        dto.setCalifArrendatario(solicitud.getCalifArrendatario());
        dto.setPrecio(solicitud.getPrecio());
        dto.setCantPersonas(solicitud.getCantPersonas());
        dto.setNumeroCuenta(solicitud.getNumeroCuenta());
        dto.setBanco(solicitud.getBanco());
        dto.setPagoVisible(solicitud.isPagoVisible());
        dto.setCalificacionVisible(solicitud.isCalificacionVisible());
        return dto;
    }
    

    private void mapToEntity(SolicitudDto solicitudDto, Solicitud solicitud) {
        if (solicitudDto == null || solicitud == null) {
            throw new IllegalArgumentException("SolicitudDto o Solicitud no pueden ser null");
        }
        solicitud.setFechaInicio(solicitudDto.getFechaInicio());
        solicitud.setFechaFin(solicitudDto.getFechaFin());
        solicitud.setCalifFinca(solicitudDto.getCalifFinca());
        solicitud.setCalifArrendatario(solicitudDto.getCalifArrendatario());
        solicitud.setPrecio(solicitudDto.getPrecio());
        solicitud.setCantPersonas(solicitudDto.getCantPersonas());
        solicitud.setNumeroCuenta(solicitudDto.getNumeroCuenta());
        solicitud.setBanco(solicitudDto.getBanco());
    }

    public List<SolicitudDto> findByArrendatarioId(Long id) {
        List<Solicitud> solicitudes = solicitudRepository.findByArrendatarioId(id);
        return solicitudes.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public Optional<SolicitudDto> calificar(Long id, SolicitudDto calificacionDto) {
        Solicitud solicitud = solicitudRepository.findById(id)
        .orElseThrow(() -> {
            throw new ResourceNotFoundException("No se encontró la solicitud con el ID: " + id);
        });

        solicitud.setCalifFinca(calificacionDto.getCalifFinca());
        solicitud.setCalifArrendatario(calificacionDto.getCalifArrendatario());
        Solicitud solicitudActualizada = solicitudRepository.save(solicitud);
        return Optional.of(mapToDto(solicitudActualizada));
    }

    public class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Obtener solicitud por fincaId
    public List<SolicitudDto> obtenerSolicitudesPorFincaId(Long fincaId) {
    List<Solicitud> solicitudes = solicitudRepository.findByFincaId(fincaId);
    return solicitudes.stream()
            .map(this::mapToDto)
            .collect(Collectors.toList());
}



    public Optional<SolicitudDto> realizarPago(Long id, String numeroCuenta, String banco) {
        Optional<Solicitud> optionalSolicitud = solicitudRepository.findById(id);
        
        if (optionalSolicitud.isPresent()) {
            Solicitud solicitud = optionalSolicitud.get();
            // Actualizar solo los detalles de pago
            solicitud.setNumeroCuenta(numeroCuenta); 
            solicitud.setBanco(banco);
            
            // Guardar la solicitud con los nuevos detalles de pago
            Solicitud updatedSolicitud = solicitudRepository.save(solicitud);
            return Optional.of(mapToDto(updatedSolicitud)); 
        }
        return Optional.empty(); 
    }
    
}
