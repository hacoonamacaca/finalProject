package tw.com.ispan.eeit.controller.food;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.service.food.FoodService;



@RestController
@RequestMapping("/api/food")
public class FoodController {
	
	@Autowired
    private FoodService foodService;

	
	@GetMapping
	public List<FoodDTO> getAllFoods(){
		return foodService.findAllFoods();
	};
}
