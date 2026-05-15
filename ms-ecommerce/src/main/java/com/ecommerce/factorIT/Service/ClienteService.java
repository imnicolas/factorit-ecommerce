package com.ecommerce.factorIT.Service;

import com.ecommerce.factorIT.Constants;
import com.ecommerce.factorIT.DTO.response.ClienteResponse;
import com.ecommerce.factorIT.DTO.response.PageResponse;
import com.ecommerce.factorIT.Repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    /**
     * VIPs del mes consultado.
     * Regla: un cliente es VIP en un mes si SUM(carritos COMPLETADO en ese mes) > $10.000.
     * Es 100% dinámico; no depende del flag esVip ni de tabla de historial.
     */
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> obtenerClientesVip(int anio, int mes, Pageable pageable) {
        Page<ClienteResponse> resultado = clienteRepository
                .findVipsEnMes(anio, mes, Constants.VIP_THRESHOLD, pageable)
                .map(ClienteResponse::from);
        return PageResponse.from(resultado);
    }

    /**
     * Ganaron VIP en mes X: son VIP en X y NO eran VIP en (X-1).
     */
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> obtenerClientesQueGanaronVip(int anio, int mes, Pageable pageable) {
        LocalDate mesAnterior = LocalDate.of(anio, mes, 1).minusMonths(1);
        Page<ClienteResponse> resultado = clienteRepository
                .findNuevosVipEnMes(anio, mes, mesAnterior.getYear(), mesAnterior.getMonthValue(), Constants.VIP_THRESHOLD, pageable)
                .map(ClienteResponse::from);
        return PageResponse.from(resultado);
    }

    /**
     * Perdieron VIP en mes X: eran VIP en (X-1) y NO son VIP en X.
     */
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> obtenerClientesQuePerdieronVip(int anio, int mes, Pageable pageable) {
        LocalDate mesAnterior = LocalDate.of(anio, mes, 1).minusMonths(1);
        Page<ClienteResponse> resultado = clienteRepository
                .findPerdieronVipEnMes(anio, mes, mesAnterior.getYear(), mesAnterior.getMonthValue(), Constants.VIP_THRESHOLD, pageable)
                .map(ClienteResponse::from);
        return PageResponse.from(resultado);
    }

    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> buscarClientes(String search, Pageable pageable) {
        Page<ClienteResponse> resultado = clienteRepository.buscarPaginado(search, pageable)
                .map(ClienteResponse::from);
        return PageResponse.from(resultado);
    }
}
