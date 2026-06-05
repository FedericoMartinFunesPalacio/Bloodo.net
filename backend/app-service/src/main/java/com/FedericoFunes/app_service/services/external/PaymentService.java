package com.FedericoFunes.app_service.services.external;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import java.util.List;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    public String createDonationPreference(BigDecimal amount) throws MPException, MPApiException {
        MercadoPagoConfig.setAccessToken(accessToken);

        PreferenceItemRequest item =
                PreferenceItemRequest.builder()
                        .title("Donación Bloodo.net")
                        .quantity(1)
                        .unitPrice(amount)
                        .build();

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(List.of(item))
                        .build();

        PreferenceClient client = new PreferenceClient();
        Preference preference = client.create(preferenceRequest);

        return preference.getInitPoint();
    }
}

