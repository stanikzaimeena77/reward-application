package com.charter.reward.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.charter.reward.model.Customer;
import com.charter.reward.model.Purchase;
import com.charter.reward.model.Reward;
import com.charter.reward.repository.CustomerRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder.In;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RewardServiceTest {

    private static final BigDecimal[] AMOUNTS =
        new BigDecimal[]{
            new BigDecimal(250.00),
            new BigDecimal(120.00),
            new BigDecimal(30),
            new BigDecimal(75.12)
    };

    private static final Instant[] INSTANTS =
        new Instant[]{
            Instant.parse("2022-05-22T02:41:37.909240026Z"),
            Instant.parse("2022-06-22T02:41:37.909240026Z"),
            Instant.parse("2022-05-02T02:41:37.909240026Z"),
            Instant.parse("2022-07-22T02:41:37.909240026Z")
        };

    @Mock
    private CustomerRepository customerRepositoryMock;

    @InjectMocks
    private RewardService serviceUnderTest;

    @Test
    void rewardByCustomerId() {
        String customerName = UUID.randomUUID().toString();
        List<Purchase> purchases = purchases();
        Customer customer = purchases.get(0).getCustomer();
        customer.setPurchases(purchases);
        customer.setName(customerName);
        when(customerRepositoryMock.findById(anyLong()))
            .thenReturn(Optional.of(customer));

        Reward reward = serviceUnderTest.rewardByCustomerId(123l);

        assertEquals(customerName, reward.getCustomerName());
    }

    @Test
    void rewardByCustomerId_exceptionTest() {
        assertThrows(ResponseStatusException.class,
            () -> serviceUnderTest.rewardByCustomerId(123l));
    }

    @Test
    void from() {
        Reward reward = serviceUnderTest.from(purchases());
        Map<Month, Long> pointsByMonth = reward.getPointsByMonth();

        assertEquals(25l, pointsByMonth.get(Month.JULY));
        assertEquals(90l, pointsByMonth.get(Month.JUNE));
        assertEquals(350l, pointsByMonth.get(Month.MAY));
    }

    private List<Purchase> purchases() {
        int index = 0;
        return List.of(
            instanceOf(index++),
            instanceOf(index++),
            instanceOf(index++),
            instanceOf(index));
    }

    private Purchase instanceOf(int index) {
        Purchase p = new Purchase();
        p.setCustomer(customer("test-user"));
        p.setAmount(AMOUNTS[index]);
        p.setTimestamp(INSTANTS[index]);

        return p;
    }

    private Customer customer(String name) {
        Customer c = new Customer();
        c.setName(name);

        return c;
    }

    @ParameterizedTest
    @MethodSource("amountProvider")
    void calculate(BigDecimal amount, long expectedPoints) {
        assertEquals(expectedPoints, serviceUnderTest.calculate(amount));
    }

    private static Stream<Arguments> amountProvider() {
        return Stream.of(
            Arguments.of(AMOUNTS[0], 350l),
            Arguments.of(AMOUNTS[1], 90l),
            Arguments.of(AMOUNTS[2], 0l),
            Arguments.of(AMOUNTS[3], 25l)
        );
    }
}