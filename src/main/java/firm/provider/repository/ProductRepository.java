package firm.provider.repository;

import firm.provider.model.Firm;
import firm.provider.model.Order;
import firm.provider.model.Product;
import firm.provider.util.LocationType;
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
    public static String INSERT = "INSERT INTO products(location_type, location_id, name, price, producer) VALUES (?,?,?,?,?)";
    public static String SELECT_BY_ORDER = "SELECT * FROM orders_products where order_id=?";
    public static String SELECT_BY_ID = "SELECT * FROM products where id=?";
    public static String SELECT_BY_FIRM_ID = "SELECT * FROM products where location_type=? and location_id=?";

    DataSource dataSource;

    public List<Product> getAllByLocationTypeAndLocationId(LocationType locationType, long locationId) {

        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_LOCATION_TYPE_AND_BY_LOCATION_ID);
            statement.setString(1, locationType.name());
            statement.setLong(2, locationId);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                products.add(new Product(
                        result.getLong("id"),
                        result.getString("name"),
                        result.getString("producer"),
                        result.getFloat("price"),
                        null,
                        LocationType.valueOf(result.getString("location_type")),
                        result.getLong("location_id")
                ));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
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

    protected static List<Product> selectByOrder(DataSource dataSource, List<Order> orders) {
        List<Product> products = new ArrayList<>();

        try (Connection conn = dataSource.getConnection()) {

            PreparedStatement statement = conn.prepareStatement(SELECT_BY_ORDER_LIST);

            for (Order order : orders) {
                statement.setLong(1, order.getId());

                ResultSet result = statement.executeQuery();

                while (result.next()) {
                    products.add(new Product(
                            result.getLong("id"),
                            result.getString("name"),
                            result.getString("producer"),
                            result.getFloat("price"),
                            null,
                            LocationType.valueOf(result.getString("location_type")),
                            result.getLong("location_id")
                    ));
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return products;
    }

    public static Product extractProduct(ResultSet result) throws SQLException {
        return new Product(
                result.getLong("id"),
                result.getString("name"),
                result.getString("producer"),
                result.getFloat("price"),
                null,
                LocationType.valueOf(result.getString("location_type")),
                result.getLong("location_id")
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
}
