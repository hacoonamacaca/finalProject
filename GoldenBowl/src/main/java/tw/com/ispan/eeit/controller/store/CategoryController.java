package tw.com.ispan.eeit.controller.store;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.store.CategoryBean;
import tw.com.ispan.eeit.repository.store.CategoryRepository;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/all")
    public List<CategoryBean> getAllCategories() {
        return categoryRepository.findAll();
    }
}