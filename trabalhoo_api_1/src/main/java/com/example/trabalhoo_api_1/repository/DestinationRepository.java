package com.example.trabalhoo_api_1.repository;

import com.example.trabalhoo_api_1.entity.Destination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DestinationRepository extends JpaRepository<Destination, Long> {




    List<Destination> findByLocalizacaoContainingIgnoreCase(String localizacao);


    List<Destination> findByDisponivel(boolean disponivel);
}
