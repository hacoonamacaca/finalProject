package tw.com.ispan.eeit.controller.food;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.food.FoodClassDTO;
import tw.com.ispan.eeit.service.food.FoodClassService;


@RestController
@RequestMapping("/api/food-classes")
public class FoodClassController {
	@Autowired
	private FoodClassService foodClassService;
	
	@GetMapping
	public List<FoodClassDTO> getAllFoodClasses(){
		return foodClassService.findAllFoodClasses();
	};

}
