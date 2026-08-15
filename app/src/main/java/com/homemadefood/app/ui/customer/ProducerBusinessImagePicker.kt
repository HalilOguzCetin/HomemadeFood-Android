package com.homemadefood.app.ui.customer

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.homemadefood.app.data.remote.ApiConfig

@Composable
fun ProducerBusinessImagePicker(
    selectedImageUri: String?,
    existingImageUrl: String?,
    isSubmitting: Boolean,
    onImageSelected: (String) -> Unit,
    onRemoveSelectedImage: () -> Unit,
    isRequired: Boolean = true,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .PickVisualMedia()
        ) { uri ->
            if (uri != null) {
                onImageSelected(
                    uri.toString()
                )
            }
        }

    fun openPhotoPicker() {
        photoPickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts
                    .PickVisualMedia
                    .ImageOnly
            )
        )
    }

    val existingResolvedUrl =
        ApiConfig.resolveMediaUrl(
            existingImageUrl
        )

    val displayModel =
        selectedImageUri
            ?.takeIf {
                it.isNotBlank()
            }
            ?: existingResolvedUrl

    Column(
        modifier =
            modifier.fillMaxWidth(),

        verticalArrangement =
            Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text =
                if (isRequired) {
                    "İşletme Vitrin Görseli *"
                } else {
                    "İşletme Vitrin Görseli"
                }
        )

        Text(
            text =
                "İşletmenizi veya hazırladığınız yemekleri temsil eden net bir görsel seçin."
        )

        if (displayModel == null) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(180.dp),

                shape =
                    RoundedCornerShape(16.dp),

                tonalElevation = 1.dp
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(20.dp),

                    contentAlignment =
                        Alignment.Center
                ) {
                    Column(
                        horizontalAlignment =
                            Alignment.CenterHorizontally,

                        verticalArrangement =
                            Arrangement.spacedBy(
                                10.dp
                            )
                    ) {
                        Text(
                            "Henüz işletme görseli seçilmedi"
                        )

                        OutlinedButton(
                            onClick =
                                ::openPhotoPicker,

                            enabled =
                                !isSubmitting
                        ) {
                            Text(
                                "İşletme Görseli Seç"
                            )
                        }
                    }
                }
            }
        } else {
            AsyncImage(
                model = displayModel,

                contentDescription =
                    "İşletme vitrin görseli",

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(
                            RoundedCornerShape(
                                16.dp
                            )
                        ),

                contentScale =
                    ContentScale.Crop
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.spacedBy(
                        10.dp
                    )
            ) {
                OutlinedButton(
                    onClick =
                        ::openPhotoPicker,

                    enabled =
                        !isSubmitting,

                    modifier =
                        Modifier.weight(1f)
                ) {
                    Text(
                        if (
                            selectedImageUri
                                .isNullOrBlank() &&
                            existingResolvedUrl != null
                        ) {
                            "Görseli Değiştir"
                        } else {
                            "Başka Görsel Seç"
                        }
                    )
                }

                if (
                    !selectedImageUri
                        .isNullOrBlank()
                ) {
                    TextButton(
                        onClick =
                            onRemoveSelectedImage,

                        enabled =
                            !isSubmitting,

                        modifier =
                            Modifier.weight(1f)
                    ) {
                        Text(
                            if (
                                existingResolvedUrl !=
                                null
                            ) {
                                "Yeni Seçimi İptal Et"
                            } else {
                                "Görseli Kaldır"
                            }
                        )
                    }
                }
            }

            if (
                selectedImageUri
                    .isNullOrBlank() &&
                existingResolvedUrl != null
            ) {
                Text(
                    "Mevcut işletme görseli kullanılacak. İsterseniz yeni bir görsel seçebilirsiniz."
                )
            }
        }
    }
}

@Composable
fun ProducerBusinessImagePreview(
    businessImageUrl: String?,
    modifier: Modifier = Modifier
) {
    val resolvedUrl =
        ApiConfig.resolveMediaUrl(
            businessImageUrl
        )

    if (resolvedUrl == null) {
        return
    }

    AsyncImage(
        model = resolvedUrl,

        contentDescription =
            "İşletme vitrin görseli",

        modifier =
            modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(
                    RoundedCornerShape(
                        16.dp
                    )
                ),

        contentScale =
            ContentScale.Crop
    )
}