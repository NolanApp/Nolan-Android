package soup.nolan.core.detector

import soup.nolan.core.detector.input.RawImage

interface Detector {

    fun detect(image: RawImage)

    fun isInDetecting(): Boolean
}
