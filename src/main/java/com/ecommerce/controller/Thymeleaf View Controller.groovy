import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        model.addAttribute("products", productService.getAll());
        return "products"; // templates/products.html
    }

    @GetMapping("/products/add")
    public String addProductForm() {
        return "add-product"; // templates/add-product.html
    }

    @GetMapping("/products/my-products")
    public String myProducts(Model model) {
        // add filtering logic for user-specific products if needed
        model.addAttribute("myProducts", productService.getAll());
        return "my-products"; // templates/my-products.html
    }
}
