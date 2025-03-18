# Kindergarten Suggestion

This project provides a solution for kindergarten suggestions, allowing users to search and evaluate schools.

## Pre-requisites

Before building the project, make sure you have the following installed:

1. **JDK 17**: You can download it from the official [Oracle website](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

2. **Git**: Make sure you have Git installed. You can download it from the official [Git website](https://git-scm.com/downloads).

3. **Database**: MySQL (make sure to install and configure database).

## Git Local Setup

1. **Install Git**: Download Git from the [Git website](https://git-scm.com/downloads) and install it.

2. **Clone the repository:**

   ```bash
   git clone https://gitlab.com/anhnnhe176642/kindergarten-suggestion.git
   cd Kindergarten-Suggestion
   ```

## Building the Project

To build the project, follow these steps:

1. **Create `application-dev.yml` from `application-dev-example.yml`:**

   Copy the `src/main/resources/application-dev-example.yml` file to `src/main/resources/application-dev.yml`:

   ```bash
   cp src/main/resources/application-dev-example.yml src/main/resources/application-dev.yml
   ```

2. **Edit the new `application-dev.yml`:**

   Open the newly created `src/main/resources/application-dev.yml` file and update the following properties:

   ```yaml
   spring:
     datasource:
       url: jdbc:<your-database-driver>://<your-database-host>:<your-database-port>/<your-database-name>
       username: <your-database-username>
       password: <your-database-password>

     security:
       user:
         name: <your-admin-username>
         email: <your-admin-email>
         password: <your-admin-password>
   ```

   Replace `<your-database-driver>`, `<your-database-host>`, `<your-database-port>`, `<your-database-name>`, `<your-database-username>`, and `<your-database-password>` with your actual database details.

3. **Run the project:**

   After configuring the database, you can run the project using the following command:

   ```bash
   ./mvnw spring-boot:run
   ```

4. **Access the application:**

   Open your browser and go to: [http://localhost:8080](http://localhost:8080)