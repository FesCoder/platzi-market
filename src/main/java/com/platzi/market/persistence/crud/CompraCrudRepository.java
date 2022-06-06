package com.platzi.market.persistence.crud;

import com.platzi.market.persistence.entity.Compra;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

// Recibe el Entity y la PK.
public interface CompraCrudRepository extends CrudRepository<Compra, Integer> {
    // Query Method.
    Optional<List<Compra>> findByIdCliente(String idCliente);
}
