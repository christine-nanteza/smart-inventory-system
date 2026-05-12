# 🏪 Joan & Vicky Inventory Management System

A desktop application built in **Java** using **Object-Oriented Programming** principles, developed to solve real inventory challenges faced by **Joan & Vicky Wholesale Shop** in Kyengera, Uganda.

---

## 📌 Problem Statement

Joan & Vicky Wholesale Shop was manually tracking stock, which caused:
- Losses from **expired products** going unnoticed
- **Stock running out** unexpectedly
- No clear picture of **daily sales and revenue**

This system automates all of that with real-time alerts, sales tracking, and detailed reports.

---

## 🎯 Features

### 🔐 Login & Security
- Role-based login — **Admin** and **Cashier**
- Admin gets full system access
- Cashier gets limited access (sales & inventory only)

### 📦 Product Management
- Add **perishable** products (food, medicine) with expiry tracking
- Add **non-perishable** products (electronics)
- Auto-generated product IDs (PRD001, PRD002...)
- Update, delete, and search products by name

### 📊 Stock Management
- Real-time stock level tracking
- **Low stock alerts**
- **Expiry date tracking**
- **Expiring soon warnings** (within 7 days)
- Restock products with before/after display

### 🛒 Sales Management
- Process sales with **automatic receipt generation**
- Refund processing
- Prevents selling **expired products**
- Prevents selling **more than available stock**

### 📈 Reports (5 Types)
- Full inventory report
- Low stock report
- Expired products report
- Sales report
- Daily summary

### 📉 Analytics Dashboard
- Total revenue
- Today's revenue
- Net revenue after refunds
- Best selling product
- Dead stock (never sold products)

### 👥 User Management *(Admin only)*
- Add new users
- Deactivate users
- Role assignment

---

## 🖥️ Interface

JavaFX Desktop GUI featuring:
- Login screen
- Dashboard with side navigation
- Product table with add form
- Sales table with receipt display
- Inventory with **colour-coded rows:**
  - 🟢 Green = OK stock
  - 🟡 Yellow = Low stock
  - 🔴 Red = Expired
- Reports viewer
- Analytics dashboard
- User management panel

---

## ⚙️ Technical Details

| Detail | Value |
|--------|-------|
| Language | Java 21 |
| GUI | JavaFX 21.0.2 |
| IDE | IntelliJ IDEA |
| Build Tool | Maven |
| Storage | File-based (.txt files) |
| Platform | Windows Desktop |

---

## 💾 Storage

File-based storage — no internet or database required:
- Products saved to `products.txt`
- Sales saved to `sales.txt`
- Data loads automatically on startup
- Nothing is lost when the app closes

---

## 🎓 OOP Concepts Demonstrated

| Concept | Implementation |
|---------|---------------|
| **Abstraction** | `Product` is an abstract class |
| **Inheritance** | `Perishable` and `NonPerishable` both extend `Product` |
| **Polymorphism** | `isExpired()` behaves differently per product type |
| **Encapsulation** | All fields private with getters and setters |
| **Exception Handling** | 3 custom exceptions for specific error scenarios |
| **Collections** | `ArrayList` stores products, sales and users |
| **File Handling** | `FileHandler` saves and loads data between sessions |
| **Interfaces** | `Reportable` and `Manageable` interfaces |

---

## 👥 System Users

| Role | Access |
|------|--------|
| **Admin** | Full access — manage products, sales, users, reports, analytics |
| **Cashier** | Limited access — process sales and view inventory only |

---

## 🏠 Real-Life Context

| | |
|-|-|
| **Based on** | Joan & Vicky Wholesale Shop |
| **Location** | Kyengera, Uganda |
| **Problem** | Manual stock tracking causing losses from expired products and unexpected stockouts |
| **Solution** | Automated system with alerts, sales tracking, receipt generation, and reports |

---

## 🚀 How to Run

1. Clone the repository
```bash
git clone https://github.com/christine-nanteza/smart-inventory-system.git
```
2. Open in **IntelliJ IDEA**
3. Make sure **Java 21** and **JavaFX 21.0.2** are installed
4. Run `Main.java` to start the application

---

## 👩‍💻 Developer

**Nanteza Christine**
- 🌐 Portfolio: [nanteza-christine.kesug.com](https://nanteza-christine.kesug.com)
- 💼 LinkedIn: [linkedin.com/in/nanteza-christine-ab95b6400](https://linkedin.com/in/nanteza-christine-ab95b6400)
- 🐙 GitHub: [github.com/christine-nanteza](https://github.com/christine-nanteza)
