/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.bankaccount.aplication;

import reactor.core.publisher.Mono;

/**
 *
 * @author jonavcar
 */
public interface CreditOperations {

    public Mono<Integer> creditCardsByCustomer(String customer);
}
