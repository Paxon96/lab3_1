package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import java.util.Date;

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
        bookKeeper = new BookKeeper(invoiceFactory);
        productData = new ProductData(Id.generate(), new Money(1), "Temp", ProductType.FOOD, new Date());
        invoiceRequest = new InvoiceRequest(clientData);
        taxPolicy = mock(TaxPolicy.class);


        when(invoiceFactory.create(any(ClientData.class))).thenReturn(new Invoice(Id.generate(), new ClientData(Id.generate(), "Temp")));
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
}
