package com.example.banking_application.service.impl;

import com.example.banking_application.config.JwtTokenProvider;
import com.example.banking_application.dto.*;
import com.example.banking_application.entity.Role;
import com.example.banking_application.entity.User;
import com.example.banking_application.repository.UserRepository;
import com.example.banking_application.utils.AccountUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    UserRepository userRepository;
   @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EmailService emailService;

    @Autowired
    TransactionService transactionService;


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtTokenProvider jwtTokenProvider;


    @Override
    public BankResponse creatAccount(UserRequest userRequest) {
        /*
         * Creating an account - saving a new user into db
         * Check if user already has an account
         */
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            return BankResponse.builder()
                    .responseCode((AccountUtils.Account_Exists_Code))
                    .responseMessage(AccountUtils.Account_Exists_Message)
                    .accountInfo(null)
                    .build();

        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .address(userRequest.getAddress())
                .gender(userRequest.getGender())
                .otherName(userRequest.getOtherName())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .accountBalance(BigDecimal.ZERO)
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .PhNumber(userRequest.getPhNumber())
                .status("Active")
                .role(Role.ROLE_ADMIN)
                .build();

        User savedUser = userRepository.save(newUser);
        //Send email Aelert
        EmailDetails emailDetails = EmailDetails.builder()
                .recipients(savedUser.getEmail())
                .subject("Account Creation")
                .messageBody("Congratulations! Your Account Has Been Successfully Created.\n  Your Account Details:\n " +
                        "Account Name:" + savedUser.getFirstName() + " " + savedUser.getLastName() + "  " + savedUser.getOtherName() + "\nAccount Number:" + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getLastName() + " " + savedUser.getOtherName())
                        .accountNumber(savedUser.getAccountNumber())
                        .build())
                .build();


    }
    public BankResponse login(LoginDto loginDto){
        Authentication authentication=null;
        authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword())
        );
        EmailDetails loginAlert= EmailDetails.builder()
                .subject("You are Logged in!")
                .recipients(loginDto.getEmail())
                .messageBody("You logged into your account.If you did not initiate this request,please contact your bank ")
                .build();

        emailService.sendEmailAlert(loginAlert);
        return BankResponse.builder()
                .responseCode("Login Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }




    //Balance Enquiry,Name Enquiry,Credit Debit,Funds Transfer
    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
        //check if the provided account number exists in the db
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.Account_Not_Exists_Code)
                    .responseMessage(AccountUtils.Account_Not_Exists_Message)
                    .accountInfo(null)
                    .build();
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return BankResponse.builder()
                .responseCode(AccountUtils.Account_Found_Code)
                .responseMessage(AccountUtils.Account_Found_Message)
                .accountInfo(AccountInfo.builder()
                        .accountName(foundUser.getFirstName() +" "+foundUser.getLastName()+" "+foundUser.getOtherName())
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(foundUser.getAccountNumber())
                        .build())
                .build();

    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return AccountUtils.Account_Not_Exists_Message;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();


    }

    @Override
    public BankResponse creditAmount(CreditDebitRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.Account_Not_Exists_Code)
                    .responseMessage(AccountUtils.Account_Not_Exists_Message)
                    .accountInfo(null)
                    .build();
        }
       User userToCredit=userRepository.findByAccountNumber(request.getAccountNumber());
       userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));
       userRepository.save(userToCredit);
       //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);


       return BankResponse.builder()
               .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
               .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
               .accountInfo(AccountInfo.builder()
                       .accountNumber(userToCredit.getAccountNumber())
                       .accountName(userToCredit.getFirstName()+" "+userToCredit.getLastName()+" "+userToCredit.getOtherName())
                       .accountBalance(userToCredit.getAccountBalance())
                       .build())
               .build();

    }

    @Override
    public BankResponse debtAmount(CreditDebitRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.Account_Not_Exists_Code)
                    .responseMessage(AccountUtils.Account_Not_Exists_Message)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        BigInteger availableBalance =userToDebit.getAccountBalance().toBigIntegerExact();
        BigInteger debitAmount =request.getAmount().toBigIntegerExact();
        if (availableBalance.intValue() < debitAmount.intValue()) {
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSGAE)
                    .accountInfo(null)
                    .build();
        }
        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountNumber(userToDebit.getAccountNumber())
                            .accountName(userToDebit.getFirstName()+" "+userToDebit.getLastName()+ " "+userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .build())
                    .build();
        }


    }

    @Override
    public BankResponse transfer(TransferRequest request) {
        /*Get the account to debit (check if account exist or not)
        * check if the account has enough amount to debit is not more then current balance or not
        * debit the amount
        * get the account to credit
        *  the amount
        */

        boolean isDestinationAccountNumber= userRepository.existsByAccountNumber(request.getDestinationAccountNumber());
        if(!isDestinationAccountNumber){
            return BankResponse.builder()
                    .responseCode(AccountUtils.Account_Not_Exists_Code)
                    .responseMessage(AccountUtils.Account_Not_Exists_Message)
                    .accountInfo(null)
                    .build();
        }
       User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if(request.getAmount().compareTo(sourceAccountUser.getAccountBalance())>0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSGAE)
                    .accountInfo(null)
                    .build();

        }
        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        String sourceUserName=sourceAccountUser.getFirstName()+" "+sourceAccountUser.getLastName()+" "+sourceAccountUser.getOtherName();
        userRepository.save(sourceAccountUser);
        EmailDetails debitAlert = EmailDetails.builder()
                .subject("SUCCESSFUL DEBITED Rs"+request.getAmount())
                .recipients(sourceAccountUser.getEmail())
                .messageBody("The Sum of "+request.getAmount()+"has been deducted from your account! Your updated balance is "+sourceAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(debitAlert);

        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());
        //String recipientUserName=destinationAccountUser.getFirstName()+" "+destinationAccountUser.getLastName()+" "+destinationAccountUser.getOtherName();
        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        EmailDetails creditAlert = EmailDetails.builder()
                .subject("SUCCESSFUL CREDITED Rs"+request.getAmount())
                .recipients(destinationAccountUser.getEmail())
                .messageBody("The Sum of "+request.getAmount()+"has been sent To your account from "+sourceUserName+"! Your current balance is "+destinationAccountUser.getAccountBalance())
                .build();
        emailService.sendEmailAlert(creditAlert);


        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();

        transactionService.saveTransaction(transactionDto);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }



}
