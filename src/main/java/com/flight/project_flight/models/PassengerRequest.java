//package com.flight.project_flight.models;
//
//import com.fasterxml.jackson.annotation.JsonFormat;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//import com.flight.project_flight.config.CustomLocalDateTimeDeserializer;
//import jakarta.persistence.Column;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Size;
//
//import java.time.LocalDateTime;
//import java.util.Date;
//
//public class PassengerRequest {
//    private Long id;
//    @NotBlank(message = "First name is required")
//    @Size(max = 50, message = "First name cannot exceed 50 characters")
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    @Size(max = 50, message = "Last name cannot exceed 50 characters")
//    private String lastName;
//
//    @NotBlank(message = "Email is required")
//    @Email(message = "Email should be valid")
//    private String email;
//
//
//    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
//    @NotNull(message = "Password is required")
//    @NotBlank(message = "Password cannot be blank")
//    @Column(nullable = true)
//    private String password;
//
//    @Size(max = 15, message = "Phone number cannot exceed 15 characters")
//    private String phone;
//
//    @Size(max = 20, message = "Passport number cannot exceed 20 characters")
//    private String passportNumber;
//
//    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
//    private LocalDateTime dob;
//
//    private Boolean enabled;
//
//    public PassengerRequest() {}
//
//    public PassengerRequest(Long id, String firstName, String lastName, String email, String password, String phone, String passportNumber, LocalDateTime dob) {
//        this.id = id;
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.email = email;
//        this.password = password;
//        this.phone = phone;
//        this.passportNumber = passportNumber;
//        this.dob = dob;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(Long id) {
//        this.id = id;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public void setPhone(String phone) {
//        this.phone = phone;
//    }
//
//    public String getPassportNumber() {
//        return passportNumber;
//    }
//
//    public void setPassportNumber(String passportNumber) {
//        this.passportNumber = passportNumber;
//    }
//
//    public LocalDateTime getDob() {
//        return dob;
//    }
//
//    public void setDob(LocalDateTime dob) {
//        this.dob = dob;
//    }
//
//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }
//}