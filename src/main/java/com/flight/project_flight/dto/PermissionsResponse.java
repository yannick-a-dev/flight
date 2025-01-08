package com.flight.project_flight.dto;

import lombok.Data;

import java.util.List;

@Data
public class PermissionsResponse {
    private String username;
    private List<String> roles;

    public PermissionsResponse(String username, List<String> roles) {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
