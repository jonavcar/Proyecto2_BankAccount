
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
    PERSONAL("PJ") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    PERSONAL_VIP("PV") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    BUSINESS("PN") {
        @Override
        public boolean equals(String customerType) {
            return value.equals(customerType);
        }
    },
    BUSINESS_PYME("BP") {
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
