package com.lunapos.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserClientDTO {
    public UUID userId;
    public String userName;
    public List<ProductLineDTO> productLines = new ArrayList<>();
}
