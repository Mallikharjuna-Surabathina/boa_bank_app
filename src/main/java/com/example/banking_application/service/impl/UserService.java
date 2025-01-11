package com.example.banking_application.service.impl;

import com.example.banking_application.dto.*;

public interface UserService {
    BankResponse creatAccount(UserRequest userRequest);
    BankResponse balanceEnquiry(EnquiryRequest request);
    String nameEnquiry(EnquiryRequest request);
    BankResponse creditAmount(CreditDebitRequest request);
    BankResponse debtAmount(CreditDebitRequest request );

    BankResponse login(LoginDto loginDto);

    BankResponse transfer(TransferRequest request);
}
