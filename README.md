# Easy-Shop E-Commerce API

**Developer:** Carlos Henríquez Coss

## 📖 Project Description

This project, **Easy-shop**, is the backend server for an e-commerce application (online store). The system operates using a RESTful API project built in Spring Boot with a MySQL database for secure and efficient data storage. The Easy Shop is a general goods store with anything and everything you might need.

The application is designed following a Spring Boot layered architecture:
* **Controllers (`@RestController`):** Handles HTTP routes, requests/responses, status codes, and security.
* **Services (`@Service`):** Holds the core business logic of the store.
* **Repositories (`@Repository`):** Communicates with the database using Spring Data JPA and JDBC for queries.

### Main Features
* **Authentication & Authorization:** Secure user registration and login using JWT tokens.
* **Catalog Management:** Search, filter, and display products by category.
* **User Roles:** Endpoints restriction (Product and Category CRUD operations) exclusively for administrators (`ADMIN` role).
* **Shopping Cart:** Full functionality for logged-in users to add products, modify quantities, and remove items, maintaining persistence in the database.
* **Profile & Order Management:** Converting the shopping cart into final orders during checkout.

## 🛠️ Technologies Used

* **Backend:** Java, Spring Boot 3
* **Database:** MySQL, Spring Data JPA, JDBC
* **Security:** Spring Security, JSON Web Tokens (JWT)
* **API Testing & Documentation:** Insomnia, Swagger UI

---

## 📸 Application Images

*(Note: Below are the main interfaces this API interacts with. Please update the image paths with your actual screenshots)*

### 1. Main Store Interface
Displays the product catalog where users can search and filter items.
<img width="468" height="318" alt="image" src="https://github.com/user-attachments/assets/941c59d9-a124-4957-995d-eff10d0af205" />


### 2. User Authentication
The login modal requiring valid credentials to generate the JWT token and allow purchases.
<img width="907" height="562" alt="image" src="https://github.com/user-attachments/assets/e47e09af-7f6a-4d05-aaad-8fa6e7f51871" />


### 3. API Documentation (Swagger UI)
The automatically generated interface that allows exploring and testing each API endpoint directly from the browser.
<img width="1873" height="1283" alt="image" src="https://github.com/user-attachments/assets/429ce905-0ff0-4616-9285-3ffe7d94aa4e" />


---

## 💻 Highlighted Code: Shopping Cart Logic

One of the most interesting pieces of this project is the logic implemented in the `ShoppingCartService` for adding products to the cart (Phase 3). 

This code block handles a crucial business rule: **preventing duplicate records** by evaluating the current database state. If the product already exists in the user's cart, the API performs an *update*, increasing the quantity by 1. If the product is not in the cart, it performs an *insert* of the new item with an initial quantity of 1.

```java
@Transactional
public ShoppingCartItem addProductToCart(int userId, int productId) {
    // 1. Retrieve the user's current cart
    ShoppingCart cart = shoppingCartRepository.getCartByUserId(userId);
    
    // 2. Check if the product already exists in the cart
    if (cart.contains(productId)) {
        // Update logic: The product exists, increment the quantity
        ShoppingCartItem existingItem = cart.getItemByProductId(productId);
        int newQuantity = existingItem.getQuantity() + 1;
        
        shoppingCartRepository.updateQuantity(userId, productId, newQuantity);
        existingItem.setQuantity(newQuantity);
        
        return existingItem;
    } else {
        // Insert logic: The product is new to the cart
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
            
        ShoppingCartItem newItem = new ShoppingCartItem(product, 1);
        shoppingCartRepository.addItem(userId, productId, 1);
        
        return newItem;
    }
}
