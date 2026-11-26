<div align="center">
  <img src="src/main/webapp/assets/images/logo.png" alt="logo_foundly" width="200" height="200" />
  <h1>Foundly</h1>
  <p>
    <b>The go-to platform for reporting, searching, and returning lost items.</b>
  </p>

  <p>
    <img src="https://img.shields.io/badge/Java-21-orange" alt="Java 21" />
    <img src="https://img.shields.io/badge/Jakarta%20Servlet-6.1-blue" alt="Jakarta Servlet 6.1" />
    <img src="https://img.shields.io/badge/Tomcat-11-yellow" alt="Apache Tomcat" />
    <img src="https://img.shields.io/badge/Database-MySQL-blue" alt="MySQL" />
    <img src="https://img.shields.io/badge/Build-Maven-C71A36" alt="Maven" />
  </p>
</div>

---

## 📖 About The Project

**Foundly** is a web application designed to digitize and simplify the process of returning lost items.
We aim to build a network of honest citizens and local businesses (**Drop-Points**) collaborating to return what was lost in a **secure, traceable, and rewarding** way.

The system solves the issue of mistrust between strangers by introducing safe exchange points and a verification system (Secure Claim) based on specific questions.

---

## ✨ Key Features

### 👤 For Users (Finder & Owner)
- **Dual Role**: A single account to report found items or claim lost ones.
- **Secure Claim 🛡**: Anti-fraud system. The claimant must correctly answer verification questions set by the finder.
- **Lost Pets 🐾**: A dedicated section with specific fields for reporting lost pets.
- **Gamification 🏆**: Earn points and badges (e.g., *Sherlock Holmes*) for every successful return.

### 🏪 For Drop-Points (Businesses)
- **Business Registration**: Dedicated registration flow with business verification.
- **Custody Point**: Shops can offer themselves as safe locations for exchanges, increasing their local visibility.
- **Inventory Management**: Dashboard to track items in custody and manage check-in/check-out operations.

---

## 🏗 Architecture & Tech Stack

The project strictly follows the **MVC (Model-View-Controller)** architectural pattern to ensure maintainability and scalability.

### Backend
- **Language**: Java 21
- **Web API**: Jakarta Servlet 6.1
- **Server**: Apache Tomcat 11
- **Security**: Password hashing via **BCrypt** (jBCrypt).
- **Data Access**: DAO (Data Access Object) pattern with custom Connection Pooling.
- **Service Layer**: Business logic completely separated from controllers.

### Database
- **DBMS**: MySQL (Hosted on Aiven Cloud)
- **Schema**: *Joined Inheritance* strategy for polymorphic report management (Objects vs. Animals).

### Frontend
- **Technology**: JSP (JavaServer Pages)
- **Styling**: Custom CSS3
- **Scripting**: JavaScript

---

## 🚀 Getting Started

Run the following commands from the project folder, for example in the VS Code terminal. Use JDK 21, Maven, Tomcat 11, and the MySQL command-line client. Set `JAVA_HOME` to your JDK directory and `CATALINA_HOME` to your Tomcat installation.

1. Initialize the database. The command prompts for the Aiven `avnadmin` password, creates the `foundly` database if needed, and imports `db/schemaDB.sql`. It preserves existing rows.

   ```bash
   bash scripts/avvia-aiven.sh --init-db
   ```

2. In Aiven Console, ensure `foundlyapp` has the privileges `SELECT`, `INSERT`, `UPDATE`, and `DELETE`.

3. Build the WAR with `mvn package -DskipTests`, then run the launcher. It prompts for the `foundlyapp` password and verifies the Aiven server certificate using `db/aiven-ca.pem`.

   ```bash
   bash scripts/avvia-aiven.sh
   ```

4. Open [Foundly](http://127.0.0.1:18081/Foundly/index). Press Ctrl+C in the terminal to stop Tomcat.

The launcher uses the configured Aiven service by default. To use another service, pass its host, port, and CA file after the script name or after `--init-db`. Run `mvn clean verify` to execute the complete test suite separately.

## Software Dependability

- [Lab 1 CI/CD](CICD.md)
- [Lab 2a · Docker](Docker.md)

## 👥 Authors

This project was developed by the **NC05 Team** for the Bachelor's Degree in Computer Science at the **University of Salerno**.

| Name | Contact |
| :--- | :--- |
| **Salvador Davide Passarelli** | [Email](mailto:s.passarelli2@studenti.unisa.it) |
| **Natale Nappi** | [Email](mailto:n.nappi8@studenti.unisa.it) |
| **Salvatore Lepore** | [Email](mailto:s.lepore11@studenti.unisa.it) |

<p align="right">
  <i>Supervisor: Prof. Carmine Gravino</i>
</p>
