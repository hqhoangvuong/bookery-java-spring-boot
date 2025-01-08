# Bookery Java Spring Boot Project

## Project Setup

### Prerequisites

- Java Development Kit (JDK) 11 or higher
- Apache Maven 3.6.0 or higher
- An IDE such as IntelliJ IDEA or Eclipse

### Getting Started

1. **Clone the repository:**
    ```sh
    git clone https://github.com/your-username/bookery-java-spring-boot.git
    cd bookery-java-spring-boot
    ```

2. **Edit PostgreSQL credentials in two files**
   - src\main\resources\application.properties
   - pom.xml

3. **Build the project:**
    ```sh
    mvn clean install
    ```

4. **Run the application:**
    ```sh
    mvn liquibase:update
    mvn jooq-codegen:generate
    mvn spring-boot:run
    ```

### Configuration

- **Application Properties:**
  Configure your application settings in `src/main/resources/application.properties`.

### Testing

- **Run tests:**
  ```sh
  mvn test
  ```

### Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Maven Documentation](https://maven.apache.org/guides/index.html)

### Contributing

1. Fork the repository
2. Create a new branch (`git checkout -b feature-branch`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature-branch`)
5. Create a new Pull Request

### License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.