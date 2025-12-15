package com.travel.flightservice.service;

import com.travel.flightservice.dto.FlightAvailabilityDTO;
import com.travel.flightservice.dto.FlightDTO;
import com.travel.flightservice.entity.Flight;
import com.travel.flightservice.exception.ResourceNotFoundException;
import com.travel.flightservice.exception.DuplicateResourceException;
import com.travel.flightservice.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FlightService {
    private final FlightRepository flightRepository;

    @Transactional
    public FlightDTO createFlight(FlightDTO flightDTO) {
        if (flightRepository.existsByFlightNumber(flightDTO.getFlightNumber())) {
            throw new DuplicateResourceException("Flight number already exists: " + flightDTO.getFlightNumber());
        }

        Flight flight = mapToEntity(flightDTO);
        flight.setAvailableSeats(flight.getTotalSeats());
        Flight savedFlight = flightRepository.save(flight);
        log.info("Created flight: {}", savedFlight.getFlightNumber());
        return mapToDTO(savedFlight);
    }

    public FlightDTO getFlightById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));
        return mapToDTO(flight);
    }

    public List<FlightDTO> getAllFlights() {
        return flightRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public FlightAvailabilityDTO checkAvailability(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        log.info("Checking availability for flight: {}, Available seats: {}",
                flight.getFlightNumber(), flight.getAvailableSeats());

        if (!flight.getActive()) {
            return FlightAvailabilityDTO.unavailable(id, flight.getFlightNumber(), "Flight is inactive");
        }

        if (flight.getAvailableSeats() <= 0) {
            return FlightAvailabilityDTO.unavailable(id, flight.getFlightNumber(), "No seats available");
        }

        return FlightAvailabilityDTO.available(
                id,
                flight.getFlightNumber(),
                flight.getAvailableSeats(),
                flight.getPrice()
        );
    }

    @Transactional
    public boolean reserveSeat(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with id: " + id));

        if (flight.getAvailableSeats() > 0) {
            flight.setAvailableSeats(flight.getAvailableSeats() - 1);
            flightRepository.save(flight);
            log.info("Reserved seat for flight: {}, Remaining seats: {}",
                    flight.getFlightNumber(), flight.getAvailableSeats());
            return true;
        }
        return false;
    }

    private FlightDTO mapToDTO(Flight flight) {
        return new FlightDTO(
                flight.getId(),
                flight.getFlightNumber(),
                flight.getAirline(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureTime(),
                flight.getArrivalTime(),
                flight.getPrice(),
                flight.getTotalSeats(),
                flight.getAvailableSeats(),
                flight.getActive()
        );
    }

    private Flight mapToEntity(FlightDTO dto) {
        Flight flight = new Flight();
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirline(dto.getAirline());
        flight.setOrigin(dto.getOrigin());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setPrice(dto.getPrice());
        flight.setTotalSeats(dto.getTotalSeats());
        flight.setActive(true);
        return flight;
    }
}