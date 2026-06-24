package org.yearup.service;

import org.springframework.stereotype.Service;
import org.yearup.models.CartItem;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.repository.ShoppingCartRepository;

import java.util.List;

@Service
public class ShoppingCartService
{
    // a shopping cart is built from cart rows plus a product lookup for each row
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductService productService;

    public ShoppingCartService(ShoppingCartRepository shoppingCartRepository, ProductService productService)
    {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productService = productService;
    }

    public ShoppingCart getByUserId(int userId)
    {

        ShoppingCart shoppingCart = new ShoppingCart();

        List<CartItem> items = shoppingCartRepository.findByUserId(userId);

        for (CartItem item : items){
            Product product = productService.getById(item.getProductId());
            item.setProduct(product);
            shoppingCart.add(item);
        }

        // load the user's cart rows, look up each product, and build the ShoppingCart
        return shoppingCart;
    }

    public ShoppingCart addProductToCart(int userId, int productId, int quantity){
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem != null){
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            shoppingCartRepository.save(existingItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUserId(userId);
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            shoppingCartRepository.save(newItem);

        }

        return getByUserId(userId);
    }


    public ShoppingCart updateProductQuantity(int userId, int productId, int quantity){
        CartItem existingItem = shoppingCartRepository.findByUserIdAndProductId(userId, productId);

        if (existingItem != null){
            existingItem.setQuantity(quantity);
            shoppingCartRepository.save(existingItem);
        }
        return getByUserId(userId);
    }

    public ShoppingCart clearCart (int userId){
        shoppingCartRepository.deleteByUserId(userId);
        return getByUserId(userId);
    }
    // add additional methods here
}
