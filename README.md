# Pahana Edu Bookshop - JavaEE Web Application

A comprehensive web-based bookshop management system built with Java EE, designed for "Pahana Edu" bookshop to manage customers, inventory, orders, and generate detailed reports.

## 🚀 Features

### Core Functionality
- **Customer Management**: Add, update, and manage customer information with unique account numbers
- **Inventory Management**: Track books/items with stock levels, pricing, and status management
- **Order Processing**: Complete order placement system with item selection and payment tracking
- **Order Management**: View, update, and manage existing orders with status tracking
- **Bill Generation**: Automated PDF bill generation for completed orders
- **Reporting System**: Comprehensive reports with filtering options (date range, customer, items)
- **Dashboard Analytics**: Real-time statistics, top-selling items, and customer analytics
- **Support System**: Built-in help system with FAQ and user guides

### Technical Features
- **RESTful API**: JSON-based API endpoints for all operations
- **Responsive Design**: Bootstrap 5-based UI that works on all devices
- **Real-time Updates**: Dynamic content loading without page refreshes
- **PDF Generation**: Apache PDFBox integration for bill generation
- **Email Integration**: Support request notifications via email
- **Database Connection Pooling**: Apache Commons DBCP2 for efficient database connections

## 🛠️ Technology Stack

- **Backend**: Java EE (Servlets, JSP)
- **Frontend**: HTML5, CSS3, JavaScript, jQuery, Bootstrap 5
- **Database**: MySQL 8.0
- **Build Tool**: Maven 3.8.5
- **PDF Generation**: Apache PDFBox 2.0.29
- **Email**: JavaMail API 1.6.2
- **Connection Pooling**: Apache Commons DBCP2 2.9.0
- **JSON Processing**: javax.json 1.1.4
- **Testing**: JUnit 5, Mockito

## 📋 Prerequisites

- Java 8 or higher
- Apache Tomcat 8.5 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/Pahana-Edu-Bookshop-JavaEE.git
cd Pahana-Edu-Bookshop-JavaEE
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE pahana_edu_bookshop;

-- Create tables (run the SQL default script in src/main/resources/db/)
-- Tables: customers, items, orders, order_items, support_requests
```

### 3. Configure Database Connection
Update database connection settings in your application server or modify the `DBUtil` class:
```java
// Update these values in DBUtil.java
private static final String URL = "jdbc:mysql://localhost:3306/pahana_edu_bookshop";
private static final String USERNAME = "your_username";
private static final String PASSWORD = "your_password";
```

### 4. Build the Project
```bash
# Using Maven wrapper (recommended)
./mvnw clean package

# Or using system Maven
mvn clean package
```

### 5. Deploy to Tomcat
1. Copy the generated `target/Pahana-Edu-Bookshop-JavaEE-1.0-SNAPSHOT.war` to Tomcat's `webapps` directory
2. Start Tomcat server
3. Access the application at `http://localhost:8080/Pahana-Edu-Bookshop-JavaEE-1.0-SNAPSHOT/`

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/icbt/pahanaedubookshopjavaee/
│   │   ├── controller/          # Servlet controllers
│   │   ├── service/             # Business logic services
│   │   ├── dao/                 # Data access objects
│   │   ├── dto/                 # Data transfer objects
│   │   ├── model/               # Entity classes
│   │   └── util/                # Utility classes
│   ├── webapp/
│   │   ├── controller/          # JavaScript controllers
│   │   ├── css/                 # Stylesheets
│   │   ├── js/                  # JavaScript files
│   │   └── index.html           # Main application page
│   └── resources/
│       └── sql/                 # Database scripts
└── test/
    └── java/                    # Unit tests
```

## 🎯 API Endpoints

### Customer Management
- `GET /customer` - Get all customers
- `GET /customer?action=ids` - Get customer IDs
- `GET /customer?action=generateAccountNumber` - Generate new account number
- `GET /customer?accountNumber={id}` - Get specific customer
- `POST /customer` - Create/update customer
- `PUT /customer` - Update customer status

### Item Management
- `GET /item` - Get all items
- `GET /item?action=generateItemCode` - Generate new item code
- `POST /item` - Create/update item
- `PUT /item` - Update item status

### Order Management
- `GET /orders` - Get all orders
- `POST /orders` - Create new order
- `PUT /orders` - Update order status

### Reports
- `GET /reports` - Generate reports with filters
- `GET /generateBill?orderCode={code}` - Generate PDF bill

### Dashboard
- `GET /dashboardStats` - Get dashboard statistics
- `GET /dashboardAnalytics` - Get analytics data

## 🧪 Testing

Run the test suite:
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=CustomerServletTest
```

## 📊 Features Overview

### Dashboard
- Total orders, pending orders, and revenue statistics
- Top-selling items with sales data
- Most active customers analytics
- Real-time data updates

### Customer Management
- Auto-generated unique account numbers (CUS-0001 format)
- Complete customer information management
- Status tracking (Active/Inactive)
- Search and filter capabilities

### Inventory Management
- Auto-generated item codes (ITM-0001 format)
- Stock level tracking
- Price management
- Item status control

### Order Processing
- Multi-item order placement
- Real-time price calculations
- Payment method tracking
- Order status management

### Reporting System
- Detailed order reports
- Date range filtering
- Customer-specific reports
- Item performance reports
- PDF export functionality

## 🔒 Security Features

- Input validation on all forms
- SQL injection prevention
- XSS protection
- Session management
- Error handling and logging

## 🎨 UI/UX Features

- Modern Bootstrap 5 design
- Responsive layout for all devices
- Interactive dashboard with charts
- Modal dialogs for forms
- Toast notifications
- Loading indicators
- Search and filter functionality

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Dasindu Hewagamage**
- Email: dasinduhewagamage@gmail.com
- LinkedIn: https://www.linkedin.com/in/dasindu-hewagamage-a92961205/
- GitHub: https://github.com/Dazzler123

## 🙏 Acknowledgments

- Bootstrap team for the excellent CSS framework
- Apache Foundation for PDFBox and Commons libraries
- MySQL team for the robust database system
- All contributors and testers

## 📞 Support

For support and questions:
1. Check the built-in Help section in the application
2. Review the FAQ section
3. Create an issue on GitHub
4. Contact the development team

---

© 2025 Designed and Developed by Dasindu Hewagamage. All rights reserved.
