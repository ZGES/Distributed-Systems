package pl.agh.rest.control;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.agh.rest.processing.DataManager;



@RestController
public class PollutionController {

    private DataManager dataManager = new DataManager();

    @GetMapping("/pollution")
    public String pollution(@RequestParam(defaultValue = "Kraków") String cityName){

        return dataManager.htmlBuilder(cityName);

    }
}
