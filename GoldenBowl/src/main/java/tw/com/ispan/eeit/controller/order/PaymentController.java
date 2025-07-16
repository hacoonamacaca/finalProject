package tw.com.ispan.eeit.controller.order;

@RestController
@RequestMapping("/api/ecpay-order")
public class EcpayOrderDetailController {

    @Autowired
    private EcpayOrderDetailRepository repo;

    // 查全部
    @GetMapping
    public List<EcpayOrderDetail> findAll() {
        return repo.findAll();
    }

    // 新增
    @PostMapping
    public EcpayOrderDetail create(@RequestBody EcpayOrderDetail detail) {
        return repo.save(detail);
    }

    // 修改
    @PutMapping("/{id}")
    public EcpayOrderDetail update(@PathVariable Integer id, @RequestBody EcpayOrderDetail detail) {
        detail.setId(id);
        return repo.save(detail);
    }

    // 單筆
    @GetMapping("/{id}")
    public EcpayOrderDetail findOne(@PathVariable Integer id) {
        return repo.findById(id).orElse(null);
    }
}
