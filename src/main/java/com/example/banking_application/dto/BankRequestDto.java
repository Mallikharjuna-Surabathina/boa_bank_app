package com.example.banking_application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BankRequestDto {

   private  String accountNumber;
    private String startDate;
    private String endDate;
}
