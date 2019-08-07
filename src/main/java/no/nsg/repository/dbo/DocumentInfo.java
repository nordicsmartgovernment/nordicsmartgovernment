package no.nsg.repository.dbo;

import no.nsg.repository.TransformationManager;

import java.time.LocalDateTime;


public class DocumentInfo {

    private TransformationManager.Direction direction = TransformationManager.Direction.DOESNT_MATTER;

    private String account;

    private Float amount;

    private Float vat;

    private String currency;

    LocalDateTime transactionTime;


    public void setDirection(final TransformationManager.Direction direction) {
        this.direction = direction;
    }

    public TransformationManager.Direction getDirection() {
        return direction;
    }

    public void setAccount(final String account) {
        this.account = account;
    }

    public String getAccount() {
        return this.account;
    }

    public void setAmount(final Float amount) {
        this.amount = amount;
    }

    public Float getAmount() {
        return this.amount;
    }

    public void setVat(final Float vat) {
        this.vat = vat;
    }

    public Float getVat() {
        return this.vat;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setTransactionTime(final LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public LocalDateTime getTransactionTime() {
        return this.transactionTime;
    }
}
