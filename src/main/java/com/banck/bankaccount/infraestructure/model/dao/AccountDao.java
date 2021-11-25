package com.banck.bankaccount.infraestructure.model.dao;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author jonavcar
 */
@Data
@Document("account")
public class AccountDao {

    @Id
    public String account;
    public String customer;
    public String customerType;
    public String accountType;
    public String dateCreated;
}
