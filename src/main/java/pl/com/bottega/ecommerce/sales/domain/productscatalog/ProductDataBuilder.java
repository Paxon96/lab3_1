package pl.com.bottega.ecommerce.sales.domain.productscatalog;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

public class ProductDataBuilder {
    private Id id = Id.generate();
    private Money price = new Money(10);
    private String name = "default test product data name";
    private Date snapshotDate = new Date();
    private ProductType type = ProductType.STANDARD;

    public ProductDataBuilder withId(Id id) {
        this.id = id;
        return this;
    }

    public ProductDataBuilder withPrice(Money price) {
        this.price = price;
        return this;
    }

    public ProductDataBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ProductDataBuilder withDate(Date date) {
        this.snapshotDate = date;
        return this;
    }

    public ProductDataBuilder withType(ProductType productType) {
        this.type = productType;
        return this;
    }

    public ProductData build() {
        return new ProductData(id, price, name, type, snapshotDate);
    }

}
