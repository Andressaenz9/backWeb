package com.finca.arriendo.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finca.arriendo.dto.FincaDto;
import com.finca.arriendo.model.Estado;
import com.finca.arriendo.model.Finca;
import com.finca.arriendo.model.Solicitud;
import com.finca.arriendo.model.Tipo;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.FincaRepository;
import com.finca.arriendo.repository.SolicitudRepository; 
import com.finca.arriendo.repository.UsuarioRepository;

@Service
public class FincaService {

    @Autowired
    FincaRepository fincaRepository;

    @Autowired
    SolicitudRepository solicitudRepository; 
    
    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    ModelMapper modelMapper;

    public FincaDto get(Long id) {
        Optional<Finca> finca = fincaRepository.findById(id);
        return finca.map(value -> modelMapper.map(value, FincaDto.class)).orElse(null);
    }

    public FincaDto get(String nombre) {
        Optional<Finca> fincaOpt = fincaRepository.findByNombre(nombre);
        if (fincaOpt.isPresent()) {
            return modelMapper.map(fincaOpt.get(), FincaDto.class);
        }
        return null;
    }
    

    public List<FincaDto> list() {
        return fincaRepository.findAll().stream()
                .map(finca -> modelMapper.map(finca, FincaDto.class))
                .collect(Collectors.toList());
    }

    public FincaDto saveNew(FincaDto fincaDto) {
        //Crear la nueva instancia de Finca
        Finca finca = new Finca();
        
        // Mapear propiedades de FincaDto a Finca
        finca.setNombre(fincaDto.getNombre());
        finca.setUbicacion(fincaDto.getUbicacion());
        finca.setDisponible(fincaDto.getDisponible());
        finca.setCalificacion(fincaDto.getCalificacion());
        finca.setCapacidad(fincaDto.getCapacidad());
        finca.setDepartamento(fincaDto.getDepartamento());
        finca.setMunicipio(fincaDto.getMunicipio());

        //Para asegurar que el precioDefecto no sea null antes de asignarlo
        if (finca.getPrecioDefecto() == null) {
            finca.setPrecioDefecto(0.0f);
        }else{
            finca.setPrecioDefecto(fincaDto.getPrecioDefecto().floatValue());
        }

        //Guardar la nueva finca en la database (Asignando el ID de forma automatica)
        finca = fincaRepository.save(finca);

        //Asegurarse de que el ID haya sido asignado correctamente a fincaDto antes de retornarlo
        fincaDto.setId(finca.getId());

        return modelMapper.map(finca, FincaDto.class);
    }


    public FincaDto update(FincaDto fincaDto) {
        Finca finca = fincaRepository.findById(fincaDto.getId())
            .orElseThrow(() -> new RuntimeException("Finca no encontrada"));

        // Mapear propiedades de FincaDto a Finca para actualización
        finca.setNombre(fincaDto.getNombre());
        finca.setUbicacion(fincaDto.getUbicacion());
        finca.setDisponible(fincaDto.getDisponible());
        finca.setCalificacion(fincaDto.getCalificacion());
        finca.setCapacidad(fincaDto.getCapacidad());
        finca.setDepartamento(fincaDto.getDepartamento());
        finca.setMunicipio(fincaDto.getMunicipio());
        finca.setPrecioDefecto(fincaDto.getPrecioDefecto().floatValue());

        finca = fincaRepository.save(finca);

        return modelMapper.map(finca, FincaDto.class);
    }

    public void delete(FincaDto fincaDto) {
        Finca finca = modelMapper.map(fincaDto, Finca.class);
        fincaRepository.delete(finca);
    }

    //Función para calificar el arrendatario
    public void calificarArrendatario(Long solicitudId, int calificacion) {
    Optional<Solicitud> solicitudOptional = solicitudRepository.findById(solicitudId);
    if (solicitudOptional.isPresent()) {
        Solicitud solicitud = solicitudOptional.get();
        solicitud.calificarArrendatario(calificacion); 
        solicitudRepository.save(solicitud);
        verificarCalificaciones(solicitud); 
    }
}

private void verificarCalificaciones(Solicitud solicitud) {
    if (solicitud.getCalifFinca() != null && solicitud.getCalifArrendatario() != null) {
        solicitud.setEstado(Estado.CERRADO); 
        solicitudRepository.save(solicitud);
    }
}

    public FincaDto calificarFinca(Long fincaId, int calificacion) {
        Optional<Finca> fincaOptional = fincaRepository.findById(fincaId);
        if (fincaOptional.isPresent()) {
            Finca finca = fincaOptional.get();
            finca.calificar(calificacion); // Método para calificar la finca
            finca = fincaRepository.save(finca);
            return modelMapper.map(finca, FincaDto.class);
        }
        return null; 
    }

    // Método para obtener fincas de un usuario que es arrendador
    public List<FincaDto> getFincasByUsuarioIdAndTipo(Long usuarioId) {
        // Primero, obtener el usuario
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(usuarioId);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getTipo() == Tipo.ARRENDADOR) {
                // Filtrar las fincas por el id del dueño
                return fincaRepository.findByDueno(usuario).stream()
                        .map(finca -> modelMapper.map(finca, FincaDto.class))
                        .collect(Collectors.toList());
            }
        }
        return List.of(); 
    }

    // Método transferir fincas
    public void transferirFinca(Long fincaId, Long usuarioId) throws Exception {
    
    //Buscar la finca por id
    Optional<Finca> optionalFinca = fincaRepository.findById(fincaId);
    
    //Buscar el usuario por id
    Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioId);

    //Verificar que ambos existan
    if (optionalFinca.isPresent() && optionalUsuario.isPresent()) {
    Finca finca = optionalFinca.get();
    Usuario nuevodueno = optionalUsuario.get();

    //Cambiar el dueño de la finca
    finca.setDueno(nuevodueno);
    fincaRepository.save(finca);
    }else{
        throw new Exception("La finca o el usuario no existen");
        }
    }

}