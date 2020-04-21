package project;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import project.city.CityController;

@Controller
public class ResponseController {

    @Autowired
    private CityController cityController;


    @GetMapping(value = "city")
    public String showCities(@RequestParam(value = "search", defaultValue = "ALL" ,required = false) String name, Model model) {
        if (name.equalsIgnoreCase("ALL")){
            model.addAttribute("search" , cityController.all());
        }else model.addAttribute("search", cityController.city(name));
        return "cities";
    }


}