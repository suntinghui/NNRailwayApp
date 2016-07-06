package com.zhc.eid.util;

import com.zhc.eid.model.BankEntity;
import com.zhc.eid.model.BankEntityEx;

import java.util.ArrayList;

/**
 * 得到所有的银行信息
 *
 * @author sth
 */
public class BankUtil {

    private static ArrayList<BankEntityEx> bankList = null;

    public static ArrayList<BankEntityEx> getBankList() {
        if (bankList == null) {
            bankList = new ArrayList<BankEntityEx>();

            bankList.add(new BankEntityEx("工商银行", "ICBC", "bank_4", "50000", "50000", "0"));
            bankList.add(new BankEntityEx("农业银行", "ABC", "bank_2", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("招商银行", "CMB", "bank_6", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("建设银行", "CCB", "bank_3", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("中国银行", "BOC", "bank_1", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("中国民生银行", "CMBC", "bank_12", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("浦发银行", "SPDB", "bank_10", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("光大银行", "CEB", "bank_4", "50000", "1000000", "0"));
            bankList.add(new BankEntityEx("兴业银行", "CIB", "bank_11", "50000", "1000000", "0"));
        }

        return bankList;
    }

    public static BankEntityEx getBankFromCode(String code) {
        getBankList();

        for (BankEntityEx bank : bankList) {
            if (bank.getCode().equalsIgnoreCase(code)) {
                return bank;
            }
        }

        return new BankEntityEx("工商银行", "ICBC", "bank_4", "50000", "50000", "0");
    }

}
