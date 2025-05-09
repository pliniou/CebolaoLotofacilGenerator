@Entity(tableName = "jogos")
data class Jogo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "numeros") val numeros: List<Int>,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean = false,
    @ColumnInfo(name = "data_geracao") val dataGeracao: LocalDate = LocalDate.now()
)