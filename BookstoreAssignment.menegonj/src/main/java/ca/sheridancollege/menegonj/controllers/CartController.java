package ca.sheridancollege.menegonj.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.sheridancollege.menegonj.beans.Book;
import ca.sheridancollege.menegonj.services.BookService;
import ca.sheridancollege.menegonj.services.CartService;
import jakarta.servlet.http.HttpSession;

@Controller
public class CartController {

	@Autowired
	private BookService bookService;

	@Autowired
	private CartService cartService;

	@GetMapping("/cart")
	public String viewCart(Model model, HttpSession session) {
		List<Book> cart = (List<Book>) session.getAttribute("cart");
		model.addAttribute("cart", cart != null ? cart : new ArrayList<Book>());
		return "cart";
	}

	@PostMapping("/addToCart/{isbn}")
	public String addToCart(@PathVariable Long isbn, @RequestParam("quantity") int quantity, HttpSession session) {
		Book bookToAdd = bookService.getBookByIsbn(isbn);

		if (bookToAdd != null) {
			bookToAdd.setQuantity(quantity);

			// Retrieve cart from session or initialize it if null
			List<Book> cart = (List<Book>) session.getAttribute("cart");
			if (cart == null) {
				cart = new ArrayList<>();
			}

			// Check if the book already exists in the cart
			boolean bookExists = false;
			for (Book cartBook : cart) {
				if (cartBook.getIsbn().equals(isbn)) {
					cartBook.setQuantity(cartBook.getQuantity() + quantity); // Update quantity
					bookExists = true;
					break;
				}
			}

			// If the book doesn't exist, add it to the cart
			if (!bookExists) {
				cart.add(bookToAdd);
			}

			// Update the cart in the session
			session.setAttribute("cart", cart);
		}

		return "redirect:/books";
	}

	@PostMapping("/updateCart")
	public String updateCart(@ModelAttribute("cart") List<Book> updatedCart, HttpSession session) {
		List<Book> cart = (List<Book>) session.getAttribute("cart");

		for (Book updatedBook : updatedCart) {
			for (Book cartBook : cart) {
				if (updatedBook.getIsbn().equals(cartBook.getIsbn())) {
					int newQuantity = updatedBook.getQuantity();
					if (newQuantity <= 0) {
						cart.remove(cartBook);
					} else {
						cartBook.setQuantity(newQuantity);
					}
					break;
				}
			}
		}

		session.setAttribute("cart", cart);
		return "redirect:/cart";
	}

	@PostMapping("/checkout")
	public String checkout(HttpSession session) {
		// Retrieve the cart from the session and save the order details to your
		// database
		List<Book> cart = cartService.getCartBooks(session);
		// Saving order details logic here

		// Clear the cart after placing the order
		cartService.clearCart(session);

		return "redirect:/cart";
	}
}
