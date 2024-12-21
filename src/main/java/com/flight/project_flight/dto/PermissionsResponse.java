package com.flight.project_flight.dto;

import lombok.Data;
import java.util.List;

@Data
public class PermissionsResponse {
    private String username;
    private List<String> roles;

    public PermissionsResponse(String username, List<String> roles) {
    }
}
