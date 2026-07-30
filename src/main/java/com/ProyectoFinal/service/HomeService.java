package com.ProyectoFinal.service;

import com.ProyectoFinal.domain.Home;
import com.ProyectoFinal.repository.HomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HomeService {

    private final HomeRepository homeRepository;

    public HomeService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

   
    @Transactional(readOnly = true)
    public Home getHomePrincipal() {
        return homeRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("El registro Home con ID 1 no existe."));
    }
}
