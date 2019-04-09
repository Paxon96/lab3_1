package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class BookKeeperTest {

    private InvoiceFactory invoiceFactory;
    private BookKeeper bookKeeper;
    private ProductData productData;
    private ClientData clientData;
    private InvoiceRequest invoiceRequest;
    private TaxPolicy taxPolicy;
    private RequestItem requestItem;

    @Before
    public void init() {
        invoiceFactory = mock(InvoiceFactory.class);
        bookKeeper = new BookKeeper(new InvoiceFactory());
        Product product = new Product(Id.generate(), new Money(1), "Temp", ProductType.FOOD);
        productData = product.generateSnapshot();
        clientData = new ClientData(Id.generate(), "Kowalski");
        invoiceRequest = new InvoiceRequest(clientData);
        taxPolicy = mock(TaxPolicy.class);


        when(invoiceFactory.create(any(ClientData.class))).thenReturn(new Invoice(Id.generate(), clientData));
        when(taxPolicy.calculateTax(any(ProductType.class), any(Money.class))).thenReturn(new Tax(new Money(1),"TaxTest"));
    }

    @Test
    public void invoiceRequestWithOneItemReturningInvoiceWithOneItemTest(){
        requestItem = new RequestItem(productData,1,new Money(1));
        invoiceRequest.add(requestItem);

        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        Assert.assertThat(invoice.getItems().size(),is(equalTo(1)));
    }

    @Test
    public void invoiceRequestWithTwoItemInvokingCalculateTaxMethodTwiceTest(){
        invoiceRequest.add(new RequestItem(productData,1,new Money(2)));
        invoiceRequest.add(new RequestItem(productData,2,new Money(3)));

        bookKeeper.issuance(invoiceRequest, taxPolicy);

        verify(taxPolicy, times(2)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test
    public void invoiceRequestWithNoItemReturningInvoiceWithNoItemTest(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest,taxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(equalTo(0)));
    }

    @Test
    public void invoiceRequestWithNoItemNotInvokingCalculateTaxMethodTest(){
        bookKeeper.issuance(invoiceRequest,taxPolicy);

        verify(taxPolicy,times(0)).calculateTax(Mockito.any(), Mockito.any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void invoiceRequestWithTwoItemInvokingCalculateTaxMethodTest(){
        invoiceRequest.add(new RequestItem(productData,1,new Money(2,"PL")));
        invoiceRequest.add(new RequestItem(productData,2,new Money(3)));

        bookKeeper.issuance(invoiceRequest, taxPolicy);
    }

    @Test
    public void invoiceRequestShouldHaveGivenClientDataTest(){
        Invoice invoice = bookKeeper.issuance(invoiceRequest, taxPolicy);
        Assert.assertThat(invoice.getClient(),equalTo(clientData));
    }
}
