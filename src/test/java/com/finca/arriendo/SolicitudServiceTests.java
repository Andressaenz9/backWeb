package com.finca.arriendo;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import com.finca.arriendo.dto.SolicitudDto;
import com.finca.arriendo.model.Estado;
import com.finca.arriendo.model.Finca;
import com.finca.arriendo.model.Solicitud;
import com.finca.arriendo.model.Usuario;
import com.finca.arriendo.repository.FincaRepository;
import com.finca.arriendo.repository.SolicitudRepository;
import com.finca.arriendo.repository.UsuarioRepository;
import com.finca.arriendo.services.SolicitudService;

class SolicitudServiceTests {

    @InjectMocks
    private SolicitudService solicitudService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SolicitudRepository solicitudRepository;

    @Mock
    private FincaRepository fincaRepository;

    @Mock
    private ModelMapper modelMapper;

    private Solicitud solicitud;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        solicitud = new Solicitud();
        solicitud.setId(1L);
    }

    @Test
    void testObtenerSolicitudPorId() {
        Solicitud solicitud = new Solicitud();
        solicitud.setId(1L);
        SolicitudDto solicitudDto = new SolicitudDto();
        solicitudDto.setId(1L);

        when(solicitudRepository.findById(1L)).thenReturn(Optional.of(solicitud));
        when(modelMapper.map(solicitud, SolicitudDto.class)).thenReturn(solicitudDto);

        Optional<SolicitudDto> result = solicitudService.obtenerSolicitudPorId(1L);
        assertTrue(result.isPresent());
        assertEquals(solicitudDto.getId(), result.get().getId());
    }

      @Test
    void testObtenerSolicitudesPorFincaId() {
        Long fincaId = 1L;
        Solicitud solicitud1 = new Solicitud();
        solicitud1.setId(1L);
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);

        when(solicitudRepository.findByFincaId(anyLong())).thenReturn(List.of(solicitud1, solicitud2));

        List<SolicitudDto> result = solicitudService.obtenerSolicitudesPorFincaId(fincaId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
    }

    

    @Test
    void testObtenerTodasLasSolicitudes() {
        Solicitud solicitud1 = new Solicitud();
        solicitud1.setId(1L);
        Solicitud solicitud2 = new Solicitud();
        solicitud2.setId(2L);

        SolicitudDto solicitudDto1 = new SolicitudDto();
        solicitudDto1.setId(1L);
        SolicitudDto solicitudDto2 = new SolicitudDto();
        solicitudDto2.setId(2L);

        when(solicitudRepository.findAll()).thenReturn(List.of(solicitud1, solicitud2));
        when(modelMapper.map(solicitud1, SolicitudDto.class)).thenReturn(solicitudDto1);
        when(modelMapper.map(solicitud2, SolicitudDto.class)).thenReturn(solicitudDto2);

        List<SolicitudDto> result = solicitudService.obtenerTodasLasSolicitudes();
        assertEquals(2, result.size());
        assertEquals(solicitudDto1.getId(), result.get(0).getId());
        assertEquals(solicitudDto2.getId(), result.get(1).getId());
    }

    @Test
void testCrearSolicitud() {
    // Crear un DTO de solicitud con valores básicos
    SolicitudDto solicitudDto = new SolicitudDto();
    solicitudDto.setArrendatarioId(1L);
    solicitudDto.setArrendadorId(2L);
    solicitudDto.setFincaId(3L);
    solicitudDto.setFechaInicio(new Date());
    solicitudDto.setFechaFin(new Date());
    solicitudDto.setPrecio(1500.0f);
    solicitudDto.setCantPersonas(5);

    // Mock de entidades relacionadas
    Usuario arrendatario = new Usuario();
    arrendatario.setId(1L);

    Usuario arrendador = new Usuario();
    arrendador.setId(2L);

    Finca finca = new Finca();
    finca.setId(3L);

    // Crear la entidad solicitud resultante para simular la guardada
    Solicitud solicitudGuardada = new Solicitud();
    solicitudGuardada.setId(10L);
    solicitudGuardada.setArrendatario(arrendatario);
    solicitudGuardada.setArrendador(arrendador);
    solicitudGuardada.setFinca(finca);
    solicitudGuardada.setFechaInicio(solicitudDto.getFechaInicio());
    solicitudGuardada.setFechaFin(solicitudDto.getFechaFin());
    solicitudGuardada.setPrecio(solicitudDto.getPrecio());
    solicitudGuardada.setCantPersonas(solicitudDto.getCantPersonas());
    solicitudGuardada.setEstado(Estado.EN_TRAMITE);

    // Configuración de los mocks
    when(usuarioRepository.findById(1L)).thenReturn(Optional.of(arrendatario));
    when(usuarioRepository.findById(2L)).thenReturn(Optional.of(arrendador));
    when(fincaRepository.findById(3L)).thenReturn(Optional.of(finca));
    when(solicitudRepository.save(any(Solicitud.class))).thenReturn(solicitudGuardada);
    when(modelMapper.map(solicitudGuardada, SolicitudDto.class)).thenReturn(new SolicitudDto(
        10L, 1L, 2L, 3L, Estado.EN_TRAMITE, solicitudDto.getFechaInicio(), solicitudDto.getFechaFin(),
        null, null, solicitudDto.getPrecio(), solicitudDto.getCantPersonas(), null, null, false, false
    ));

    // Llamar al servicio
    SolicitudDto result = solicitudService.crearSolicitud(solicitudDto);

    // Verificaciones
    assertNotNull(result, "El resultado no debe ser null");
    assertEquals(10L, result.getId(), "El ID de la solicitud no coincide");
    assertEquals(Estado.EN_TRAMITE, result.getEstado(), "El estado no coincide");
    assertEquals(1L, result.getArrendatarioId(), "El ID del arrendatario no coincide");
    assertEquals(2L, result.getArrendadorId(), "El ID del arrendador no coincide");
    assertEquals(3L, result.getFincaId(), "El ID de la finca no coincide");

    // Verificar interacciones
    verify(usuarioRepository).findById(1L);
    verify(usuarioRepository).findById(2L);
    verify(fincaRepository).findById(3L);
    verify(solicitudRepository).save(any(Solicitud.class));
}





    @Test
    void testEliminarSolicitud() {
        Long id = 1L;

        when(solicitudRepository.existsById(id)).thenReturn(true);
        doNothing().when(solicitudRepository).deleteById(id);

        boolean result = solicitudService.eliminarSolicitud(id);
        assertTrue(result);

        verify(solicitudRepository, times(1)).deleteById(id);
    }

    @Test
    void testActualizarSolicitud() {
        Long id = 1L;
        SolicitudDto solicitudDto = new SolicitudDto();
        solicitudDto.setId(id);

        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setEstado(Estado.ACEPTADA);

        when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenReturn(solicitud);

        Optional<SolicitudDto> result = solicitudService.actualizarSolicitud(id, solicitudDto);
        assertTrue(result.isPresent());
        assertEquals(solicitudDto.getId(), result.get().getId());
    }

    @Test
    void testCalificarArrendatario() {
        Long id = 1L;
        int calificacion = 5;

        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);

        when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenReturn(solicitud);

        Optional<SolicitudDto> result = solicitudService.calificarArrendatario(id, calificacion);
        assertTrue(result.isPresent());
        assertEquals(calificacion, result.get().getCalifArrendatario());
    }

    @Test
    void testCalificarFinca() {
        Long id = 1L;
        int calificacion = 4;

        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);

        when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenReturn(solicitud);

        Optional<SolicitudDto> result = solicitudService.calificarFinca(id, calificacion);
        assertTrue(result.isPresent());
        assertEquals(calificacion, result.get().getCalifFinca());
    }

    @Test
    void testRealizarPago() {
        Long id = 1L;
        String numeroCuenta = "123456789";
        String banco = "Banco Test";

        Solicitud solicitud = new Solicitud();
        solicitud.setId(id);
        solicitud.setNumeroCuenta(""); //El numero de cuenta estará vacio inicialmente
        solicitud.setBanco(""); //El nombre del banco estará vacio inicialmente

        when(solicitudRepository.findById(id)).thenReturn(Optional.of(solicitud));
        when(solicitudRepository.save(solicitud)).thenAnswer(invocation -> {
            Solicitud savedSolicitud = invocation.getArgument(0);
            //Verificar que tanto el número de cuenta y el banco se hayan actualizado de forma correcta
            assertEquals(numeroCuenta,savedSolicitud.getNumeroCuenta());
            assertEquals(banco, savedSolicitud.getBanco());
            return savedSolicitud;
        });

        Optional<SolicitudDto> result = solicitudService.realizarPago(id, numeroCuenta, banco);
        assertTrue(result.isPresent());
        assertEquals(numeroCuenta, result.get().getNumeroCuenta()); 
        assertEquals(banco, result.get().getBanco()); 
    }
}
