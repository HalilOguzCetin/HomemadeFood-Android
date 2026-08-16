package com.homemadefood.app.data.local

import com.homemadefood.app.data.model.AddressResponse
import kotlinx.coroutines.flow.first

/*
 * C4D
 *
 * Backend'deki Address.IsDefault ile uygulamadaki
 * "şu an aktif teslimat adresi" kavramını birbirinden ayırır.
 *
 * Tüm adres mutation'larından sonra aynı resolution kuralını
 * kullanabilmek için C4B'deki mantık merkezi hale getirildi.
 */
class DeliveryAddressSelectionManager(
    private val sessionManager: SessionManager
) {

    suspend fun resolve(
        addresses: List<AddressResponse>
    ): AddressResponse? {
        if (addresses.isEmpty()) {
            sessionManager
                .clearSelectedDeliveryAddress()

            return null
        }

        val storedAddressId =
            sessionManager
                .selectedDeliveryAddressId
                .first()

        val storedAddress =
            storedAddressId?.let {
                    selectedId ->

                addresses.firstOrNull {
                        address ->
                    address.id == selectedId
                }
            }

        val resolvedAddress =
            storedAddress
                ?: addresses
                    .firstOrNull {
                        it.isDefault
                    }
                ?: addresses.first()

        if (
            storedAddressId !=
            resolvedAddress.id
        ) {
            sessionManager
                .setSelectedDeliveryAddressId(
                    resolvedAddress.id
                )
        }

        return resolvedAddress
    }

    suspend fun select(
        addressId: Int,
        addresses: List<AddressResponse>
    ): AddressResponse? {
        if (addressId <= 0) {
            return null
        }

        val selectedAddress =
            addresses.firstOrNull {
                it.id == addressId
            }
                ?: return null

        sessionManager
            .setSelectedDeliveryAddressId(
                selectedAddress.id
            )

        return selectedAddress
    }

    /*
     * Yeni adres ekleme kuralı:
     *
     * - Kullanıcının zaten aktif bir teslimat adresi varsa
     *   yeni adres onu otomatik değiştirmez.
     *
     * - Aktif seçim hiç yoksa (örn. ilk adres oluşturuldu)
     *   yeni adres aktif teslimat adresi olur.
     *
     * Bu işlem backend IsDefault alanını değiştirmez.
     */
    suspend fun activateIfNone(
        addressId: Int
    ) {
        if (addressId <= 0) {
            return
        }

        val currentAddressId =
            sessionManager
                .selectedDeliveryAddressId
                .first()

        if (currentAddressId == null) {
            sessionManager
                .setSelectedDeliveryAddressId(
                    addressId
                )
        }
    }
}