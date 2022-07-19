package ar.com.benjamd.appmockito.repositories;

import ar.com.benjamd.appmockito.models.Examen;

import java.util.List;

public interface ExamenRepository {
    List<Examen> findAll();

    Examen guardar(Examen examen);

}
