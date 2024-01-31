package com.nikonenko.paymentservice.services;

import com.nikonenko.paymentservice.dto.BalanceResponse;
import com.nikonenko.paymentservice.dto.StripeCardRequest;
import com.nikonenko.paymentservice.dto.StripeChargeRequest;
import com.nikonenko.paymentservice.dto.StripeChargeResponse;
import com.nikonenko.paymentservice.dto.StripeTokenResponse;
import com.nikonenko.paymentservice.exceptions.CreateChargeFailedException;
import com.nikonenko.paymentservice.exceptions.GenerateTokenFailedException;
import com.nikonenko.paymentservice.exceptions.RetrieveBalanceFailedException;
import com.stripe.exception.StripeException;
import com.stripe.model.Balance;
import com.stripe.model.Charge;
import com.stripe.model.Token;
import com.stripe.net.RequestOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class StripeServiceImpl implements StripeService{
    @Value("${api.stripe.secret-key}")
    private String secretKey;
    @Value("${api.stripe.public-key}")
    private String publicKey;
    private final ModelMapper modelMapper;

    @Override
    public StripeTokenResponse generateTokenByCard(StripeCardRequest stripeCardRequest) {
        try {
            Map<String, Object> cardParams = createCardParams(stripeCardRequest);
            Token token = Token.create(Map.of("card", cardParams), getRequestOptions(publicKey));
            return StripeTokenResponse.builder()
                    .token(token.getId())
                    .cardNumber(stripeCardRequest.getCardNumber())
                    .build();
        } catch (StripeException ex) {
            throw new GenerateTokenFailedException(ex.getMessage());
        }
    }

    private Map<String, Object> createCardParams(StripeCardRequest stripeCardRequest) {
        return  Map.of(
                "number", stripeCardRequest.getCardNumber(),
                "exp_month", stripeCardRequest.getExpirationMonth(),
                "exp_year" , stripeCardRequest.getExpirationYear(),
                "cvc", stripeCardRequest.getCvc()
        );
    }

    @Override
    public StripeChargeResponse charge(StripeChargeRequest chargeRequest) {
        StripeChargeResponse chargeResponse = modelMapper.map(chargeRequest, StripeChargeResponse.class);
        chargeResponse.setSuccess(false);
        Map<String, Object> chargeParams = createChargeParams(chargeRequest);
        Charge charge = createCharge(chargeParams);
        setChargeResponse(charge, chargeResponse);
        return chargeResponse;
    }

    private Map<String, Object> createChargeParams(StripeChargeRequest chargeRequest) {
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
        chargeParams.put("currency", chargeRequest.getCurrency());
        chargeParams.put("source", chargeRequest.getStripeToken());
        return chargeParams;
    }

    private Charge createCharge(Map<String, Object> chargeParams) {
        try {
            return Charge.create(chargeParams, getRequestOptions(secretKey));
        } catch (StripeException e) {
            log.error("StripeService charge exception: {}", e.getMessage());
            throw new CreateChargeFailedException(e.getMessage());
        }
    }

    private void setChargeResponse(Charge charge, StripeChargeResponse chargeResponse) {
        chargeResponse.setMessage(charge.getOutcome().getSellerMessage());
        if (charge.getPaid()) {
            chargeResponse.setChargeId(charge.getId());
            chargeResponse.setSuccess(true);
        }
    }

    @Override
    public BalanceResponse getBalance() {
        Balance balance = retrieveBalance();
        return BalanceResponse
                .builder()
                .amount((double)balance.getPending().get(0).getAmount() / 100)
                .currency(balance.getPending().get(0).getCurrency())
                .build();
    }

    private Balance retrieveBalance() {
        try {
            return Balance.retrieve(getRequestOptions(secretKey));
        } catch (StripeException e) {
            throw new RetrieveBalanceFailedException(e.getMessage());
        }
    }

    private RequestOptions getRequestOptions(String key){
        return RequestOptions.builder()
                .setApiKey(key)
                .build();
    }
}
