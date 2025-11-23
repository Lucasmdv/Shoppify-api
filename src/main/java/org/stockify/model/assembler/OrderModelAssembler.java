package org.stockify.model.assembler;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import org.stockify.controller.OrderController;
import org.stockify.controller.SaleController;
import org.stockify.dto.request.order.OrderFilterRequest;
import org.stockify.dto.request.sale.SaleFilterRequest;
import org.stockify.dto.response.OrderResponse;
import org.stockify.dto.response.SaleResponse;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderModelAssembler implements RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {
    @Override
    public EntityModel<OrderResponse> toModel(OrderResponse orderResponse) {
        return EntityModel.of(orderResponse,
                linkTo(methodOn(OrderController.class).getOrderById(orderResponse.getId())).withSelfRel(),
                linkTo(methodOn(OrderController.class).getAll(new OrderFilterRequest(),0, 20, null)).withRel("clients"),
                linkTo(methodOn(OrderController.class).deleteOrderById(orderResponse.getId())).withRel("delete"),
                linkTo(methodOn(OrderController.class).putOrder(orderResponse.getId(), null)).withRel("update"),
                linkTo(methodOn(OrderController.class).patchOrder(orderResponse.getId(), null)).withRel("partial-update"));
    }
}
