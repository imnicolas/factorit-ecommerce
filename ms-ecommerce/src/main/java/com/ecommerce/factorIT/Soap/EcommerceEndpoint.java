package com.ecommerce.factorIT.Soap;

import com.ecommerce.factorIT.DTO.response.CarritoResponse;
import com.ecommerce.factorIT.DTO.response.ClienteResponse;
import com.ecommerce.factorIT.DTO.response.PageResponse;
import com.ecommerce.factorIT.DTO.response.ProductoResponse;
import com.ecommerce.factorIT.Service.CarritoService;
import com.ecommerce.factorIT.Service.ClienteService;
import com.ecommerce.factorIT.Service.ProductoService;
import com.ecommerce.factorIT.soap.gen.CarritoSoap;
import com.ecommerce.factorIT.soap.gen.ClienteSoap;
import com.ecommerce.factorIT.soap.gen.EstadoCarritoEnum;
import com.ecommerce.factorIT.soap.gen.GetCarritoByIdRequest;
import com.ecommerce.factorIT.soap.gen.GetCarritoByIdResponse;
import com.ecommerce.factorIT.soap.gen.GetClientesVipRequest;
import com.ecommerce.factorIT.soap.gen.GetClientesVipResponse;
import com.ecommerce.factorIT.soap.gen.GetProductosRequest;
import com.ecommerce.factorIT.soap.gen.GetProductosResponse;
import com.ecommerce.factorIT.soap.gen.ProductoSoap;
import com.ecommerce.factorIT.soap.gen.TipoCarritoEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.Namespace;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import org.springframework.ws.soap.server.endpoint.annotation.SoapAction;

/**
 * Endpoint SOAP que expone tres operaciones de consulta.
 *
 * WSDL: http://localhost:8080/ws/ecommerce.wsdl
 * Namespace: http://ecommerce.factorit.com/soap
 *
 * Cada operación declara su SoapAction (SOAP 1.1) para compatibilidad con
 * clientes estrictos (Apache Axis, .NET WCF, JAX-WS legacy).
 */
@Endpoint
@Namespace(prefix = "tns", uri = EcommerceEndpoint.NS)
@RequiredArgsConstructor
public class EcommerceEndpoint {

    static final String NS = "http://ecommerce.factorit.com/soap";

    private final ProductoService productoService;
    private final CarritoService carritoService;
    private final ClienteService clienteService;

    @PayloadRoot(namespace = NS, localPart = "GetProductosRequest")
    @SoapAction(NS + "/GetProductos")
    @ResponsePayload
    public GetProductosResponse getProductos(@RequestPayload GetProductosRequest request) {
        GetProductosResponse response = new GetProductosResponse();
        for (ProductoResponse p : productoService.obtenerProductos()) {
            ProductoSoap soap = new ProductoSoap();
            soap.setId(p.getId());
            soap.setNombre(p.getNombre());
            soap.setPrecio(p.getPrecio());
            response.getProducto().add(soap);
        }
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "GetCarritoByIdRequest")
    @SoapAction(NS + "/GetCarritoById")
    @ResponsePayload
    public GetCarritoByIdResponse getCarritoById(@RequestPayload GetCarritoByIdRequest request) {
        CarritoResponse cart = carritoService.consultarCarrito(request.getId());
        GetCarritoByIdResponse response = new GetCarritoByIdResponse();
        response.setCarrito(toCarritoSoap(cart));
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "GetClientesVipRequest")
    @SoapAction(NS + "/GetClientesVip")
    @ResponsePayload
    public GetClientesVipResponse getClientesVip(@RequestPayload GetClientesVipRequest request) {
        GetClientesVipResponse response = new GetClientesVipResponse();
        PageResponse<ClienteResponse> page = clienteService.obtenerClientesVip(
                request.getAnio(), request.getMes(),
                PageRequest.of(0, 100, Sort.by("nombre").ascending().and(Sort.by("apellido").ascending())));
        for (ClienteResponse c : page.getContent()) {
            response.getCliente().add(toClienteSoap(c));
        }
        return response;
    }

    // ─── helpers de mapeo ─────────────────────────────────────────────

    private CarritoSoap toCarritoSoap(CarritoResponse c) {
        CarritoSoap soap = new CarritoSoap();
        soap.setId(c.getId());
        soap.setTipo(TipoCarritoEnum.fromValue(c.getTipo().name()));
        soap.setEstado(EstadoCarritoEnum.fromValue(c.getState().name()));
        soap.setFecha(c.getDateCreated() != null ? c.getDateCreated().toString() : null);
        soap.setMontoBruto(c.getMontoBruto());
        soap.setDescuento(c.getDescuento());
        soap.setTotal(c.getTotal());
        soap.setCliente(toClienteSoap(c.getCliente()));
        return soap;
    }

    private ClienteSoap toClienteSoap(ClienteResponse c) {
        ClienteSoap soap = new ClienteSoap();
        soap.setId(c.getId());
        soap.setNombre(c.getNombre());
        soap.setApellido(c.getApellido());
        soap.setDni(c.getDni());
        soap.setEsVip(c.isEsVip());
        return soap;
    }
}
