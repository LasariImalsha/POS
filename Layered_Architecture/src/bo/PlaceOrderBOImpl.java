package bo;

import dao.CrudDAO;
import dao.custom.*;
import dao.custom.impl.*;
import db.DBConnection;
import model.CustomerDTO;
import model.ItemDTO;
import model.OrderDTO;
import model.OrderDetailDTO;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class PlaceOrderBOImpl {

    private static final CustomerDAO customerDAO = new CustomerDAOImpl();
    private final ItemDAO itemDAO = new ItemDAOImpl();
    private final OrderDAO orderDAO = new OrderDAOImpl();
    private final OrderDAO orderDAO1 = new OrderDAOImpl();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl();
    private final QueryDAO queryDAO = new QueryDAOImpl();


    public boolean placeOrder(String orderId, LocalDate orderDate, String customerId, List<OrderDetailDTO> orderDetails) throws SQLException, ClassNotFoundException {


            Connection connection = DBConnection.getDbConnection().getConnection();
            /*if order id already exist*/
            if (orderDAO.exist(orderId)) {
            }

            connection.setAutoCommit(false);
            boolean save = orderDAO1.save(new OrderDTO(orderId,orderDate,customerId));

            if (!save) {
                connection.rollback();
                connection.setAutoCommit(true);
                return false;
            }

            for (OrderDetailDTO detail : orderDetails) {
                boolean save1 = orderDetailDAO.save(detail);
                if (!save1) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }

//              //Search & Update Item
               // ItemDTO item = findItem(detail.getItemCode());
                ItemDTO item = null;
                item.setQtyOnHand(item.getQtyOnHand() - detail.getQty());


                boolean update = itemDAO.update(new ItemDTO(item.getDescription(),item.getUnitPrice(),item.getQtyOnHand(),item.getCode()));

                if (!update)                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return false;
                }
            }

            connection.commit();
            connection.setAutoCommit(true);
            return true;
    }

    public  CustomerDTO searchCustomer(String id) throws SQLException,ClassNotFoundException{
        return customerDAO.search(id);
    }

    public ItemDTO searchItem(String code) throws SQLException,ClassNotFoundException{
        return itemDAO.search(code);
    }

    public boolean checkItemIsAvailable(String code) throws SQLException, ClassNotFoundException {
        return itemDAO.exist(code);
    }

    public boolean checkCustomerIsAvailable(String id) throws SQLException, ClassNotFoundException {
        return itemDAO.exist(id);
    }

    public String generateNewOrderId() throws SQLException, ClassNotFoundException {
        return orderDAO.generateNewId();
    }

    public ArrayList<CustomerDTO> getAllCustomers() throws SQLException, ClassNotFoundException {
        return customerDAO.getAll();
    }

    public ArrayList<ItemDTO> getAllItems() throws SQLException, ClassNotFoundException {
        return itemDAO.getAll();
    }

}

