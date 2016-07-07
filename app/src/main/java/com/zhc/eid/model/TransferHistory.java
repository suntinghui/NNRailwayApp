package com.zhc.eid.model;

import java.io.Serializable;

/**
 * Created by sth on 7/7/16.
 */
public class TransferHistory implements Serializable{

    private String date;
    private String type;
    private String debitloanindicator; // 借贷标记
    private String transferamt; // 交易金额
    private String balance;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDebitloanindicator() {
        return debitloanindicator;
    }

    public void setDebitloanindicator(String debitloanindicator) {
        this.debitloanindicator = debitloanindicator;
    }

    public String getTransferamt() {
        return transferamt;
    }

    public void setTransferamt(String transferamt) {
        this.transferamt = transferamt;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }
}
