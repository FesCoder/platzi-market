package com.platzi.market.domain.repository;

import com.platzi.market.domain.Purchase;

import java.util.List;
import java.util.Optional;

public interface PurchaseRepository {

    List<Purchase> getAll();
    // Es un Optional para que podemos controlar si no existe el Cliente.
    Optional<List<Purchase>> getByClient(String clientId);
    Purchase save(Purchase purchase);
}
