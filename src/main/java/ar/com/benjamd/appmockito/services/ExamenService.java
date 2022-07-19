package ar.com.benjamd.appmockito.services;

import ar.com.benjamd.appmockito.models.Examen;

import java.util.Optional;

public interface ExamenService {
    Optional<Examen> findExamenPorNombre(String nombre);
    Examen findExamenPorNombreConPregunta(String nombre);

    Examen guardar(Examen examen);

}
