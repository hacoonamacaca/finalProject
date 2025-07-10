package tw.com.ispan.eeit.controller.food;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.dto.food.FoodDTO;
import tw.com.ispan.eeit.model.entity.food.FoodBean;
import tw.com.ispan.eeit.service.food.FoodService;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @GetMapping
    public ResponseEntity<List<FoodDTO>> getAllFoods() { // 修改返回類型
        List<FoodDTO> foods = foodService.getAllFoodsDTO(); // 調用新的 Service 方法
        return new ResponseEntity<>(foods, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDTO> getFoodById(@PathVariable Integer id) { // 修改返回類型
        Optional<FoodDTO> food = foodService.getFoodDTOById(id); // 調用新的 Service 方法
        return food.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<FoodBean> createFood(@RequestBody FoodBean food) {
        FoodBean createdFood = foodService.createFood(food);
        return new ResponseEntity<>(createdFood, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodBean> updateFood(@PathVariable Integer id, @RequestBody FoodBean foodDetails) {
        FoodBean updatedFood = foodService.updateFood(id, foodDetails);
        if (updatedFood != null) {
            return new ResponseEntity<>(updatedFood, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteFood(@PathVariable Integer id) {
        if (foodService.deleteFood(id)) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
