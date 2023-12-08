package com.lunapos.interfaces;

import com.lunapos.dtos.ProductLineCreateDTO;
import com.lunapos.dtos.RoomOrderDTO;
import com.lunapos.models.ProductLine;
import com.lunapos.models.UserClient;

public interface IOrderService {
    RoomOrderDTO showOrderData(String roomId);
    UserClient createUserClient(String roomId);
    ProductLine addProductToCart(String roomId, String userClientId, ProductLineCreateDTO body);
}
