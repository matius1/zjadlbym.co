package pl.edu.pk.fmi.zjadlbym.co.config;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/przepis")
public class RestController {

    @RequestMapping("/get")
    public String przepisyGet(){

        return "przepis";
    }

}
