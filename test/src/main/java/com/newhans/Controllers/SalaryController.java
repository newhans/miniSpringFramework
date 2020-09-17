package com.newhans.Controllers;

import com.newhans.Service.SalaryService;
import com.newhans.beans.AutoWired;
import com.newhans.web.mvc.Controller;
import com.newhans.web.mvc.RequestMapping;
import com.newhans.web.mvc.RequestParam;

@Controller
public class SalaryController {
    @AutoWired
    private SalaryService salaryService;

    @RequestMapping("/get_salary.json")
    public Integer getSalary(@RequestParam("name") String name, @RequestParam("experience") String experience){
        return salaryService.claSalary(Integer.parseInt(experience));
    }
}
