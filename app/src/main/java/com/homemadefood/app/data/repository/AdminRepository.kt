package com.homemadefood.app.data.repository

import com.homemadefood.app.data.model.AdminOrderDetailResponse
import com.homemadefood.app.data.model.AdminOrderListItemResponse
import com.homemadefood.app.data.model.AdminProducerApplicationResponse
import com.homemadefood.app.data.model.AdminUserDetailResponse
import com.homemadefood.app.data.model.AdminUserListItemResponse
import com.homemadefood.app.data.model.ApiResponse
import com.homemadefood.app.data.model.RecommendationPerformanceResponse
import com.homemadefood.app.data.model.RejectProducerApplicationRequest
import com.homemadefood.app.data.model.UpdateUserStatusRequest
import com.homemadefood.app.data.remote.AdminApiService
import com.homemadefood.app.data.remote.RetrofitClient
import retrofit2.Response

@Suppress("UNUSED_PARAMETER")
class AdminRepository(
    private val adminApiService:
    AdminApiService =
        RetrofitClient.adminApiService
) {

    /*
     * Üretici başvurularını getirir.
     *
     * JWT artık burada taşınmaz.
     * AuthorizationInterceptor otomatik ekler.
     */
    suspend fun getProducerApplications(
        status: String = "Pending"
    ): Response<
            ApiResponse<
                    List<AdminProducerApplicationResponse>
                    >
            > {

        return adminApiService
            .getProducerApplications(
                status = status
            )
    }

    /*
     * Üretici başvurusunu onaylar.
     */
    suspend fun approveProducerApplication(
        producerProfileId: Int
    ): Response<ApiResponse<Any?>> {

        return adminApiService
            .approveProducerApplication(
                producerProfileId =
                    producerProfileId
            )
    }

    /*
     * Üretici başvurusunu reddeder.
     */
    suspend fun rejectProducerApplication(
        producerProfileId: Int,
        reason: String
    ): Response<ApiResponse<Any?>> {

        return adminApiService
            .rejectProducerApplication(
                producerProfileId =
                    producerProfileId,

                request =
                    RejectProducerApplicationRequest(
                        reason =
                            reason.trim()
                    )
            )
    }

    /*
     * Kullanıcı listesini getirir.
     */
    suspend fun getUsers(
        role: String? = null,
        isActive: Boolean? = null,
        search: String? = null
    ): Response<
            ApiResponse<
                    List<AdminUserListItemResponse>
                    >
            > {

        return adminApiService
            .getUsers(
                role =
                    role
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        },

                isActive =
                    isActive,

                search =
                    search
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        }
            )
    }

    /*
     * Kullanıcı detayını getirir.
     *
     * token parametresi şimdilik korunuyor.
     * İlgili ViewModel temizlendiğinde
     * bu parametre de kaldırılacak.
     */
    suspend fun getUserById(
        userId: Int
    ): Response<
            ApiResponse<
                    AdminUserDetailResponse
                    >
            > {
        return adminApiService
            .getUserById(
                userId = userId
            )
    }

    /*
     * Kullanıcının aktif/pasif durumunu değiştirir.
     *
     * token parametresi geçici olarak korunuyor.
     */
    suspend fun updateUserStatus(
        userId: Int,
        isActive: Boolean
    ): Response<ApiResponse<Any?>> {

        return adminApiService
            .updateUserStatus(
                userId = userId,

                request =
                    UpdateUserStatusRequest(
                        isActive = isActive
                    )
            )
    }

    /*
     * Admin sipariş listesini getirir.
     */
    suspend fun getOrders(
        status: String? = null,
        customerId: Int? = null,
        producerProfileId: Int? = null,
        search: String? = null,
        dateFrom: String? = null,
        dateTo: String? = null
    ): Response<
            ApiResponse<
                    List<AdminOrderListItemResponse>
                    >
            > {

        return adminApiService
            .getOrders(
                status =
                    status
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        },

                customerId =
                    customerId,

                producerProfileId =
                    producerProfileId,

                search =
                    search
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        },

                dateFrom =
                    dateFrom
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        },

                dateTo =
                    dateTo
                        ?.trim()
                        ?.takeIf {
                            it.isNotBlank()
                        }
            )
    }

    /*
     * Sipariş detayını getirir.
     *
     * İlgili ViewModel temizlenene kadar
     * token parametresi geçici olarak duruyor.
     */
    suspend fun getOrderById(
        orderId: Int
    ): Response<
            ApiResponse<
                    AdminOrderDetailResponse
                    >
            > {
        return adminApiService
            .getOrderById(
                orderId = orderId
            )
    }

    /*
     * Öneri algoritmasının admin analiz
     * verilerini getirir.
     *
     * token parametresi geçici olarak korunuyor.
     */
    suspend fun getRecommendationAnalytics():
            Response<
                    ApiResponse<
                            RecommendationPerformanceResponse
                            >
                    > {

        return adminApiService
            .getRecommendationAnalytics()
    }
}