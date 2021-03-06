package org.wecancodeit.shoppingcart;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartItemRestController {

	@Resource
	private CartItemRepository cartRepo;

	@Resource
	private ProductRepository productRepo;

	@GetMapping("/items")
	public Iterable<CartItem> findAllCartItems() {
		return cartRepo.findAll();
	}

	@GetMapping("/itemQuantity")
	public int getCartItemQuantity() {
		return cartRepo.getTotalItemsInCart();
	}

	@PostMapping("/addProduct/{id}/inQuantity/{quantity}")
	public Iterable<CartItem> addProductInQuantity(
		@PathVariable("id") long productId,
		@PathVariable("quantity") int quantity
	) {
		Optional<Product> productOptional = productRepo.findById(productId);
		
		if (!productOptional.isPresent()) {
			// TODO: Report Error
			return cartRepo.findAll();
		}
		
		CartItem item = new CartItem(productOptional.get(), quantity, Status.WIP);
		cartRepo.save(item);
		
		return cartRepo.findAll();
	}
	
	@PutMapping("/updateItem")
	public Iterable<CartItem> updateItemSetQuantity(
		@RequestBody CartItemUpdateRequest cartItemUpdate
	) {
		Optional<CartItem> itemOptional = cartRepo.findById(cartItemUpdate.id);
		
		// If this item doesn't exist, do nothing
		if (!itemOptional.isPresent()) {
			// TODO: Report Error?
			return cartRepo.findAll();
		}
		
		CartItem item = itemOptional.get();
		
		if (cartItemUpdate.quantity == 0) {
			cartRepo.delete(item);
		}
		else {
			item.setQuantity(cartItemUpdate.quantity);
			cartRepo.save(item);
		}
		
		return cartRepo.findAll();
	}
	
	@DeleteMapping("/removeItem/{id}")
	public Iterable<CartItem> removeItem(@PathVariable("id") long id) {
		try {
			cartRepo.deleteById(id);
		} catch (Exception e) {
			// TODO: Report Error (if item didn't exist)
		}
		return cartRepo.findAll();
	}

}
