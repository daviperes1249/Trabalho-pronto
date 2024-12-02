package com.example.trabalhoo_api_1.service;

import com.example.trabalhoo_api_1.entity.Destination;
import com.example.trabalhoo_api_1.repository.DestinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {

    @Autowired
    private DestinationRepository destinationRepository;


    public Destination addDestination(Destination destination) {
        return destinationRepository.save(destination);
    }


    public List<Destination> getAllDestinations() {
        return destinationRepository.findAll();
    }

    public Optional<Destination> updateDestination(Long id, Destination updatedDestination) {
        Optional<Destination> existingDestination = destinationRepository.findById(id);

        if (existingDestination.isPresent()) {
            Destination destination = existingDestination.get();
            destination.setNome(updatedDestination.getNome());
            destination.setLocalizacao(updatedDestination.getLocalizacao());
            destination.setDisponivel(updatedDestination.isDisponivel());
            destinationRepository.save(destination);
            return Optional.of(destination);
        }

        return Optional.empty();
    }


    public Optional<Destination> getDestinationById(Long id) {
        return destinationRepository.findById(id);
    }


    public Optional<Destination> reservePackage(Long id) {
        Optional<Destination> destination = destinationRepository.findById(id);
        if (destination.isPresent()) {
            Destination d = destination.get();
            if (!d.isDisponivel()) {
                return Optional.empty();
            }
            d.setDisponivel(false);
            destinationRepository.save(d);
            return Optional.of(d);
        }
        return Optional.empty();
    }


    public boolean deleteDestination(Long id) {
        if (destinationRepository.existsById(id)) {
            destinationRepository.deleteById(id);
            return true;
        }
        return false;
    }





}
