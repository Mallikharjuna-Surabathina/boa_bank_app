package com.example.banking_application.service.impl;

import com.example.banking_application.dto.TransactionDto;
import com.example.banking_application.entity.Transaction;

public interface TransactionService {
    void saveTransaction(TransactionDto transactionDto);
}
