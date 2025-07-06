package tw.com.ispan.eeit.controller.store;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tw.com.ispan.eeit.model.entity.store.StoreBean;
import tw.com.ispan.eeit.service.store.StoreService;

@RestController
@RequestMapping("/api/store")
public class StoreController {
    @Autowired
    private StoreService storeService;

    @PostMapping("/registerInfo")
    public Map<String, Object> register(@RequestBody Map<String, Object> map) {
        Integer ownerId = (Integer) map.get("owner");
        String name = (String) map.get("name");
        String storeCategory = (String) map.get("storeCategory");
        String intro = (String) map.get("storeIntro");

        StoreBean store = storeService.registerStore(ownerId, name, storeCategory, intro);
        return Map.of("success", true, "storeId", store.getId());
    }
}
