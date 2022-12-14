package firm.provider.repository;

import firm.provider.model.Firm;
import firm.provider.model.Order;
import firm.provider.model.Product;
import firm.provider.utils.LocationType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("DuplicatedCode")
@Component
@AllArgsConstructor
public class ProductRepository {

    public static String SELECT_BY_LOCATION_TYPE_AND_BY_LOCATION_ID = "SELECT * FROM products where location_type=? and location_id=?";
    public static String SELECT_BY_LOCATION_TYPE = "SELECT * FROM products where location_type=?";
    public static String INSERT = "INSERT INTO products(location_type, location_id, name, price, producer) VALUES (?,?,?,?,?)";
    public static String SELECT_BY_ORDER_ID = "SELECT products_id FROM orders_products where order_id=?";
    public static String SELECT_BY_ID = "SELECT * FROM products where id=?";
    public static String SELECT_ALL = "SELECT * FROM products";
    public static String SELECT_BY_FIRM_ID = "SELECT * FROM products where location_type=? and location_id=?";
    public static String SELECT_BY_PROVIDER_ID = "SELECT * FROM products where location_type=? and location_id=?";
    public static String UPDATE_LOCATION_TYPE_AND_LOCATION_ID_BY_ID = "UPDATE products set location_type=?, location_id=? where id=?";
    public static String SELECT_TOTAL_PRICE_BY_ID = "SELECT price FROM products where id=?";

    DataSource dataSource;

    /*public List<Product> getAllByLocationTypeAndLocationId(LocationType locationType, long locationId) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_LOCATION_TYPE_AND_BY_LOCATION_ID);
            statement.setString(1, locationType.name());
            statement.setLong(2, locationId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }*/

    public List<Product> getAll() {

        List<Product> products = selectAll(dataSource);

        for (Product product : products) {
            product.setOrders(OrderRepository.selectByproductId(dataSource, product.getId()));
            if (product.getLocationType() == LocationType.FIRM_PROVIDER) {
                product.setLocationEntity(ProviderRepository.selectById(dataSource, product.getLocation_id()));
            }
            else if (product.getLocationType() == LocationType.FIRM_COLLECTOR) {
                product.setLocationEntity(FirmRepository.selectById(dataSource, product.getLocation_id()));
            }
        }

        return products;
    }

    public List<Product> getAllById(List<Long> productsId) {

        List<Product> products = selectById(dataSource, productsId);

        for (Product product : products) {
            product.setOrders(OrderRepository.selectByproductId(dataSource, product.getId()));
            if (product.getLocationType() == LocationType.FIRM_PROVIDER) {
                product.setLocationEntity(ProviderRepository.selectById(dataSource, product.getLocation_id()));
            }
            else if (product.getLocationType() == LocationType.FIRM_COLLECTOR) {
                product.setLocationEntity(FirmRepository.selectById(dataSource, product.getLocation_id()));
            }
        }

        return products;
    }

    public List<Product> getAllByLocationType(LocationType locationType) {

        List<Product> products = selectAllByLocationType(dataSource, locationType);

        for (Product product : products) {
            product.setOrders(OrderRepository.selectByproductId(dataSource, product.getId()));
            if (product.getLocationType() == LocationType.FIRM_PROVIDER) {
                product.setLocationEntity(ProviderRepository.selectById(dataSource, product.getLocation_id()));
            }
            else if (product.getLocationType() == LocationType.FIRM_COLLECTOR) {
                product.setLocationEntity(FirmRepository.selectById(dataSource, product.getLocation_id()));
            }
        }

        return products;
    }

    public List<Product> getAllByLocationName(String firm, LocationType locType) {
        long firmId = FirmRepository.selectByName(dataSource, firm).getId();

        List<Product> products = selectAllByLocationTypeAndLocationId(dataSource, locType, firmId);

        for (Product product : products) {
            product.setOrders(OrderRepository.selectByproductId(dataSource, product.getId()));
            if (product.getLocationType() == LocationType.FIRM_PROVIDER) {
                product.setLocationEntity(ProviderRepository.selectById(dataSource, product.getLocation_id()));
            }
            else if (product.getLocationType() == LocationType.FIRM_COLLECTOR) {
                product.setLocationEntity(FirmRepository.selectById(dataSource, product.getLocation_id()));
            }
        }

        return products;
    }

    public List<Product> getAllByLocationTypeAndLocationName(LocationType locType, String locationName) {
        long locationId = 0;

        if (locType == LocationType.FIRM_PROVIDER) {
            locationId = ProviderRepository.selectByName(dataSource, locationName).getId();
        }
        else if (locType == LocationType.FIRM_COLLECTOR) {
            locationId = FirmRepository.selectByName(dataSource, locationName).getId();
        }
        /*else if (locType == LocationType.CLIENT) {
            locationId = ClientRepository.selectByName(dataSource, locationName).getId();
        }*/



        List<Product> products = selectAllByLocationTypeAndLocationId(dataSource, locType, locationId);

        for (Product product : products) {
            product.setOrders(OrderRepository.selectByproductId(dataSource, product.getId()));
            if (product.getLocationType() == LocationType.FIRM_PROVIDER) {
                product.setLocationEntity(ProviderRepository.selectById(dataSource, product.getLocation_id()));
            }
            else if (product.getLocationType() == LocationType.FIRM_COLLECTOR) {
                product.setLocationEntity(FirmRepository.selectById(dataSource, product.getLocation_id()));
            }
        }

        return products;
    }

    public float getTotalPrice(List<Product> products) {
        return selectTotalPriceById(dataSource, products);
    }

    public boolean save(Product product) {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(INSERT);
            statement.setString(1, product.getLocationType().name());
            statement.setLong(2, product.getLocation_id());
            statement.setString(3, product.getName());
            statement.setFloat(4, product.getPrice());
            statement.setString(5, product.getProducer());

            statement.executeUpdate();

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public void updateLocationTypeAndLocationIdById(Product product) {
        updateLocationTypeAndLocationIdById(dataSource, product.getLocationType(), product.getLocation_id(), product.getId());
    }

    public boolean save(List<Product> products) {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(INSERT);

            for (Product product: products) {
                statement.setString(1, product.getLocationType().name());
                statement.setLong(2, product.getLocation_id());
                statement.setString(3, product.getName());
                statement.setFloat(4, product.getPrice());
                statement.setString(5, product.getProducer());
                statement.addBatch();
            }

            statement.executeBatch();

            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    protected static List<Product> selectByOrderId(DataSource dataSource, long id) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ORDER_ID);
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                long productId = result.getLong("products_id");
                products.add(selectById(dataSource, productId));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    private static Product extractProduct(ResultSet result) throws SQLException {
        return new Product(
                result.getLong("id"),
                result.getString("name"),
                result.getString("producer"),
                result.getFloat("price"),
                null,
                LocationType.valueOf(result.getString("location_type")),
                result.getLong("location_id"),
                null
        );
    }

    protected static Product selectById(DataSource dataSource, long id) {

        Product product = null;

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ID);
            statement.setLong(1, id);

            ResultSet result = statement.executeQuery();

            if (result.next()) {
                product = extractProduct(result);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return product;
    }

    protected static List<Product> selectById(DataSource dataSource, List<Long> productsId) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ID);
            for (Long id:
                 productsId) {
                statement.setLong(1, id);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    products.add(extractProduct(result));
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static List<Product> selectAll(DataSource dataSource) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_ALL);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static List<Product> selectByFirmId(DataSource dataSource, long id) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_FIRM_ID);
            statement.setString(1, LocationType.FIRM_COLLECTOR.name());
            statement.setLong(2, id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static List<Product> selectByProviderId(DataSource dataSource, long id) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_FIRM_ID);
            statement.setString(1, LocationType.FIRM_PROVIDER.name());
            statement.setLong(2, id);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static List<Product> selectAllByLocationTypeAndLocationId(DataSource dataSource, LocationType locationType, long locationId) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_LOCATION_TYPE_AND_BY_LOCATION_ID);
            statement.setString(1, locationType.toString());
            statement.setLong(2, locationId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static List<Product> selectAllByLocationType(DataSource dataSource, LocationType locationType) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_LOCATION_TYPE);
            statement.setString(1, locationType.name());

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(extractProduct(result));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    protected static float selectTotalPriceById(DataSource dataSource,List<Product> products) {

        float totalPrice = 0;

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_TOTAL_PRICE_BY_ID);

            for (Product product: products) {
                statement.setLong(1, product.getId());

                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    totalPrice += result.getFloat(1);
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return totalPrice;
    }

    protected static void updateLocationTypeAndLocationIdById(DataSource dataSource, LocationType locationType, long locationId, long id) {

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(UPDATE_LOCATION_TYPE_AND_LOCATION_ID_BY_ID);

            statement.setString(1, locationType.name());
            statement.setLong(2, locationId);
            statement.setLong(3, id);

            statement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
