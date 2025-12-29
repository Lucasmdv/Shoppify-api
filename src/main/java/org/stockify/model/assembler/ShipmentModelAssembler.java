package org.stockify.model.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.stockify.controller.ShipmentController;
import org.stockify.dto.request.shipment.ShipmentFilterRequest;
import org.stockify.dto.response.ShipmentResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ShipmentModelAssembler implements RepresentationModelAssembler<ShipmentResponse, EntityModel<ShipmentResponse>> {
    @Override
    public EntityModel<ShipmentResponse> toModel(ShipmentResponse shipmentResponse) {
        return EntityModel.of(shipmentResponse,
                linkTo(methodOn(ShipmentController.class).getOrderById(shipmentResponse.getId())).withSelfRel(),
                linkTo(methodOn(ShipmentController.class).getAll(new ShipmentFilterRequest(),0, 20, null)).withRel("clients"),
                linkTo(methodOn(ShipmentController.class).deleteOrderById(shipmentResponse.getId())).withRel("delete"),
                linkTo(methodOn(ShipmentController.class).patchOrder(shipmentResponse.getId(), null)).withRel("partial-update"));
    }
}
