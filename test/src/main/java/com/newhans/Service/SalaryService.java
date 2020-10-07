package com.newhans.Service;

import com.newhans.beans.Component;

@Component
public class SalaryService {
    public Integer claSalary(Integer experience){
        return experience * 5000;
    }
}
