package soup.nolan.data

import soup.nolan.model.CameraFilter
import soup.nolan.model.CameraFilter.*

interface CameraFilterRepository {

    fun getAllFilters(): List<CameraFilter>

    fun getCameraFilter(filterId: String): CameraFilter
}

class CameraFilterRepositoryImpl : CameraFilterRepository {

    private val list = listOf(
        OR,
        A01, A02, A03, A04, A05, A06, A07, A08, A09, A10,
        A11, A12, A13, A14, A15, A16, A17, A18, A19, A20,
        A21, A22, A23, A24, A25, A26
    )

    override fun getAllFilters(): List<CameraFilter> {
        return list
    }

    override fun getCameraFilter(filterId: String): CameraFilter {
        return list.firstOrNull { it.id == filterId } ?: Companion.default
    }
}
