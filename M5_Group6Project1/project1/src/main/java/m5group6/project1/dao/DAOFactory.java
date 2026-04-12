package m5group6.project1.dao;

import m5group6.project1.dao.impl.BookDAOImpl;
import m5group6.project1.dao.impl.LoanDAOImpl;
import m5group6.project1.dao.impl.UserDAOImpl;

public final class DAOFactory {
    private DAOFactory() {}

    public static BookDAO bookDAO() { return new BookDAOImpl(); }
    public static LoanDAO loanDAO() { return new LoanDAOImpl(); }
    public static UserDAO userDAO() { return new UserDAOImpl(); }
}
