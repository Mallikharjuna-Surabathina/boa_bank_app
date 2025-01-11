package com.example.banking_application.controller;

import com.example.banking_application.dto.BankRequestDto;
import com.example.banking_application.service.impl.BankStatement;
import com.itextpdf.text.DocumentException;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/bankStatement")
@AllArgsConstructor
public class TransactionController {
    private BankStatement bankStatement;

    @GetMapping
    public List<?> generateBankStatement(@RequestBody BankRequestDto bankRequestDto) throws DocumentException, FileNotFoundException {

            return bankStatement.generateStatement(bankRequestDto.getAccountNumber(),bankRequestDto.getStartDate(),bankRequestDto.getEndDate());

    }

}
