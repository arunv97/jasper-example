package com.arun.pdfreport.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Fee {
    private String item;
    private String costPrice;
    private String sellPrice;

}

