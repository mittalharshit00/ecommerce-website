CREATE TABLE tenant
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    domain VARCHAR(100) NOT NULL,

    enabled BOOLEAN NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_tenant
        PRIMARY KEY (id),

    CONSTRAINT uk_tenant_domain
        UNIQUE (domain)

);

CREATE TABLE role
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(50) NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_role
        PRIMARY KEY (id),

    CONSTRAINT uk_role_name
        UNIQUE (name)

);

CREATE TABLE users
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    username VARCHAR(100) NOT NULL,

    email VARCHAR(255) NOT NULL,

    keycloak_user_id VARCHAR(255) NOT NULL,

    enabled BOOLEAN NOT NULL,

    tenant_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_users
        PRIMARY KEY (id),

    CONSTRAINT uk_user_username
        UNIQUE (username),

    CONSTRAINT uk_user_email
        UNIQUE (email),

    CONSTRAINT uk_user_keycloak_id
        UNIQUE (keycloak_user_id),

    CONSTRAINT fk_user_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)

);

CREATE INDEX idx_user_tenant
    ON users(tenant_id);

CREATE TABLE user_role
(
    user_id BIGINT NOT NULL,

    role_id BIGINT NOT NULL,

    CONSTRAINT pk_user_role
        PRIMARY KEY (user_id, role_id),

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
        REFERENCES role(id)
);

CREATE INDEX idx_user_role_role
    ON user_role(role_id);

CREATE TABLE category
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(100) NOT NULL,

    tenant_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_category
        PRIMARY KEY (id),

    CONSTRAINT uk_category_tenant_name
        UNIQUE (tenant_id, name),

    CONSTRAINT fk_category_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id)

);

CREATE INDEX idx_category_tenant
    ON category(tenant_id);

CREATE TABLE product
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    name VARCHAR(200) NOT NULL,

    description TEXT,

    image_url VARCHAR(255),

    price DECIMAL(10,2) NOT NULL,

    quantity INT NOT NULL,

    tenant_id BIGINT NOT NULL,

    category_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_product
        PRIMARY KEY (id),

    CONSTRAINT uk_product_tenant_name
        UNIQUE (tenant_id, name),

    CONSTRAINT fk_product_tenant
        FOREIGN KEY (tenant_id)
        REFERENCES tenant(id),

    CONSTRAINT fk_product_category
        FOREIGN KEY (category_id)
        REFERENCES category(id)

);

CREATE INDEX idx_product_tenant
    ON product(tenant_id);

CREATE INDEX idx_product_category
    ON product(category_id);

CREATE TABLE orders
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    total_quantity INT NOT NULL,

    total_amount DECIMAL(10,2) NOT NULL,

    status VARCHAR(30) NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_orders
        PRIMARY KEY (id),

    CONSTRAINT fk_order_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)

);

CREATE INDEX idx_order_user
    ON orders(user_id);

CREATE TABLE order_item
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    order_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    quantity INT NOT NULL,

    price DECIMAL(10,2) NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_order_item
        PRIMARY KEY (id),

    CONSTRAINT fk_order_item_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_order_item_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)

);

CREATE INDEX idx_order_item_order
    ON order_item(order_id);

CREATE INDEX idx_order_item_product
    ON order_item(product_id);

CREATE TABLE favourite
(
    id BIGINT NOT NULL AUTO_INCREMENT,

    user_id BIGINT NOT NULL,

    product_id BIGINT NOT NULL,

    created_at DATETIME(6) NOT NULL,

    updated_at DATETIME(6),

    CONSTRAINT pk_favourite
        PRIMARY KEY (id),

    CONSTRAINT uk_favourite_user_product
        UNIQUE (user_id, product_id),

    CONSTRAINT fk_favourite_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favourite_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
        ON DELETE CASCADE

);

CREATE INDEX idx_favourite_user
    ON favourite(user_id);

CREATE INDEX idx_favourite_product
    ON favourite(product_id);