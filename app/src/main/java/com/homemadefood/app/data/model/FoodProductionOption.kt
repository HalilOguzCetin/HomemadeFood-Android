package com.homemadefood.app.data.model

data class FoodProductionOption(
    val backendValue: String,
    val displayName: String
)

object FoodProductionOptions {

    val unitTypes: List<FoodProductionOption> = listOf(
        FoodProductionOption("Portion", "Porsiyon"),
        FoodProductionOption("Tray", "Tepsi"),
        FoodProductionOption("Piece", "Adet"),
        FoodProductionOption("Kilogram", "Kilogram"),
        FoodProductionOption("Liter", "Litre"),
        FoodProductionOption("Package", "Paket"),
        FoodProductionOption("Other", "Diğer")
    )

    val allergens: List<FoodProductionOption> = listOf(
        FoodProductionOption("Gluten", "Gluten"),
        FoodProductionOption("Crustaceans", "Kabuklular"),
        FoodProductionOption("Eggs", "Yumurta"),
        FoodProductionOption("Fish", "Balık"),
        FoodProductionOption("Peanuts", "Yer fıstığı"),
        FoodProductionOption("Soybeans", "Soya"),
        FoodProductionOption("Milk", "Süt"),
        FoodProductionOption("Nuts", "Sert kabuklu yemişler"),
        FoodProductionOption("Celery", "Kereviz"),
        FoodProductionOption("Mustard", "Hardal"),
        FoodProductionOption("Sesame", "Susam"),
        FoodProductionOption("Sulphites", "Sülfitler"),
        FoodProductionOption("Lupin", "Acı bakla (Lupin)"),
        FoodProductionOption("Molluscs", "Yumuşakçalar")
    )

    fun unitDisplayName(value: String): String =
        unitTypes.firstOrNull {
            it.backendValue.equals(
                value,
                ignoreCase = true
            )
        }?.displayName ?: value

    fun allergenDisplayName(value: String): String =
        allergens.firstOrNull {
            it.backendValue.equals(
                value,
                ignoreCase = true
            )
        }?.displayName ?: value
}
