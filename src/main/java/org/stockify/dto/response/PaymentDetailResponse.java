package org.stockify.dto.response;

import lombok.Value;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Value
public class PaymentDetailResponse {
    String paymentId;
    String status;
    String statusDetail;
    String paymentMethodId;
    String paymentTypeId;
    String issuerId;
    Integer installments;
    String cardLastFour;
    String cardholderName;
    String statementDescriptor;
    BigDecimal transactionAmount;
    BigDecimal netReceivedAmount;
    String payerEmail;
    String payerId;
    LocalDateTime dateApproved;
    LocalDateTime dateCreated;
}
