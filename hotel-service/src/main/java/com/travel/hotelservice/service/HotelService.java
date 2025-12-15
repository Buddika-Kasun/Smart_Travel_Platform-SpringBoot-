package com.travel.hotelservice.service;

import com.travel.hotelservice.dto.HotelAvailabilityDTO;
import com.travel.hotelservice.dto.HotelDTO;
import com.travel.hotelservice.entity.Hotel;
import com.travel.hotelservice.exception.ResourceNotFoundException;
import com.travel.hotelservice.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {
    private final HotelRepository hotelRepository;

    @Transactional
    public HotelDTO createHotel(HotelDTO hotelDTO) {
        Hotel hotel = mapToEntity(hotelDTO);
        hotel.setAvailableRooms(hotel.getTotalRooms());
        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Created hotel: {}", savedHotel.getName());
        return mapToDTO(savedHotel);
    }

    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return mapToDTO(hotel);
    }

    public List<HotelDTO> getAllHotels() {
        return hotelRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public HotelAvailabilityDTO checkAvailability(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        log.info("Checking availability for hotel: {}, Available rooms: {}",
                hotel.getName(), hotel.getAvailableRooms());

        if (!hotel.getActive()) {
            return HotelAvailabilityDTO.unavailable(id, hotel.getName(), "Hotel is inactive");
        }

        if (hotel.getAvailableRooms() <= 0) {
            return HotelAvailabilityDTO.unavailable(id, hotel.getName(), "No rooms available");
        }

        return HotelAvailabilityDTO.available(
                id,
                hotel.getName(),
                hotel.getAvailableRooms(),
                hotel.getPricePerNight()
        );
    }

    @Transactional
    public boolean reserveRoom(Long id) {
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        if (hotel.getAvailableRooms() > 0) {
            hotel.setAvailableRooms(hotel.getAvailableRooms() - 1);
            hotelRepository.save(hotel);
            log.info("Reserved room for hotel: {}, Remaining rooms: {}",
                    hotel.getName(), hotel.getAvailableRooms());
            return true;
        }
        return false;
    }

    private HotelDTO mapToDTO(Hotel hotel) {
        return new HotelDTO(
                hotel.getId(),
                hotel.getName(),
                hotel.getLocation(),
                hotel.getAddress(),
                hotel.getStarRating(),
                hotel.getPricePerNight(),
                hotel.getTotalRooms(),
                hotel.getAvailableRooms(),
                hotel.getAmenities(),
                hotel.getActive()
        );
    }

    private Hotel mapToEntity(HotelDTO dto) {
        Hotel hotel = new Hotel();
        hotel.setName(dto.getName());
        hotel.setLocation(dto.getLocation());
        hotel.setAddress(dto.getAddress());
        hotel.setStarRating(dto.getStarRating());
        hotel.setPricePerNight(dto.getPricePerNight());
        hotel.setTotalRooms(dto.getTotalRooms());
        hotel.setAmenities(dto.getAmenities());
        hotel.setActive(true);
        return hotel;
    }
}