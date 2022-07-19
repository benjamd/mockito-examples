package ar.com.benjamd.appmockito.services;

import ar.com.benjamd.appmockito.Datos;
import ar.com.benjamd.appmockito.models.Examen;
import ar.com.benjamd.appmockito.repositories.ExamenRepository;
import ar.com.benjamd.appmockito.repositories.ExamenRepositoryImpl;
import ar.com.benjamd.appmockito.repositories.PreguntaRepository;
import ar.com.benjamd.appmockito.repositories.PreguntaRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamenServiceImplTest {

    @Mock
    ExamenRepositoryImpl repository;
    @Mock
    PreguntaRepositoryImpl preguntaRepository;
    @InjectMocks
    ExamenServiceImpl service;

    @Captor
    ArgumentCaptor<Long> captor;

    @BeforeEach
    void setUp() {
//        repository = mock(ExamenRepository.class);
//        preguntaRepository = mock(PreguntaRepository.class);
//        service = new ExamenServiceImpl(repository, preguntaRepository);

        /*
            Descomentar si no se usa @ExtendWit(MockitoExtension.class)
            al inicio de la case test
         */
        //MockitoAnnotations.openMocks(this);

    }

    @Test
    void findExamenPorNombre() {

        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");
        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.get().getNombre());

    }

    @Test
    void findExamenPorNombreListaVacia() {
        List<Examen> datos = Collections.emptyList();

        when(repository.findAll()).thenReturn(datos);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");
        assertFalse(examen.isPresent());

    }

    @Test
    void preguntaExamenTest() {
            when(repository.findAll()).thenReturn(Datos.EXAMENES);
            when(preguntaRepository.findPreguntasPorExamenId(7L)).thenReturn(Datos.PREGUNTAS);
            Examen examen = service.findExamenPorNombreConPregunta("Historia");
            assertEquals(5, examen.getPreguntas().size());
            assertTrue(examen.getPreguntas().contains("aritmetica"));
    }

    @Test
    void preguntaExamenTestVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPregunta("Matematicas");
        assertEquals(5, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("integrales"));
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }
    @Test
    void noExisteExamenTestVerify() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        Examen examen = service.findExamenPorNombreConPregunta("Lengua");
        assertNotNull(examen);
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }


    @Test
    void guardarExamentText() {
        //Given Dado un entorno de pruebas - precondiciones
        Examen newExamen =  Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>(){
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        });

        //when cuando ejecutamos
        Examen examen =  service.guardar(newExamen);

        //Then luego validamos con asserts & verify
        assertNotNull(examen);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());

    }

    @Test
    void manejoExceptionText() {
            when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
            when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPregunta("Matematicas");
        });
        assertEquals(IllegalArgumentException.class, exception.getClass());
        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    @Test
    void argumentMatchersTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPregunta("Lengua");

        verify(repository).findAll();
//        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg.equals(5L)));
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >= 6L));
        verify(preguntaRepository).findPreguntasPorExamenId(eq(6L));

    }

    @Test
    void argumentMatchersTest2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPregunta("Lengua");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));

    }

    @Test
    void argumentMatchersTest3() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPregunta("Lengua");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat( (aLong) -> aLong != null && aLong > 0 ));

    }
    public static class MiArgsMatchers implements ArgumentMatcher<Long> {

        private Long argument;
        @Override
        public boolean matches(Long aLong) {
            this.argument = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "Es para un mensaje personalizado de error que imprime mockito" +
                    " en caso de que falle el test: " + argument +
                    " debe ser Long positivo distinto de null";
        }
    }

    @Test
    void argumentCaptorTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPregunta("Matematicas");

        /* Se reemolaza por la anotación @Captor ArgumentCaptor<Long> captor */
        //ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(preguntaRepository).findPreguntasPorExamenId(captor.capture());

        assertEquals(5L, captor.getValue());
        System.out.println("captor = " + captor.getAllValues().toString());

    }

    @Test
    void doThrowTest() {
        Examen examen = Datos.EXAMEN;
        examen.setPreguntas(Datos.PREGUNTAS);
        doThrow(IllegalArgumentException.class).when(preguntaRepository).guardarVarias(anyList());

        assertThrows(IllegalArgumentException.class, () -> {
                service.guardar(examen);
        });
    }

    @Test
    void doAnswerTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            return id == 5L? Datos.PREGUNTAS: Collections.emptyList();
        }).when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPregunta("Matematicas");
        assertAll( () -> {assertEquals(5L,examen.getId());},
                   () -> {assertEquals("Matematicas",examen.getNombre());},
                   () -> {assertEquals(5, examen.getPreguntas().size());},
                   () -> {assertTrue(examen.getPreguntas().contains("geometria"));}
        );

        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }


    @Test
    void doAnswerGuardarExamentText() {
        //Given Dado un entorno de pruebas - precondiciones
        Examen newExamen =  Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);

        doAnswer(new Answer<Examen>(){
            Long secuencia = 8L;
            @Override
            public Examen answer(InvocationOnMock invocationOnMock) throws Throwable {
                Examen examen = invocationOnMock.getArgument(0);
                examen.setId(secuencia++);
                return examen;
            }
        }).when(repository).guardar(any(Examen.class));

        //when cuando ejecutamos
        Examen examen =  service.guardar(newExamen);

        //Then luego validamos con asserts & verify
        assertNotNull(examen);
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());
        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());

    }

    @Test
    void doCallRealMethodTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        /* doCallRealMethod() llama al metodo real cuando se invoca el mock .when(mock).method
        *  solo para clases concretas!
        */
        doCallRealMethod().when(preguntaRepository).findPreguntasPorExamenId(anyLong());

        Examen examen = service.findExamenPorNombreConPregunta("Matematicas");
        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
    }

    @Test
    void spyTest() {
        ExamenRepository examenRepository = spy(ExamenRepositoryImpl.class);
        PreguntaRepository preguntaRepository = spy(PreguntaRepositoryImpl.class);
        ExamenService examenService = new ExamenServiceImpl(examenRepository,preguntaRepository);

        List<String> preguntas = Arrays.asList("aritmetica");
//        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(preguntas);
        doReturn(preguntas).when(preguntaRepository).findPreguntasPorExamenId(anyLong());
        Examen examen = examenService.findExamenPorNombreConPregunta("Matematicas");

        assertEquals(5L, examen.getId());
        assertEquals("Matematicas", examen.getNombre());
        assertEquals(1, examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("aritmetica"));

        verify(examenRepository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());
    }

    @Test
    void ordenDeInvocacionesTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPregunta("Matematicas");
        service.findExamenPorNombreConPregunta("Lengua");

        InOrder inOrder = inOrder(preguntaRepository);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void ordenDeInvocacionesTest2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        service.findExamenPorNombreConPregunta("Matematicas");
        service.findExamenPorNombreConPregunta("Lengua");

        InOrder inOrder = inOrder(repository, preguntaRepository);
        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(5L);

        inOrder.verify(repository).findAll();
        inOrder.verify(preguntaRepository).findPreguntasPorExamenId(6L);
    }

    @Test
    void numeroDeInvocacionesTest() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPregunta("Matematicas");

        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,times(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMost(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5L);
    }
    @Test
    void numeroDeInvocacionesTest2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        service.findExamenPorNombreConPregunta("Matematicas");

//        verify(preguntaRepository).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,times(2)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeast(1)).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atLeastOnce()).findPreguntasPorExamenId(5L);
        verify(preguntaRepository,atMost(2)).findPreguntasPorExamenId(5L);
//        verify(preguntaRepository,atMostOnce()).findPreguntasPorExamenId(5L);
    }

    @Test
    void numeroInvocacionesTest3() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        service.findExamenPorNombreConPregunta("Matematicas");

        verify(preguntaRepository,never()).findPreguntasPorExamenId(5L);
        verifyNoInteractions(preguntaRepository);

        verify(repository).findAll();
        verify(repository,times(1)).findAll();
        verify(repository,atLeast(1)).findAll();
        verify(repository,atLeastOnce()).findAll();
        verify(repository,atMost(10)).findAll();
        verify(repository,atMostOnce()).findAll();

    }
}