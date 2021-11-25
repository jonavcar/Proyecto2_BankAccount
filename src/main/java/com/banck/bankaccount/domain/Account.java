package com.banck.bankaccount.domain;

import lombok.Data;

/**
 *
 * @author jonavcar
 */
@Data
public class Account {

    public String account;
    public String customer;
    public String customerType;
    public String accountType;
    public String dateCreated;
}
