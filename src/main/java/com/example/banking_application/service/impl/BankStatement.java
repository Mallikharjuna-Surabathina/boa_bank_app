package com.example.banking_application.service.impl;

import com.example.banking_application.dto.EmailDetails;
import com.example.banking_application.entity.Transaction;
import com.example.banking_application.entity.User;
import com.example.banking_application.repository.TransactionRepository;
import com.example.banking_application.repository.UserRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class BankStatement {
    private TransactionRepository transactionRepository;
    private UserRepository userRepository;
    private EmailService emailService;

    private static final String FILE="C:\\Users\\surab\\Documents\\MyStatment.pdf";

    /*
    *Retrieve multiple transactions within a date range given an account number
    * Generate a PDF file of transactions
    * send the file via email
    *  */
/*

    public List<Transaction> generateStatement(String accountNumber,String startDate,String endDate) throws DocumentException, FileNotFoundException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end= LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
        List<Transaction> transactionList = transactionRepository.findAll().stream().filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> transaction.getCreatedAt().isEqual(start)).filter(transaction -> transaction.getCreatedAt().isEqual(end)).toList();
     User user = userRepository.findByAccountNumber(accountNumber);
      String customerName = user.getFirstName() +user.getLastName()+  " " +user.getOtherName();

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Setting size of the document");
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document,outputStream);
        document.open();

        PdfPTable bankInfotable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Bank Of America"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress= new PdfPCell(new Phrase("72,CA,USA"));
        bankAddress.setBorder(0);
        bankInfotable.addCell(bankName);
        bankInfotable.addCell(bankAddress);

        PdfPTable statementInfo= new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date:"+startDate));
        customerInfo.setBorder(0);
        PdfPCell statment = new PdfPCell(new Phrase("Statment Of Account"));
        statment.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date:"+endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name :"+customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        PdfPCell address = new PdfPCell(new Phrase("Customer Address"+user.getAddress()));
        address.setBorder(0);




        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRHANSACTION AMOUNT"));
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase( "STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            transactionTable.addCell(new Phrase(transaction.getCreatedAt().toString()));
            transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });
         statementInfo.addCell(customerInfo);
         statementInfo.addCell(statment);
         statementInfo.addCell(endDate);
         statementInfo.addCell(name);
         statementInfo.addCell(space);
         statementInfo.addCell(address);


         document.add(bankInfotable);
         document.add(statementInfo);
         document.add(transactionTable);

         document.close();
        EmailDetails emailDetails=EmailDetails.builder()
                .recipients(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);
        return transactionList;
    }
*/

    public List<Transaction> generateStatement(String accountNumber, String startDate, String endDate)
            throws DocumentException, FileNotFoundException {
        LocalDate start = LocalDate.parse(startDate, DateTimeFormatter.ISO_DATE);
        LocalDate end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);

        List<Transaction> transactionList = transactionRepository.findAll().stream()
                .filter(transaction -> transaction.getAccountNumber().equals(accountNumber))
                .filter(transaction -> {
                    if (transaction.getCreatedAt() == null) {
                        log.warn("Skipping transaction with null createdAt for account: {}", transaction.getAccountNumber());
                        return false;
                    }
                    LocalDate transactionDate;
                    try {
                        // Parse createdAt as LocalDateTime and extract the LocalDate
                        LocalDateTime transactionDateTime = LocalDateTime.parse(transaction.getCreatedAt(),
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
                        transactionDate = transactionDateTime.toLocalDate();
                    } catch (DateTimeParseException e) {
                        log.error("Invalid date format for transaction: {}", transaction.getCreatedAt(), e);
                        return false;
                    }
                    return (transactionDate.isEqual(start) || transactionDate.isAfter(start)) &&
                            (transactionDate.isEqual(end) || transactionDate.isBefore(end));
                })
                .toList();

        if (transactionList.isEmpty()) {
            log.warn("No transactions found for account number {} in the given date range.", accountNumber);
            return List.of();
        }

        User user = userRepository.findByAccountNumber(accountNumber);
        String customerName = user.getFirstName() + " " + user.getLastName() + " " +
                (user.getOtherName() != null ? user.getOtherName() : "");

        Rectangle statementSize = new Rectangle(PageSize.A4);
        Document document = new Document(statementSize);
        log.info("Creating PDF statement for account number: {}", accountNumber);
        OutputStream outputStream = new FileOutputStream(FILE);
        PdfWriter.getInstance(document, outputStream);
        document.open();

        PdfPTable bankInfotable = new PdfPTable(1);
        PdfPCell bankName = new PdfPCell(new Phrase("The Bank Of America"));
        bankName.setBorder(0);
        bankName.setBackgroundColor(BaseColor.BLUE);
        bankName.setPadding(20f);

        PdfPCell bankAddress = new PdfPCell(new Phrase("72, CA, USA"));
        bankAddress.setBorder(0);
        bankInfotable.addCell(bankName);
        bankInfotable.addCell(bankAddress);

        PdfPTable statementInfo = new PdfPTable(2);
        PdfPCell customerInfo = new PdfPCell(new Phrase("Start Date: " + startDate));
        customerInfo.setBorder(0);
        PdfPCell statement = new PdfPCell(new Phrase("Statement Of Account"));
        statement.setBorder(0);
        PdfPCell stopDate = new PdfPCell(new Phrase("End Date: " + endDate));
        stopDate.setBorder(0);
        PdfPCell name = new PdfPCell(new Phrase("Customer Name: " + customerName));
        name.setBorder(0);
        PdfPCell space = new PdfPCell();
        PdfPCell address = new PdfPCell(new Phrase("Customer Address: " + user.getAddress()));
        address.setBorder(0);

        PdfPTable transactionTable = new PdfPTable(4);
        PdfPCell date = new PdfPCell(new Phrase("DATE"));
        date.setBackgroundColor(BaseColor.BLUE);
        date.setBorder(0);
        PdfPCell transactionType = new PdfPCell(new Phrase("TRANSACTION TYPE"));
        transactionType.setBackgroundColor(BaseColor.BLUE);
        transactionType.setBorder(0);
        PdfPCell transactionAmount = new PdfPCell(new Phrase("TRANSACTION AMOUNT"));
        transactionAmount.setBorder(0);
        PdfPCell status = new PdfPCell(new Phrase("STATUS"));
        status.setBackgroundColor(BaseColor.BLUE);
        status.setBorder(0);

        transactionTable.addCell(date);
        transactionTable.addCell(transactionType);
        transactionTable.addCell(transactionAmount);
        transactionTable.addCell(status);

        transactionList.forEach(transaction -> {
            try {
                LocalDateTime transactionDateTime = LocalDateTime.parse(transaction.getCreatedAt(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
                transactionTable.addCell(new Phrase(transactionDateTime.toLocalDate().toString()));
            } catch (DateTimeParseException e) {
                log.error("Invalid date format for transaction: {}", transaction.getCreatedAt(), e);
                transactionTable.addCell(new Phrase("Invalid Date"));
            }
            transactionTable.addCell(new Phrase(transaction.getTransactionType().toString()));
            transactionTable.addCell(new Phrase(transaction.getAmount().toString()));
            transactionTable.addCell(new Phrase(transaction.getStatus()));
        });

        statementInfo.addCell(customerInfo);
        statementInfo.addCell(statement);
        statementInfo.addCell(stopDate);
        statementInfo.addCell(name);
        statementInfo.addCell(space);
        statementInfo.addCell(address);

        document.add(bankInfotable);
        document.add(statementInfo);
        document.add(transactionTable);

        document.close();

        EmailDetails emailDetails = EmailDetails.builder()
                .recipients(user.getEmail())
                .subject("STATEMENT OF ACCOUNT")
                .messageBody("Kindly find your requested account statement attached!")
                .attachment(FILE)
                .build();

        emailService.sendEmailWithAttachment(emailDetails);

        return transactionList;
    }



}
