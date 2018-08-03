package data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class Product {
    private String name;
    private double weight;
    private String brand;
    private double price;
    private String expiryDate;

    public Product(String name, double weight, String brand, double price, String expiryDate){
        this.name = name;
        this.weight = weight;
        this.brand = brand;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    /*public data.Product createProduct(String name, double weight, String brand, double price, String expiryDate) throws InvalidPropertiesFormatException {
        if(weight < 0 || price < 0 || !isValidDateFormat(expiryDate)){
            throw new InvalidPropertiesFormatException("Incorrect arguments");
        }
        return new data.Product(name,weight,brand,price,expiryDate);
    }*/

    public boolean isValidObject() {
        if(weight < 0 || price < 0 || !isValidDateFormat(expiryDate)){
            return false;
        }
        return true;
    }

    private boolean isValidDateFormat(String date){
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("d/MM/yyyy"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Double.compare(product.weight, weight) == 0 &&
                Double.compare(product.price, price) == 0 &&
                Objects.equals(name, product.name) &&
                Objects.equals(brand, product.brand) &&
                Objects.equals(expiryDate, product.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, weight, brand, price, expiryDate);
    }
}