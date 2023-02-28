package com.ilyak.service;

import jakarta.inject.Singleton;

import java.util.regex.Pattern;

@Singleton
public class RegexpService {

    Pattern email = Pattern.compile("");

//    Pattern phone = Pattern.compile("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}$");

    Pattern phone = Pattern.compile("^\\+7[0-9]{10}$");

}
