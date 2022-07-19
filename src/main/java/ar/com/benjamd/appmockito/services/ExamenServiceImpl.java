package ar.com.benjamd.appmockito.services;

import ar.com.benjamd.appmockito.models.Examen;
import ar.com.benjamd.appmockito.repositories.ExamenRepository;
import ar.com.benjamd.appmockito.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;

public class ExamenServiceImpl implements ExamenService {

    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImpl(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
        return  examenRepository.findAll()
                .stream()
                .filter(e -> e.getNombre().equals(nombre))
                .findFirst();


    }

    @Override
    public Examen findExamenPorNombreConPregunta(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
            preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty())
                preguntaRepository.guardarVarias(examen.getPreguntas());
        return examenRepository.guardar(examen);

    }


    /*  public Examen findExamenPorNombre(String nombre) {
       Optional<Examen> examenOptional = examenRepository.findAll()
                .stream()
                 .filter(e -> e.getNombre().equals(nombre))
                .findFirst();
       Examen examen = null;
       if(examenOptional.isPresent()){
           examen = examenOptional.orElseThrow();
       }
        return examen;
    }

   */
}
