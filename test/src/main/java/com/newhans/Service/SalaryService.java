package com.newhans.Service;

import com.newhans.beans.Bean;

@Bean
public class SalaryService {
    public Integer claSalary(Integer experience){
        return experience * 5000;
    }
}
