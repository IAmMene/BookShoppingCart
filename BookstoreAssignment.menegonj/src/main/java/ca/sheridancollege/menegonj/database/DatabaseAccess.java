package ca.sheridancollege.menegonj.database;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import ca.sheridancollege.menegonj.beans.Book;

@Repository
public class DatabaseAccess {

    @Autowired
    protected NamedParameterJdbcTemplate jdbc;

    public List<Book> getAllBooks() {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        String query = "SELECT * FROM book";
        return jdbc.query(query, namedParameters, new BeanPropertyRowMapper<>(Book.class));
    }

    public Book getBookByIsbn(Long isbn) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isbn", isbn);
        String query = "SELECT * FROM book WHERE isbn = :isbn";
        return jdbc.queryForObject(query, namedParameters, new BeanPropertyRowMapper<>(Book.class));
    }

    public void insertBook(Book book) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("title", book.getTitle());
        namedParameters.addValue("author", book.getAuthor());
        namedParameters.addValue("price", book.getPrice());
        namedParameters.addValue("description", book.getDescription());

        String query = "INSERT INTO book (title, author, price, description) "
                + "VALUES (:title, :author, :price, :description)";

        jdbc.update(query, namedParameters);
    }

    public void updateBook(Book book) {
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("isbn", book.getIsbn());
        namedParameters.addValue("title", book.getTitle());
        namedParameters.addValue("author", book.getAuthor());
        namedParameters.addValue("price", book.getPrice());
        namedParameters.addValue("description", book.getDescription());

        String query = "UPDATE book SET title = :title, author = :author, price = :price, description = :description "
                + "WHERE isbn = :isbn";

        jdbc.update(query, namedParameters);
    }

	public void deleteBookByIsbn(Long isbn) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    String query = "DELETE FROM book WHERE isbn = :isbn";
	    namedParameters.addValue("isbn", isbn);
	    if (jdbc.update(query, namedParameters) > 0) {
	        System.out.println("Deleted book with ISBN " + isbn + " from the database.");
	    }
	}
	
	public void addToCart(Book book) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("isbn", book.getIsbn());
	    namedParameters.addValue("quantity", book.getQuantity());

	    String query = "INSERT INTO cart (isbn, quantity) VALUES (:isbn, :quantity)";
	    jdbc.update(query, namedParameters);
	}

	public void updateCartItemQuantity(Long isbn, int quantity) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("isbn", isbn);
	    namedParameters.addValue("quantity", quantity);

	    String query = "UPDATE cart SET quantity = :quantity WHERE isbn = :isbn";
	    jdbc.update(query, namedParameters);
	}

	public void removeCartItem(Long isbn) {
	    MapSqlParameterSource namedParameters = new MapSqlParameterSource();
	    namedParameters.addValue("isbn", isbn);

	    String query = "DELETE FROM cart WHERE isbn = :isbn";
	    jdbc.update(query, namedParameters);
	}

	public List<Book> getCartItems() {
	    String query = "SELECT * FROM book b JOIN cart c ON b.isbn = c.isbn";
	    return jdbc.query(query, new BeanPropertyRowMapper<>(Book.class));
	}

}
