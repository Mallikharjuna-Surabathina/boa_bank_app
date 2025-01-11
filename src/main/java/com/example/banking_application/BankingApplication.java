package com.example.banking_application;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title  = "The Banking Application",
                description = "Backend Rest APIs for BOA BAnk",
                version = "v1.0",
                contact =@Contact(
                        name = "Mallikharjuna Surabathina",
                        email="mallikharjuna1770@gmail.com",
                        url="https://github.com/Mallikharjuna-Surabathina/boa_bank_app.git"
        ),license=@License(
                name ="The Banking Application",
                url ="https://github.com/Mallikharjuna-Surabathina/boa_bank_app.git"
        )
        ),externalDocs = @ExternalDocumentation(
                description  ="The Banking Application App Doccumentation",
               url="https://github.com/Mallikharjuna-Surabathina/boa_bank_app.git"
)
)
public class BankingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankingApplication.class, args);
    }

}
