package br.com.mateus.payflow.domain.charge.repository;

public interface PaymentChargeRepository {

    void processPayment(String chargeId);
}