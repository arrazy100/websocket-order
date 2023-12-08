package com.lunapos.app;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.TransactionManager;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.microprofile.context.ManagedExecutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunapos.dtos.Message;
import com.lunapos.dtos.MessageDecoder;
import com.lunapos.dtos.MessageEncoder;
import com.lunapos.dtos.ProductLineCreateDTO;
import com.lunapos.enums.MessageTypeEnum;
import com.lunapos.interfaces.IOrderService;
import com.lunapos.models.RoomOrder;

@ServerEndpoint(
    value="/linked-order/{id}",
    decoders = MessageDecoder.class,
    encoders = MessageEncoder.class
)
@ApplicationScoped
public class StartWebSocket {
    @Inject
    ManagedExecutor managedExecutor;

    @Inject
    TransactionManager transactionManager;

    @Inject
    IOrderService orderService;

    @Inject
    ObjectMapper objectMapper;

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    private void broadcast(List<UUID> userIds, Message message) {
        userIds.forEach(userId -> {
            Session session = sessions.get(userId.toString());

            if (session != null) {
                try {
                    session.getBasicRemote().sendObject(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        managedExecutor.submit(() -> {
            try {
                // check room id exist
                var roomOrder = RoomOrder.findById(UUID.fromString(id));

                if (roomOrder == null) {
                    Message result = new Message(MessageTypeEnum.NoRoomFound, null);

                    session.getBasicRemote().sendObject(result);

                    return;
                }

                transactionManager.begin();

                var user = orderService.createUserClient(id);

                transactionManager.commit();

                session.getUserProperties().put("userId", user.id.toString());

                sessions.put(user.id.toString(), session);

                Message result = new Message(MessageTypeEnum.AddClientToRoom, user.id);

                session.getBasicRemote().sendObject(result);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    transactionManager.rollback();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @OnClose
    public void onClose(Session session, @PathParam("id") String id) {
        var userId = session.getUserProperties().get("userId");

        session.getUserProperties().remove("userId");
        sessions.remove(userId);
    }

    @OnError
    public void onError(Session session, @PathParam("id") String id, Throwable throwable) {
        var userId = session.getUserProperties().get("userId");

        session.getUserProperties().remove("userId");
        sessions.remove(userId);
    }

    @OnMessage
    public void onMessage(Session session, Message message, @PathParam("id") String id) throws IOException {
        switch(message.type) {
            case AddProductToCart:
                managedExecutor.submit(() -> {
                    try {
                        transactionManager.begin();

                        ProductLineCreateDTO body = objectMapper.convertValue(message.data, ProductLineCreateDTO.class);

                        orderService.addProductToCart(id, session.getUserProperties().get("userId").toString(), body);

                        transactionManager.commit();

                        var roomOrderDTO = orderService.showOrderData(id);

                        Message result = new Message(MessageTypeEnum.ShowDataOrder, roomOrderDTO);

                        broadcast(roomOrderDTO.userClients.stream().map(userClient -> userClient.userId).toList(), result);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            transactionManager.rollback();
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                break;

            case ShowDataOrder:
                break;

            case NoRoomFound:
                break;

            case AddClientToRoom:
                break;
        }
    }
}
