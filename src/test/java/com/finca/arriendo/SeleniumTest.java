package com.finca.arriendo;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; 

@ActiveProfiles("test") 
@SpringBootTest(classes = ArriendoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class SeleniumTest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        //Configuración para obtener la versión correcta de ChromeDriver
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Asus\\Documents\\chromedriver-win64\\chromedriver.exe");

        //Configuración del WebDriver para cuestiones de compatbilidad
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        //options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--windows-size=1920,1200");

        //Inicializa el WebDriver mediante Chrome y las opciones previas
        driver = new ChromeDriver(options);
    }
    
    @Test
    public void testLoginPage()throws InterruptedException {
        //Navegar hacia la pagina de inicio
        driver.get("http://localhost:8080/h2-console/");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        //Configurar los campos para el inicio de sesión
        WebElement jdbcUrlField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("url")));
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("user")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("password")));

        //Introducir los valores para los campos URL, usuario y contraseña respectivamente
        jdbcUrlField.clear();
        jdbcUrlField.sendKeys("jdbc:h2:file:./mydatabase");

        // Espera de 2 segundos antes de introducir el nombre de usuario
        Thread.sleep(2000); // Pausa de 2 segundos

        usernameField.clear();
        usernameField.sendKeys("sa");

        // Esperar 2 segundos para ver lo que sucede
        Thread.sleep(2000); // Pausa de 2 segundos

        passwordField.clear();
        passwordField.sendKeys(""); //ContraseÑa vacía

        // Esperar 2 segundos para ver lo que sucede
        Thread.sleep(2000); // Pausa de 2 segundos

        //Hacer clic en el botón de inicio de sesión
        WebElement connectButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//input[@value='Connect']")));
        connectButton.click();

        // Esperar 2 segundos para ver lo que sucede
        Thread.sleep(2000); // Pausa de 2 segundos
    }

    @Test
    public void testFrontendLoginPage() throws InterruptedException {
        // Navegar hacia la página de inicio de sesión del frontend
        driver.get("http://localhost:4200/login");

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Configurar los campos de nombre de usuario y contraseña
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='User name']")));
        WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@placeholder='Password']")));

        // Introducir los valores para los campos
        usernameField.clear();
        usernameField.sendKeys("Carlos");

        // Espera de 2 segundos antes de introducir la contraseña
        Thread.sleep(2000);

        passwordField.clear();
        passwordField.sendKeys("password3");

        // Esperar 2 segundos para observar lo que sucede
        Thread.sleep(2000);

        // Hacer clic en el botón de inicio de sesión
        WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(text(), 'LOG IN NOW')]")));

        loginButton.click();

        // Esperar para observar el resultado después de hacer clic
        Thread.sleep(3000);
    }

    // Cerrar el navegador después de la prueba
    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
