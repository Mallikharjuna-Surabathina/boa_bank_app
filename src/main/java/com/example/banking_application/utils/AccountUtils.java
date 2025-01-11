package com.example.banking_application.utils;

import java.time.Year;

public class AccountUtils {
    public static final String Account_Exists_Code="001";
    public static final String Account_Exists_Message="This user has been already account";
    public  static final String Account_Not_Exists_Code="003";
    public static final String Account_Not_Exists_Message="User With The Provided AccountNumber Does Not Existed ";
    public static final String Account_Found_Code="004";
    public static final String Account_Found_Message="User Account Found";
    public static final String ACCOUNT_CREATION_SUCCESS="002";
    public static final String ACCOUNT_CREATION_MESSAGE="Account creation has been successful";
    public static final String ACCOUNT_CREDITED_SUCCESS="005";
    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE="User Account Was Credited Successfully";
    public static final String INSUFFICIENT_BALANCE_CODE="006";
    public static final String INSUFFICIENT_BALANCE_MESSGAE="Insufficient Funds";
    public static final String ACCOUNT_DEBITED_CODE="007";
    public static final String ACCOUNT_DEBITED_MESSAGE="Account Has Been Successful Debited";
    public static final String TRANSFER_SUCCESSFUL_CODE="008";
    public static final String TRANSFER_SUCCESSFUL_MESSAGE="Transfer Successful";

    public static String generateAccountNumber() {
        /*
         * This class generates a random 6-digit number along with the current year
         * The format will be "2024" followed by a random 6-digit number.
         * For example: 2024123456
         */
        Year currentYear = Year.now();
        int min = 100000;
        int max = 999999;
        // Generate a random number between min (100000) and max (999999)
        int randomNumber = (int) Math.floor(Math.random() * (max - min + 1)) + min;

// convert the currentyear and randomnumber to strings and concate
        String year = String.valueOf(currentYear);
        String randNumber = String.valueOf(randomNumber);
        // Concatenate the year and randomNumber
        String fullAccountNumber = year + randNumber;
        return fullAccountNumber;
    }
}


