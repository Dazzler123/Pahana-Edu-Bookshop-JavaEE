CREATE
DATABASE IF NOT EXISTS pahana_edu_bookshop;
USE
pahana_edu_bookshop;

CREATE TABLE Customer
(
    account_number VARCHAR(50) PRIMARY KEY,
    name           VARCHAR(100) NOT NULL,
    address        VARCHAR(200),
    telephone      VARCHAR(20),
    status         CHAR(1)      NOT NULL
);

CREATE TABLE Item
(
    item_code   VARCHAR(10) PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    unit_price  DECIMAL(10, 2) NOT NULL,
    qty_on_hand INT            NOT NULL,
    status      CHAR(1)        NOT NULL
);

CREATE TABLE Orders
(
    id                     INT AUTO_INCREMENT PRIMARY KEY,
    order_code             VARCHAR(20)    NOT NULL UNIQUE,
    customer_id            VARCHAR(20)    NOT NULL,
    total_amount           DECIMAL(10, 2) NOT NULL,
    total_discount_applied DECIMAL(10, 2) NOT NULL,
    order_date             DATETIME       NOT NULL,
    status                 CHAR(1) DEFAULT 'P',
    payment_status         CHAR(1) DEFAULT 'U',
    payment_method         VARCHAR(50)
);

CREATE TABLE Order_Item
(
    id               INT AUTO_INCREMENT PRIMARY KEY,
    order_id         VARCHAR(20)    NOT NULL,
    item_id          VARCHAR(20)    NOT NULL,
    qty              INT            NOT NULL,
    qty_in_weight DOUBLE DEFAULT 0,
    unit_price       DECIMAL(10, 2) NOT NULL,
    line_total       DECIMAL(10, 2) NOT NULL,
    discount_applied DECIMAL(10, 2) DEFAULT 0,
    FOREIGN KEY (order_id) REFERENCES Orders (order_code)
);

CREATE TABLE IF NOT EXISTS Support_Request
(
    id
    INT
    AUTO_INCREMENT
    PRIMARY
    KEY,
    ticket_id
    VARCHAR
(
    50
) UNIQUE NOT NULL, issue_type VARCHAR
(
    50
) NOT NULL, priority VARCHAR
(
    20
) NOT NULL, subject VARCHAR
(
    200
) NOT NULL, description TEXT NOT NULL, user_email VARCHAR
(
    100
), user_agent TEXT, timestamp DATETIME NOT NULL, status VARCHAR
(
    20
) DEFAULT 'OPEN', created_at DATETIME DEFAULT CURRENT_TIMESTAMP, updated_at DATETIME DEFAULT CURRENT_TIMESTAMP
    ON UPDATE CURRENT_TIMESTAMP, INDEX idx_ticket_id
(
    ticket_id
), INDEX idx_status
(
    status
), INDEX idx_priority
(
    priority
), INDEX idx_timestamp
(
    timestamp
) );

