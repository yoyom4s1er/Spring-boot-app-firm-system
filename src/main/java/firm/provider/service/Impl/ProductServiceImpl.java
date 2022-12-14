package firm.provider.service.Impl;

import firm.provider.model.Product;
import firm.provider.repository.ProductRepository;
import firm.provider.service.ProductService;
import firm.provider.utils.LocationType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public List<Product> getAll() {
        return productRepository.getAll();
    }

    @Override
    public List<Product> getAllByLocationType(LocationType locationType) {
        return productRepository.getAllByLocationType(locationType);
    }

    @Override
    public boolean addProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public List<Product> getAllByLocationName(String firm, LocationType locType) {

        return productRepository.getAllByLocationName(firm, locType);
    }

    @Override
    public List<Product> getAllByLocationTypeAndLocationName(LocationType locType, String locationName) {
        return productRepository.getAllByLocationTypeAndLocationName(locType, locationName);
    }

    @Override
    public List<Product> getAllById(List<Long> productsId) {
        return productRepository.getAllById(productsId);
    }
}
