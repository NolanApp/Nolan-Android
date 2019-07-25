package soup.nolan.core.detector

import soup.nolan.core.detector.model.RawImage

interface Detector {

    fun detect(image: RawImage)

    fun isInDetecting(): Boolean
}
