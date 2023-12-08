package com.lunapos.services;

import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;

import org.eclipse.microprofile.context.ManagedExecutor;

import com.lunapos.dtos.ProductLineCreateDTO;
import com.lunapos.dtos.ProductLineDTO;
import com.lunapos.dtos.RoomOrderDTO;
import com.lunapos.dtos.UserClientDTO;
import com.lunapos.interfaces.IOrderService;
import com.lunapos.models.OrderCart;
import com.lunapos.models.ProductLine;
import com.lunapos.models.RoomOrder;
import com.lunapos.models.UserClient;

@ApplicationScoped
public class OrderService implements IOrderService {
    // private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    TransactionManager transactionManager;

    public RoomOrderDTO showOrderData(String roomId) {
        RoomOrder roomOrder = RoomOrder.findById(UUID.fromString(roomId));

        RoomOrderDTO roomOrderDTO = new RoomOrderDTO();
        roomOrderDTO.userClients = roomOrder.userClients.stream().map(userClient -> {
            var userClientDTO = new UserClientDTO();
            userClientDTO.userId = userClient.id;
            userClientDTO.userName = userClient.name;
            userClientDTO.productLines = userClient.productLines.stream().map(productLine -> {
                var productLineDTO = new ProductLineDTO();
                productLineDTO.productId = productLine.productId.toString();
                productLineDTO.productVariantId = productLine.productVariantId.toString();
                productLineDTO.qty = productLine.qty;

                return productLineDTO;
            }).toList();

            return userClientDTO;
        }).toList();

        return roomOrderDTO;
    }

    public UserClient createUserClient(String roomId) {
        UserClient userClient = new UserClient();

        RoomOrder roomOrder = RoomOrder.findById(UUID.fromString(roomId));

        // count user client on room
        Long countUserClient = UserClient.count("room_order_id", UUID.fromString(roomId));

        userClient.roomOrder = roomOrder;
        userClient.name = "Guest " + (countUserClient + 1);

        userClient.persist();

        return userClient;
    }

    public ProductLine addProductToCart(String roomId, String userClientId, ProductLineCreateDTO body) {
        // get cart by room id
        OrderCart orderCart = OrderCart.find("room_order_id", UUID.fromString(roomId)).firstResult();

        // if cart not found, create new cart
        if (orderCart == null) {
            // get room by id
            RoomOrder roomOrder = RoomOrder.findById(UUID.fromString(roomId));

            orderCart = new OrderCart();
            orderCart.roomOrder = roomOrder;
            orderCart.persist();
        }

        // get user client by id
        UserClient userClient = UserClient.findById(UUID.fromString(userClientId));

        // add product to cart
        ProductLine productLine = new ProductLine();           
        productLine.orderCart = orderCart;
        productLine.userClient = userClient;
        productLine.productId = body.productId;
        productLine.productVariantId = body.productVariantId;
        productLine.qty = body.qty;
        
        productLine.persist();

        return productLine;
    }
}
