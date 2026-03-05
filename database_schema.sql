-- Complete database schema with correct data types
-- This schema matches the entity definitions in the Java code

DROP DATABASE IF EXISTS ecommercephone;

CREATE DATABASE ecommercephone CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ecommercephone;

-- Drop tables in reverse order of dependencies
DROP TABLE IF EXISTS order_history;
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS guest_cart_items;
DROP TABLE IF EXISTS cart_items;
DROP TABLE IF EXISTS addresses;
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS product_images;
DROP TABLE IF EXISTS product_attributes;
DROP TABLE IF EXISTS attribute_values;
DROP TABLE IF EXISTS attributes;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS brands;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS profiles;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS phone;  -- Remove old phone table (replaced by products)

-- Create accounts table
CREATE TABLE accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    uid VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    status ENUM('ACTIVE','BLOCKED','PENDING') DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create profiles table
CREATE TABLE profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_uid VARCHAR(50),
    full_name VARCHAR(100),
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    date_of_birth DATE,
    address_line VARCHAR(255),
    city VARCHAR(100),
    country VARCHAR(100) DEFAULT 'Vietnam'
);

-- Create categories table - FIXED: id is BIGINT to match entity
CREATE TABLE categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

-- Create brands table
CREATE TABLE brands (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create products table - FIXED: category_id is BIGINT to match categories.id
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    brand_id INT,
    image_url VARCHAR(255),
    price DECIMAL(10,2) NOT NULL,
    description TEXT,
    category_id BIGINT,  -- FIXED: Changed from INT to BIGINT
    stock INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brands(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Create product_images table
CREATE TABLE product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    image_path VARCHAR(255),
    is_primary BOOLEAN DEFAULT FALSE,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create attributes table
CREATE TABLE attributes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- Create attribute_values table
CREATE TABLE attribute_values (
    id INT AUTO_INCREMENT PRIMARY KEY,
    attribute_id INT,
    value VARCHAR(100),
    FOREIGN KEY (attribute_id) REFERENCES attributes(id)
);

-- Create product_attributes table
CREATE TABLE product_attributes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT,
    attribute_id INT,
    attribute_value_id INT,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (attribute_id) REFERENCES attributes(id),
    FOREIGN KEY (attribute_value_id) REFERENCES attribute_values(id)
);

-- Create cart_items table
CREATE TABLE cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_uid VARCHAR(50),
    product_id BIGINT,
    quantity INT DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create guest_cart_items table
CREATE TABLE guest_cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    session_token VARCHAR(100) NOT NULL,
    product_id BIGINT,
    quantity INT DEFAULT 1,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
);

-- Create addresses table
CREATE TABLE addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_uid VARCHAR(50),
    recipient_name VARCHAR(100),
    phone VARCHAR(20),
    address_line VARCHAR(255),
    city VARCHAR(100),
    state VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100) DEFAULT 'Vietnam'
);

-- Create payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    method ENUM('COD','BANK_TRANSFER','CREDIT_CARD','MOMO') DEFAULT 'COD',
    status ENUM('PENDING','COMPLETED','FAILED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP NULL
);

-- Create orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_uid VARCHAR(50),
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status ENUM('PROCESSING','SHIPPED','DELIVERED','CANCELLED') DEFAULT 'PROCESSING',
    total_amount DECIMAL(10,2),
    address_id BIGINT,
    payment_id BIGINT,
    FOREIGN KEY (address_id) REFERENCES addresses(id),
    FOREIGN KEY (payment_id) REFERENCES payments(id)
);

-- Create order_items table
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    product_id BIGINT,
    quantity INT,
    price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- Create order_history table
CREATE TABLE order_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT,
    old_status ENUM('PROCESSING','SHIPPED','DELIVERED','CANCELLED'),
    new_status ENUM('PROCESSING','SHIPPED','DELIVERED','CANCELLED'),
    changed_by_uid VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    note VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(id)
);

-- Create notifications table
CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_uid VARCHAR(50),
    title VARCHAR(255),
    message TEXT,
    type ENUM('ORDER','SYSTEM','PROMOTION') DEFAULT 'SYSTEM',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO accounts(uid,email,password,role) VALUES
('USR-ADM001','admin@ecommerce.vn','admin123','ADMIN'),
('USR-HUY123','nguyenhuy@gmail.com','user123','USER'),
('USR-THAO88','lethao@yahoo.com','user456','USER');

INSERT INTO profiles(account_uid,full_name,phone,city,address_line) VALUES
('USR-ADM001','Admin Store','0909000000','Hà Nội','Tầng 5, VTC Tower'),
('USR-HUY123','Nguyễn Huy','0912345678','TP. HCM','22 Nguyễn Du'),
('USR-THAO88','Lê Thảo','0987654321','Đà Nẵng','45 Lê Lợi');

INSERT INTO categories(name,description) VALUES
('Điện thoại','Smartphone chính hãng');

INSERT INTO brands(name) VALUES
('Apple'),('Samsung'),('Xiaomi'),('Oppo'),('Vivo');

INSERT INTO products(name,brand_id,image_url,price,description,category_id,stock) VALUES
('iPhone 15 Pro Max 256GB',1,'images/iphone15pm.jpg',34990000,'Màu Titan tự nhiên',1,18),
('Samsung Galaxy S24 Ultra 512GB',2,'images/s24ultra.jpg',28990000,'Màu Black',1,14);

INSERT INTO product_images(product_id,image_path,is_primary,sort_order) VALUES
(1,'images/iphone15pm_1.jpg',TRUE,1),
(1,'images/iphone15pm_2.jpg',FALSE,2),
(1,'images/iphone15pm_3.jpg',FALSE,3),
(2,'images/s24ultra_1.jpg',TRUE,1),
(2,'images/s24ultra_2.jpg',FALSE,2);

INSERT INTO attributes(name) VALUES
('Color'),('Storage'),('RAM');

INSERT INTO attribute_values(attribute_id,value) VALUES
(1,'Natural Titanium'),
(1,'Black'),
(2,'256GB'),
(2,'512GB'),
(3,'8GB'),
(3,'12GB');

INSERT INTO product_attributes(product_id,attribute_id,attribute_value_id) VALUES
(1,1,1),(1,2,3),(1,3,5),
(2,1,2),(2,2,4),(2,3,6);

INSERT INTO addresses(account_uid,recipient_name,phone,address_line,city,state,postal_code) VALUES
('USR-HUY123','Nguyễn Huy','0912345678','22 Nguyễn Du','TP. HCM','HCM','700000');

INSERT INTO cart_items(account_uid,product_id,quantity) VALUES
('USR-HUY123',1,1),
('USR-HUY123',2,1);

INSERT INTO guest_cart_items(session_token,product_id,quantity) VALUES
('GUEST-4f7ac09a',2,1);

INSERT INTO payments(method,status,transaction_id,payment_date) VALUES
('MOMO','COMPLETED','MOMO987654321',NOW());

INSERT INTO orders(account_uid,status,total_amount,address_id,payment_id) VALUES
('USR-HUY123','DELIVERED',63980000,1,1);

INSERT INTO order_items(order_id,product_id,quantity,price) VALUES
(1,1,1,34990000),
(1,2,1,28990000);

INSERT INTO order_history(order_id,old_status,new_status,changed_by_uid,note) VALUES
(1,'PROCESSING','SHIPPED','USR-ADM001','Xác nhận chuyển hàng'),
(1,'SHIPPED','DELIVERED','USR-ADM001','Đã giao thành công');

INSERT INTO notifications(account_uid,title,message,type) VALUES
('USR-HUY123','Đơn hàng #1 giao thành công','Cảm ơn bạn đã mua hàng!','ORDER');

