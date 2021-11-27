
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.banck.bankaccount.utils;

/**
 *
 * @author jonavcar
 */
public enum CustomerType {
    PERSONAL("CP") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    PERSONAL_VIP("CPV") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    BUSINESS("CE") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    BUSINESS_PYME("CEP") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    };

    public final String value;

    public boolean equals(String customerType) {
        return value.equals(customerType);
    }

    private CustomerType(String value) {
        this.value = value;
    }
}
