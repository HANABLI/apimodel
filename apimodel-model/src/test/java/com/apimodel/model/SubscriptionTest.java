package com.apimodel.model;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;


import java.util.Optional;

import static com.apimodel.model.Subscription.*;

public class SubscriptionTest {

    @Test
    public void testFrom() {
        Assertions.assertEquals(Optional.of(BASIC), Subscription.from("BASIC"));
        Assertions.assertEquals(Optional.of(PRO), Subscription.from("PRO"));
        Assertions.assertEquals(Optional.of(ULTRA), Subscription.from("ULTRA"));
        Assertions.assertEquals(Optional.of(MEGA), Subscription.from("MEGA"));
        Assertions.assertEquals(Optional.of(CUSTOM), Subscription.from("CUSTOM"));
        Assertions.assertEquals(Optional.empty(), Subscription.from("blabla"));
    }
}
