package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest {

    @InjectMocks
    private AddProductCommandHandler addProductCommandHandler;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SuggestionService suggestionService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SystemContext systemContext;

    private Reservation reservation;
    private Product product;
    private Client client;

    @Before
    public void init() {
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED, new ClientData(Id.generate(), "Test"),
                new Date());
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);

        product = new Product(Id.generate(), new Money(1), "Test Product", ProductType.STANDARD);
        when(productRepository.load(any(Id.class))).thenReturn(product);

        when(clientRepository.load(any(Id.class))).thenReturn(new Client());
        when(systemContext.getSystemUser()).thenReturn(new SystemUser(Id.generate()));
        when(suggestionService.suggestEquivalent(any(Product.class), any(Client.class))).thenReturn(
                new Product(Id.generate(), new Money(1), "Test Product", ProductType.STANDARD));
    }

    @Test
    public void addProductShouldReturnReservationFromRepoTest() {
        addProductCommandHandler.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));

        verify(reservationRepository, times(1)).load(any(Id.class));
    }

    @Test
    public void addProductShouldNotReturnProductFromSuggestionServiceTest() {
        addProductCommandHandler.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));

        verify(suggestionService, never()).suggestEquivalent(any(Product.class), any(Client.class));
    }

    @Test
    public void addingANotAvailableProductShouldReturnFromSuggestionServiceTest() {
        product.markAsRemoved();
        when(productRepository.load(any(Id.class))).thenReturn(product);

        addProductCommandHandler.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));
        verify(suggestionService, times(1)).suggestEquivalent(any(Product.class), any(Client.class));
    }

    @Test
    public void addProductShouldLoadProductFromRepoTest(){
        addProductCommandHandler.handle(new AddProductCommand(Id.generate(), Id.generate(), 1));

        verify(productRepository, times(1)).load(any(Id.class));
    }
}
