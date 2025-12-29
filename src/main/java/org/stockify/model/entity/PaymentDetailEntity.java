package org.stockify.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_details")
@Audited
public class PaymentDetailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_id")
    private String paymentId;

    @Column(name = "status")
    private String status;

    @Column(name = "status_detail")
    private String statusDetail;

    @Column(name = "payment_method_id")
    private String paymentMethodId;

    @Column(name = "payment_type_id")
    private String paymentTypeId;

    @Column(name = "issuer_id")
    private String issuerId;

    @Column(name = "installments")
    private Integer installments;

    @Column(name = "card_last_four")
    private String cardLastFour;

    @Column(name = "cardholder_name")
    private String cardholderName;

    @Column(name = "statement_descriptor")
    private String statementDescriptor;

    @Column(name = "transaction_amount", precision = 20, scale = 2)
    private BigDecimal transactionAmount;

    @Column(name = "net_received_amount", precision = 20, scale = 2)
    private BigDecimal netReceivedAmount;

    @Column(name = "payer_email")
    private String payerEmail;

    @Column(name = "payer_id")
    private String payerId;

    @Column(name = "date_approved")
    private LocalDateTime dateApproved;

    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false, unique = true)
    private TransactionEntity transaction;
}
