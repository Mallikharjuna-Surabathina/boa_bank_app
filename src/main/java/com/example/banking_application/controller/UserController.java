package com.example.banking_application.controller;

import com.example.banking_application.dto.*;
import com.example.banking_application.service.impl.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Tag(name="User Account management APIs")
public class UserController {
    @Autowired
    UserService userService;

    @Operation(
            summary  ="Creation New User Account",
            description = "Creating a new user and assigning an account ID "
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http status 201 CREADTED"
    )
    @PostMapping()
    public BankResponse createAccount(@RequestBody UserRequest userRequest){
        return userService.creatAccount(userRequest);
    }

    @Operation(
            summary  ="Balance Enquiry",
            description = "Given an account number check how much user has"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http status 201 SUCCESS"
    )
    @GetMapping("balanceEnquiry")
     public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
     }

     @PostMapping("/login")
     public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
     }

     @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }
    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAmount(request);
    }
    @PostMapping("debit")
    public BankResponse debitAmount(@RequestBody CreditDebitRequest request){

        return userService.debtAmount(request);
    }
    @PostMapping("transfer")
    public BankResponse transfer(@RequestBody TransferRequest request){
        return userService.transfer(request);
    }



}
